package iu.quaraseequi.erzhan.di

import iu.quaraseequi.erzhan.data.db.ErzhanDatabase
import iu.quaraseequi.erzhan.data.storage.ImageStorage
import iu.quaraseequi.erzhan.domain.interactors.images.ImagesInteractor
import iu.quaraseequi.erzhan.repositories.featureExtraction.FeatureExtractionRepository
import iu.quaraseequi.erzhan.repositories.featureExtraction.FeatureExtractionRepositoryImpl
import iu.quaraseequi.erzhan.repositories.imageSrorage.ImageStorageRepository
import iu.quaraseequi.erzhan.repositories.imageSrorage.ImageStorageRepositoryImpl
import iu.quaraseequi.erzhan.repositories.objectDetection.ObjectDetectionRepository
import iu.quaraseequi.erzhan.repositories.objectDetection.ObjectDetectionRepositoryImpl
import iu.quaraseequi.erzhan.tf.tflite.TFLiteObjectDetectionAPIModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val appModule = module {
    /** Data */
    single { ImageStorage(androidContext()) }

    /** DB */
    single { ErzhanDatabase.createInstance(androidContext()) }
    single { get<ErzhanDatabase>().imagesDao() }

    /** Repositories */
    single<ImageStorageRepository> { ImageStorageRepositoryImpl(get(), get()) }
    single<ObjectDetectionRepository> { ObjectDetectionRepositoryImpl(get()) }
    single<FeatureExtractionRepository> { FeatureExtractionRepositoryImpl(androidContext().assets) }

    /** Interactors */
    single { ImagesInteractor(get(), get(), get()) }

    /** TF Models */
    single {
        TFLiteObjectDetectionAPIModel.create(
            androidContext().assets,
            "detect.tflite",
            "file:///android_asset/labelmap.txt",
            300,
            true
        )
    }
}
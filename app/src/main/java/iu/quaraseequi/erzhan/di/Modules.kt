package iu.quaraseequi.erzhan.di

import iu.quaraseequi.erzhan.data.storage.ImageStorage
import iu.quaraseequi.erzhan.domain.interactors.images.ImagesInteractor
import iu.quaraseequi.erzhan.repositories.imageSrorage.ImageStorageRepository
import iu.quaraseequi.erzhan.repositories.imageSrorage.ImageStorageRepositoryImpl
import iu.quaraseequi.erzhan.repositories.objectDetection.ObjectDetectionRepository
import iu.quaraseequi.erzhan.repositories.objectDetection.ObjectDetectionRepositoryImpl
import iu.quaraseequi.erzhan.tf.tflite.TFLiteObjectDetectionAPIModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {
    /** Data */
    single { ImageStorage(androidContext()) }

    /** Repositories */
    single<ImageStorageRepository> { ImageStorageRepositoryImpl(get()) }
    single<ObjectDetectionRepository> { ObjectDetectionRepositoryImpl(get()) }

    /** Interactors */
    single { ImagesInteractor(get(), get()) }

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
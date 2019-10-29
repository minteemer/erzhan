package iu.quaraseequi.erzhan.di

import iu.quaraseequi.erzhan.data.storage.ImageStorage
import iu.quaraseequi.erzhan.domain.interactors.images.ImagesInteractor
import iu.quaraseequi.erzhan.repositories.imageSrorage.ImageStorageRepository
import iu.quaraseequi.erzhan.repositories.imageSrorage.ImageStorageRepositoryImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val appModule = module {
    /** Data */
    single { ImageStorage(androidContext()) }

    /** Repositories */
    single<ImageStorageRepository> { ImageStorageRepositoryImpl(get()) }

    /** Interactors */
    single { ImagesInteractor(get()) }

}
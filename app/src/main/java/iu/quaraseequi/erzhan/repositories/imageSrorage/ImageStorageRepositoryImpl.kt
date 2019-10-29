package iu.quaraseequi.erzhan.repositories.imageSrorage

import io.reactivex.Single
import iu.quaraseequi.erzhan.data.storage.ImageStorage
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage

class ImageStorageRepositoryImpl(
    private val imageStorage: ImageStorage
) : ImageStorageRepository {

    override fun getSavedImages(): Single<List<TargetImage>> =
        Single.fromCallable {
            imageStorage.getImagePathList()
                .map { TargetImage(it) }
        }

}
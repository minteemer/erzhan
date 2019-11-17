package iu.quaraseequi.erzhan.repositories.imageSrorage

import android.graphics.Bitmap
import io.reactivex.Completable
import io.reactivex.Single
import iu.quaraseequi.erzhan.data.db.dao.ImagesDao
import iu.quaraseequi.erzhan.data.db.models.TargetImageModel
import iu.quaraseequi.erzhan.data.storage.ImageStorage
import iu.quaraseequi.erzhan.data.toJson
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage

class ImageStorageRepositoryImpl(
    private val imageStorage: ImageStorage,
    private val imagesDao: ImagesDao
) : ImageStorageRepository {

    override fun getSavedImages(): Single<List<TargetImage>> =
        imagesDao.getAllImages().map { images ->
            images.map { it.toEntity() }
        }

    override fun saveImage(image: Bitmap, features: List<DoubleArray>): Completable =
        imageStorage.saveImage(image, System.currentTimeMillis().toString())
            .flatMap {
                imagesDao.saveImage(
                    TargetImageModel(
                        imagePath = it.absolutePath,
                        descriptor = features.toJson()
                    )
                )
            }
            .ignoreElement()

    override fun removeImage(imageId: Long): Completable =
        imagesDao.removeImage(imageId)
            .andThen(imageStorage.removeImage(imageId.toString()))
}
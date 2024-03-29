package iu.quaraseequi.erzhan.repositories.imageSrorage

import android.graphics.Bitmap
import io.reactivex.Completable
import io.reactivex.Single
import iu.quaraseequi.erzhan.data.db.dao.ImagesDao
import iu.quaraseequi.erzhan.data.db.models.TargetImageModel
import iu.quaraseequi.erzhan.data.storage.ImageStorage
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage
import kotlin.random.Random

class ImageStorageRepositoryImpl(
    private val imageStorage: ImageStorage,
    private val imagesDao: ImagesDao
) : ImageStorageRepository {


    override fun getSavedImages(): Single<List<TargetImage>> =
        imagesDao.getAllImages().map { images ->
            images.map { it.toEntity() }
        }

    override fun saveImage(image: Bitmap, descriptor: FloatArray): Completable =
        imageStorage.saveImage(image, "${System.currentTimeMillis()}-${Random.nextInt()}")
            .flatMap {
                imagesDao.saveImage(TargetImageModel(TargetImage(0, it.absolutePath, descriptor)))
            }
            .ignoreElement()

    override fun removeImage(imageId: Long): Completable =
        imagesDao.removeImage(imageId)
            .andThen(imageStorage.removeImage(imageId.toString()))
}
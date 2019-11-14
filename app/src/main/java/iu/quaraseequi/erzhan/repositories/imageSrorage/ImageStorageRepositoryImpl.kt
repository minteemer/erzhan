package iu.quaraseequi.erzhan.repositories.imageSrorage

import android.graphics.Bitmap
import io.reactivex.Completable
import io.reactivex.Single
import iu.quaraseequi.erzhan.data.storage.ImageStorage
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage
import java.io.File

class ImageStorageRepositoryImpl(
    private val imageStorage: ImageStorage
) : ImageStorageRepository {

    override fun getSavedImages(): Single<List<TargetImage>> =
        Single.fromCallable {
            imageStorage.getImagePathList()
                .map {
                    TargetImage(
                        File(it).nameWithoutExtension.toLong(),
                        it
                    )
                }
        }

    override fun saveImage(image: Bitmap, imageId: Long) =
        imageStorage.saveImage(image, imageId.toString())

    override fun removeImage(imageId: Long): Completable = Completable.fromCallable{
        imageStorage.removeImage(imageId.toString())
    }
}
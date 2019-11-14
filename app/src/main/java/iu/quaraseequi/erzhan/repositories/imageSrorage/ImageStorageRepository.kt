package iu.quaraseequi.erzhan.repositories.imageSrorage

import android.graphics.Bitmap
import android.media.Image
import io.reactivex.Completable
import io.reactivex.Single
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage
import java.io.File

interface ImageStorageRepository {
    fun getSavedImages(): Single<List<TargetImage>>

    fun saveImage(image: Bitmap, imageId: Long)

    fun removeImage(imageId: Long): Completable
}
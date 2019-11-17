package iu.quaraseequi.erzhan.repositories.imageSrorage

import android.graphics.Bitmap
import io.reactivex.Completable
import io.reactivex.Single
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage

interface ImageStorageRepository {
    fun getSavedImages(): Single<List<TargetImage>>

    fun saveImage(image: Bitmap, features: List<FloatArray>): Completable

    fun removeImage(imageId: Long): Completable
}
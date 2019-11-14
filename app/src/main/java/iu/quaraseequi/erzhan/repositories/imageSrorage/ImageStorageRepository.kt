package iu.quaraseequi.erzhan.repositories.imageSrorage

import android.media.Image
import io.reactivex.Single
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage
import java.io.File

interface ImageStorageRepository {
    fun getSavedImages(): Single<List<TargetImage>>

    fun saveImage(image: Image, imageName: String): File
}
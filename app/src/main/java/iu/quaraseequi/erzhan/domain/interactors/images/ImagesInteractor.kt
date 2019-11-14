package iu.quaraseequi.erzhan.domain.interactors.images

import android.media.Image
import io.reactivex.Completable
import io.reactivex.Single
import iu.quaraseequi.erzhan.data.YUItoRGBBitmap
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage
import iu.quaraseequi.erzhan.repositories.imageSrorage.ImageStorageRepository
import iu.quaraseequi.erzhan.repositories.objectDetection.ObjectDetectionRepository


class ImagesInteractor(
    private val imageStorageRepository: ImageStorageRepository,
    private val objectDetectionRepository: ObjectDetectionRepository
) {

    fun getSavedImages(): Single<List<TargetImage>> = imageStorageRepository.getSavedImages()


    fun saveImage(cameraImage: Image): Completable = Completable.fromAction {
        val imageId = System.currentTimeMillis()
        val rgbBitmap = cameraImage.YUItoRGBBitmap()

        imageStorageRepository.saveImage(rgbBitmap, imageId)

//        val image = rgbBitmap.toMat()
//
//        objectDetectionRepository.detectObjects(rgbBitmap).asSequence()
//            .filter { it.confidence > 0.5 }
//            .map { image.cropRect(it.location) }
//            .forEachIndexed { i, mat ->
//                val bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
//                Utils.matToBitmap(mat, bmp)
//                imageStorageRepository.saveImage(bmp, i.toLong())
//            }
    }

    fun removeImage(imageId: Long) = imageStorageRepository.removeImage(imageId)

}
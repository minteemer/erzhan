package iu.quaraseequi.erzhan.domain.interactors.images

import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import io.reactivex.Completable
import io.reactivex.Single
import iu.quaraseequi.erzhan.data.YUItoRGBBitmap
import iu.quaraseequi.erzhan.data.cropRect
import iu.quaraseequi.erzhan.data.toMat
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage
import iu.quaraseequi.erzhan.repositories.featureExtraction.FeatureExtractionRepository
import iu.quaraseequi.erzhan.repositories.imageSrorage.ImageStorageRepository
import iu.quaraseequi.erzhan.repositories.objectDetection.ObjectDetectionRepository
import org.opencv.android.Utils


class ImagesInteractor(
    private val imageStorageRepository: ImageStorageRepository,
    private val objectDetectionRepository: ObjectDetectionRepository,
    private val featureExtractionRepository: FeatureExtractionRepository
) {

    fun getSavedImages(): Single<List<TargetImage>> = imageStorageRepository.getSavedImages()


    fun saveImage(cameraImage: Image): Completable = Completable.fromAction {
        val imageId = System.currentTimeMillis()
        val rgbBitmap = cameraImage.YUItoRGBBitmap()

        imageStorageRepository.saveImage(rgbBitmap, imageId)

        val image = rgbBitmap.toMat()

        objectDetectionRepository.detectObjects(rgbBitmap).asSequence()
            .filter { it.confidence > 0.5 }
            .map { image.cropRect(it.location) }
            .forEachIndexed { i, mat ->
                val bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
                Utils.matToBitmap(mat, bmp)
                imageStorageRepository.saveImage(bmp, System.currentTimeMillis())

                Log.d("Features", featureExtractionRepository.getFeatures(mat).toString())
            }
    }

    fun removeImage(imageId: Long) = imageStorageRepository.removeImage(imageId)

}
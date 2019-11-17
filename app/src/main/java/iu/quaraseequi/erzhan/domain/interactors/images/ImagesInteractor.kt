package iu.quaraseequi.erzhan.domain.interactors.images

import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import io.reactivex.Completable
import io.reactivex.Single
import iu.quaraseequi.erzhan.data.YUItoRGBBitmap
import iu.quaraseequi.erzhan.data.cropRect
import iu.quaraseequi.erzhan.data.toMat
import iu.quaraseequi.erzhan.data.transform
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage
import iu.quaraseequi.erzhan.repositories.featureExtraction.FeatureExtractionRepository
import iu.quaraseequi.erzhan.repositories.imageSrorage.ImageStorageRepository
import iu.quaraseequi.erzhan.repositories.objectDetection.ObjectDetectionRepository
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc


class ImagesInteractor(
    private val imageStorageRepository: ImageStorageRepository,
    private val objectDetectionRepository: ObjectDetectionRepository,
    private val featureExtractionRepository: FeatureExtractionRepository
) {

    fun getSavedImages(): Single<List<TargetImage>> = imageStorageRepository.getSavedImages()


    fun saveImage(cameraImage: Image): Completable = Completable.fromAction {
        val imageId = System.currentTimeMillis()
        val rgbBitmap = cameraImage.YUItoRGBBitmap().transform(rotate = 90f)

        imageStorageRepository.saveImage(rgbBitmap, imageId)

        val image = rgbBitmap.toMat()

        objectDetectionRepository.detectObjects(rgbBitmap).asSequence()
            .filter { it.confidence >= 0.5 }
            .also { recognitions ->
                recognitions.forEach {
                    Log.d("Recognition", "${it.title} - ${it.location} (${it.confidence})")
                }
            }
            .map { image.cropRect(it.location) }
            .forEachIndexed { i, mat ->
                val resizeimage = Mat()
                val sz = Size(256.0, 256.0)
                Imgproc.resize(mat, resizeimage, sz)
                val bmp = Bitmap.createBitmap(
                    resizeimage.cols(),
                    resizeimage.rows(),
                    Bitmap.Config.ARGB_8888
                )
                Utils.matToBitmap(resizeimage, bmp)

                val feature = featureExtractionRepository.getFeatures(bmp)
            }
    }

    fun removeImage(imageId: Long) = imageStorageRepository.removeImage(imageId)

}
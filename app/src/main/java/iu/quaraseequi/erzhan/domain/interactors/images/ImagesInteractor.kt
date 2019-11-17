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


    fun saveImage(cameraImage: Image): Completable = Completable.defer {
        val rgbBitmap = cameraImage.YUItoRGBBitmap().transform(rotate = 90f)

        val image = rgbBitmap.toMat()
        val features = objectDetectionRepository.detectObjects(rgbBitmap).asSequence()
            .filter { it.confidence >= 0.5 }
            .also { recognitions ->
                recognitions.forEach {
                    Log.d("Recognition", "${it.title} - ${it.location} (${it.confidence})")
                }
            }
            .map { image.cropRect(it.location) }
            .map { objectMat ->
                val resizedImage = Mat().also{
                    Imgproc.resize(objectMat, it, Size(256.0, 256.0))
                }
                val bmp = Bitmap.createBitmap(
                    resizedImage.cols(),
                    resizedImage.rows(),
                    Bitmap.Config.ARGB_8888
                )
                Utils.matToBitmap(resizedImage, bmp)

                featureExtractionRepository.getFeatures(bmp)
            }
            .toList()

        imageStorageRepository.saveImage(rgbBitmap, features)
    }

    fun removeImage(imageId: Long) = imageStorageRepository.removeImage(imageId)

}
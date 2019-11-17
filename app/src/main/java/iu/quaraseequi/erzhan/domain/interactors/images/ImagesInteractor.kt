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
import kotlin.math.pow
import kotlin.math.sqrt


class ImagesInteractor(
    private val imageStorageRepository: ImageStorageRepository,
    private val objectDetectionRepository: ObjectDetectionRepository,
    private val featureExtractionRepository: FeatureExtractionRepository
) {

    companion object {
        private const val DISTANCE_THRESHOLD = 1.25
    }

    fun getSavedImages(): Single<List<TargetImage>> = imageStorageRepository.getSavedImages()


    fun saveImage(image: Image): Completable = Completable.defer {
        val rgbBitmap = image.YUItoRGBBitmap().transform(rotate = 90f)
        val features = extractFeatures(rgbBitmap)
        imageStorageRepository.saveImage(rgbBitmap, features)
    }

    fun checkImage(image: Image): Single<Boolean> = Single.defer {
        val rgbBitmap = image.YUItoRGBBitmap().transform(rotate = 90f)
        val features = extractFeatures(rgbBitmap)

        imageStorageRepository.getSavedImages()
            .map { images ->
                logDistances(features, images)

                images.any { savedImage ->
                    savedImage.descriptor.any { savedImageFeature ->
                        features.any {
                            l2Distance(it, savedImageFeature) < DISTANCE_THRESHOLD
                        }
                    }
                }
            }
    }

    fun removeImage(imageId: Long) = imageStorageRepository.removeImage(imageId)

    private fun extractFeatures(rgbBitmap: Bitmap): List<FloatArray> {
        val image = rgbBitmap.toMat()
        return objectDetectionRepository.detectObjects(rgbBitmap).asSequence()
            .filter { it.confidence >= 0.5 }
            .also { recognitions ->
                recognitions.forEach {
                    Log.d("Recognition", "${it.title} - ${it.location} (${it.confidence})")
                }
            }
            .map { image.cropRect(it.location) }
            .map { objectMat ->
                val resizedImage = Mat().also {
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
    }

    private fun l2Distance(vec1: FloatArray, vec2: FloatArray): Double {
        assert(vec1.size == vec2.size) { "Vector lengths must be matching" }

        return vec1.indices.asSequence()
            .map { i ->
                (vec1[i].toDouble() - vec2[i].toDouble()).pow(2)
            }
            .sum()
            .let { sqrt(it) }
    }

    fun cosineSimilarity(vec1: FloatArray, vec2: FloatArray): Double {
        var dotProduct = 0.0
        var normA = 0.0
        var normB = 0.0
        for (i in vec1.indices) {
            dotProduct += vec1[i] * vec2[i]
            normA += vec1[i].pow(2.0f)
            normB += vec2[i].pow(2.0f)
        }
        return dotProduct / (sqrt(normA) * sqrt(normB))
    }

    private fun logDistances(features: List<FloatArray>, images: List<TargetImage>) {
        if (features.isNotEmpty()){
            images.forEach { img ->
                img.descriptor.forEach { imgFeature ->
                    Log.d(
                        "FeatureMatching",
                        "${img.id} L2: ${features.map {
                            l2Distance(
                                it,
                                imgFeature
                            )
                        }.joinToString()}"
                    )
                    Log.d(
                        "FeatureMatching",
                        "${img.id} Cos: ${features.map {
                            cosineSimilarity(
                                it,
                                imgFeature
                            )
                        }.joinToString()}"
                    )
                }
            }
        }
    }
}
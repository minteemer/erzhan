package iu.quaraseequi.erzhan.repositories.objectDetection

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import iu.quaraseequi.erzhan.tf.env.ImageUtils
import iu.quaraseequi.erzhan.tf.tflite.Classifier

class ObjectDetectionRepositoryImpl(
    private val detector: Classifier
) : ObjectDetectionRepository {

    companion object {
        const val CROP_SIZE = 300

        private val frameToCropTransform by lazy {
            ImageUtils.getTransformationMatrix(
                CROP_SIZE, CROP_SIZE,
                CROP_SIZE, CROP_SIZE,
                90, false
            )
        }

        private val cropToFrameTransform
                by lazy { Matrix().also { frameToCropTransform.invert(it) } }
    }

    override fun detectObjects(image: Bitmap): List<Classifier.Recognition> =
        detector.recognizeImage(image.cropToSize()).apply {
            forEach {
                val location = it.location
                cropToFrameTransform.mapRect(it.location)
                it.location = RectF(
                    location.left / CROP_SIZE, location.top / CROP_SIZE,
                    location.right / CROP_SIZE, location.bottom / CROP_SIZE
                )
            }
        }

    private fun Bitmap.cropToSize(): Bitmap {
        val croppedBitmap = Bitmap.createBitmap(CROP_SIZE, CROP_SIZE, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(croppedBitmap)
        canvas.drawBitmap(this, frameToCropTransform, null)

        return croppedBitmap
    }
}
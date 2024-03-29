package iu.quaraseequi.erzhan.repositories.objectDetection

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.RectF
import iu.quaraseequi.erzhan.tf.env.ImageUtils
import iu.quaraseequi.erzhan.tf.tflite.Classifier
import iu.quaraseequi.erzhan.tf.tflite.TFLiteObjectDetectionAPIModel

class ObjectDetectionRepositoryImpl(
    assetManager: AssetManager
) : ObjectDetectionRepository {

    companion object {
        private const val CROP_SIZE = 300
        private const val MODEL_FILE = "detect.tflite"
        private const val LABEL_MAP_FILE = "file:///android_asset/labelmap.txt"

    }

    private val detector: Classifier by lazy {
        TFLiteObjectDetectionAPIModel.create(
            assetManager,
            MODEL_FILE,
            LABEL_MAP_FILE,
            CROP_SIZE,
            true
        )
    }

    override fun detectObjects(image: Bitmap): List<Classifier.Recognition> {
        val frameToCropTransform by lazy {
            ImageUtils.getTransformationMatrix(
                image.width, image.height,
                CROP_SIZE, CROP_SIZE,
                0, false
            )
        }

        val cropToFrameTransform = Matrix().also { frameToCropTransform.invert(it) }

        return detector.recognizeImage(image.applyTransform(frameToCropTransform)).apply {
            forEach {
                val location = it.location
                cropToFrameTransform.mapRect(it.location)
                it.location = RectF(
                    location.left / CROP_SIZE, location.top / CROP_SIZE,
                    location.right / CROP_SIZE, location.bottom / CROP_SIZE
                )
            }
        }
    }

    private fun Bitmap.applyTransform(transform: Matrix): Bitmap {
        val croppedBitmap = Bitmap.createBitmap(CROP_SIZE, CROP_SIZE, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(croppedBitmap)
        canvas.drawBitmap(this, transform, null)

        return croppedBitmap
    }
}
package iu.quaraseequi.erzhan.repositories.objectDetection

import android.media.Image
import android.util.Log
import iu.quaraseequi.erzhan.data.cropToSize
import iu.quaraseequi.erzhan.data.toYUItoRGBBitmap
import iu.quaraseequi.erzhan.tf.env.ImageUtils
import iu.quaraseequi.erzhan.tf.tflite.Classifier

class ObjectDetectionRepositoryImpl(
    private val detector: Classifier
) : ObjectDetectionRepository {

    companion object {
        const val CROP_SIZE = 300
    }

    override fun detectObjects(image: Image) {
        val rgbFrameBitmap = image.toYUItoRGBBitmap()
        val croppedBitmap = rgbFrameBitmap.cropToSize(CROP_SIZE)
        ImageUtils.saveBitmap(croppedBitmap)
        val results = detector.recognizeImage(croppedBitmap)
        Log.d("Detection", results.joinToString("\n") { "${it.title} (${it.location})" })
    }
}
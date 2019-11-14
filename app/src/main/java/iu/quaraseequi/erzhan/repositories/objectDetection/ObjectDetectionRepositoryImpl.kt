package iu.quaraseequi.erzhan.repositories.objectDetection

import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import iu.quaraseequi.erzhan.data.cropToSize
import iu.quaraseequi.erzhan.data.YUItoRGBBitmap
import iu.quaraseequi.erzhan.tf.env.ImageUtils
import iu.quaraseequi.erzhan.tf.tflite.Classifier

class ObjectDetectionRepositoryImpl(
    private val detector: Classifier
) : ObjectDetectionRepository {

    companion object {
        const val CROP_SIZE = 300
    }

    override fun detectObjects(image: Bitmap): List<Classifier.Recognition> {
        val croppedBitmap = image.cropToSize(CROP_SIZE)
        ImageUtils.saveBitmap(croppedBitmap)
        return detector.recognizeImage(croppedBitmap)
    }
}
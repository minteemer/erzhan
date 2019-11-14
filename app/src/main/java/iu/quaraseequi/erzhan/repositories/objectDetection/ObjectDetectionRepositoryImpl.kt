package iu.quaraseequi.erzhan.repositories.objectDetection

import android.graphics.Bitmap
import iu.quaraseequi.erzhan.data.cropToSize
import iu.quaraseequi.erzhan.tf.tflite.Classifier

class ObjectDetectionRepositoryImpl(
    private val detector: Classifier
) : ObjectDetectionRepository {

    companion object {
        const val CROP_SIZE = 300
    }

    override fun detectObjects(image: Bitmap): List<Classifier.Recognition> {
        return detector.recognizeImage(image.cropToSize(CROP_SIZE))
    }
}
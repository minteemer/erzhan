package iu.quaraseequi.erzhan.repositories.objectDetection

import android.graphics.Bitmap
import iu.quaraseequi.erzhan.tf.tflite.Classifier

interface ObjectDetectionRepository {

    fun detectObjects(image: Bitmap): List<Classifier.Recognition>

}
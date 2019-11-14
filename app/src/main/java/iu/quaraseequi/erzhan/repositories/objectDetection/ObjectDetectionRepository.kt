package iu.quaraseequi.erzhan.repositories.objectDetection

import android.media.Image

interface ObjectDetectionRepository {

    fun detectObjects(image: Image)

}
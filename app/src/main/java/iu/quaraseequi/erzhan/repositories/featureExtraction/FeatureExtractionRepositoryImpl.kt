package iu.quaraseequi.erzhan.repositories.featureExtraction

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel.MapMode.READ_ONLY


class FeatureExtractionRepositoryImpl(
    private val assetsManager: AssetManager
) : FeatureExtractionRepository {

    companion object {
        private const val MODEL_FILE = "feature_extraction_model.tflite"
        private const val INPUT_SIZE = 256
        private const val IMAGE_NORMALIZATION = 255.0f
        private const val NUM_BYTES_PER_CHANNEL = 4
    }

    val tflite: Interpreter by lazy {
        val fileDescriptor = assetsManager.openFd(MODEL_FILE)
        FileInputStream(fileDescriptor.fileDescriptor).channel
            .map(READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength)
            .let {
                @Suppress("DEPRECATION")
                Interpreter(it)
            }
    }


    override fun getFeatures(bitmap: Bitmap): FloatArray {
        val imgData =
            ByteBuffer.allocateDirect(1 * INPUT_SIZE * INPUT_SIZE * 3 * NUM_BYTES_PER_CHANNEL)
        imgData.order(ByteOrder.nativeOrder())
        val intValues = IntArray(INPUT_SIZE * INPUT_SIZE)
        val outputVector = Array(1) { Array(4) { Array(4) { FloatArray(1) } } }
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        imgData.rewind()
        for (i in 0 until INPUT_SIZE) {
            for (j in 0 until INPUT_SIZE) {
                val pixelValue = intValues[i * INPUT_SIZE + j]
                imgData.putFloat((pixelValue shr 16 and 0xFF) / IMAGE_NORMALIZATION)
                imgData.putFloat((pixelValue shr 8 and 0xFF) / IMAGE_NORMALIZATION)
                imgData.putFloat((pixelValue and 0xFF) / IMAGE_NORMALIZATION)
            }
        }

        tflite.run(imgData, outputVector)

        val vec = outputVector.map { it.map { it.map { it.toList() }.flatten() }.flatten() }.flatten()

        Log.d("FeatureExtractor", "Extracted vec: $vec")
        return vec.toFloatArray()
    }

}
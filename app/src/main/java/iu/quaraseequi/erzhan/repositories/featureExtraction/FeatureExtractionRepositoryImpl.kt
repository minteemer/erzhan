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

    var modelFile = "feature_extraction_model.tflite"
    private val inputSize = 256
    private val IMAGE_MEAN = 128.0f
    private val IMAGE_STD = 128.0f
    private val numBytesPerChannel = 4

    val tflite: Interpreter by lazy { Interpreter(loadModelFile(modelFile)) }

    private fun loadModelFile(MODEL_FILE: String): ByteBuffer {
        val fileDescriptor = assetsManager.openFd(MODEL_FILE)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(READ_ONLY, startOffset, declaredLength)
    }

    override fun getFeatures(bitmap: Bitmap): DoubleArray {
        val imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * numBytesPerChannel)
        imgData.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputSize * inputSize)
        val outputVector = Array(1){Array(2){Array(2){FloatArray(1)} } }

        imgData.rewind()
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val pixelValue = intValues[i * inputSize + j]
                imgData.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                imgData.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                imgData.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
            }
        }

        tflite.run(imgData, outputVector)
        Log.d(
            "Tflite",
            outputVector.joinToString (prefix = "[", postfix = "]"){
                it.joinToString(prefix = "[", postfix = "]") {
                    it.joinToString(prefix = "[", postfix = "]") {
                        it.joinToString(prefix = "[", postfix = "]") {
                            it.toString()
                        }
                    }
                }
            }
        )

        return DoubleArray(100) { i -> i.toDouble() }
    }

}
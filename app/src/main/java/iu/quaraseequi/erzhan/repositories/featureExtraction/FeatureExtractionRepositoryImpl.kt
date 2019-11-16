package iu.quaraseequi.erzhan.repositories.featureExtraction

import android.content.res.AssetManager
import android.util.Log
import org.opencv.core.Mat
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel.MapMode.READ_ONLY


class FeatureExtractionRepositoryImpl(
    private val assetsManager: AssetManager
) : FeatureExtractionRepository {

    var modelFile = "xorGate.lite"

    val tflite: Interpreter by lazy { Interpreter(loadModelFile(modelFile)) }

    private fun loadModelFile(MODEL_FILE: String): ByteBuffer {
        val fileDescriptor = assetsManager.openFd(MODEL_FILE)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(READ_ONLY, startOffset, declaredLength)
    }

    override fun getFeatures(image: Mat): DoubleArray {
        val inp = arrayOf(floatArrayOf(0f, 0f))
        val out = arrayOf(floatArrayOf(0f))

        tflite.run(inp, out)
        Log.d("Tflite", out.joinToString { it.joinToString() })

        return DoubleArray(100) { i -> i.toDouble() }
    }

}
package iu.quaraseequi.erzhan.data.storage

import android.content.Context
import android.media.Image
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.lang.IllegalStateException
import android.R.attr.left
import android.R.attr.top
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import java.io.ByteArrayOutputStream


class ImageStorage(
    context: Context
) {

    companion object {
        private const val ALBUM_NAME = "saved_target_photos"
    }

    val albumDir = File(
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: throw IllegalStateException("External files dir does not exist"),
        ALBUM_NAME
    ).apply { mkdir() }

    /**
     * Save the image to storage
     * @param image Image object from OnImageAvailableListener
     * @param file File where image is going to be written
     * @return File object pointing to the file URI, null if the file already exists
     */
    fun saveImage(image: Image, imageName: String): File {
        val file = File(albumDir, imageName)

        val bytes = NV21toJPEG(YUV420toNV21(image), image.width, image.height, 100)
        val output = FileOutputStream(file)
        output.write(bytes)
        image.close()
        output.close()
        return file
    }


    fun getImagePathList(): List<String> =
        albumDir.listFiles()
            ?.map { it.absolutePath }
            ?: emptyList()

    private fun NV21toJPEG(nv21: ByteArray, width: Int, height: Int, quality: Int): ByteArray {
        val out = ByteArrayOutputStream()
        val yuv = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        yuv.compressToJpeg(Rect(0, 0, width, height), quality, out)
        return out.toByteArray()
    }

    private fun YUV420toNV21(image: Image): ByteArray {
        val crop = image.cropRect
        val format = image.format
        val width = crop.width()
        val height = crop.height()
        val planes = image.planes
        val data = ByteArray(width * height * ImageFormat.getBitsPerPixel(format) / 8)
        val rowData = ByteArray(planes[0].rowStride)

        var channelOffset = 0
        var outputStride = 1
        for (i in planes.indices) {
            when (i) {
                0 -> {
                    channelOffset = 0
                    outputStride = 1
                }
                1 -> {
                    channelOffset = width * height + 1
                    outputStride = 2
                }
                2 -> {
                    channelOffset = width * height
                    outputStride = 2
                }
            }

            val buffer = planes[i].buffer
            val rowStride = planes[i].rowStride
            val pixelStride = planes[i].pixelStride

            val shift = if (i == 0) 0 else 1
            val w = width shr shift
            val h = height shr shift
            buffer.position(rowStride * (crop.top shr shift) + pixelStride * (crop.left shr shift))
            for (row in 0 until h) {
                val length: Int
                if (pixelStride == 1 && outputStride == 1) {
                    length = w
                    buffer.get(data, channelOffset, length)
                    channelOffset += length
                } else {
                    length = (w - 1) * pixelStride + 1
                    buffer.get(rowData, 0, length)
                    for (col in 0 until w) {
                        data[channelOffset] = rowData[col * pixelStride]
                        channelOffset += outputStride
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length)
                }
            }
        }
        return data
    }
}
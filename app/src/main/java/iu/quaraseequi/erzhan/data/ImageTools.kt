package iu.quaraseequi.erzhan.data

import android.graphics.Bitmap
import android.graphics.RectF
import android.media.Image
import android.util.Log
import iu.quaraseequi.erzhan.tf.env.ImageUtils
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Rect

fun Image.YUItoRGBBitmap(): Bitmap {
    val planes = planes

    val yuvBytes = arrayOfNulls<ByteArray>(3)
    fillBytes(planes, yuvBytes)

    var rgbBytes = IntArray(width * height)

    ImageUtils.convertYUV420ToARGB8888(
        yuvBytes[0],
        yuvBytes[1],
        yuvBytes[2],
        width,
        height,
        planes[0].rowStride,
        planes[1].rowStride,
        planes[1].pixelStride,
        rgbBytes
    )

    val rgbFrameBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    rgbFrameBitmap.setPixels(rgbBytes, 0, width, 0, 0, width, height)

    close()
    return rgbFrameBitmap
}

fun Bitmap.toMat(): Mat =
    Mat().also {
        Utils.bitmapToMat(copy(Bitmap.Config.ARGB_8888, true), it)
    }

fun Mat.cropRect(rect: RectF): Mat {
    val x = (width() * (rect.centerX() - rect.width() / 2))
    val y = height() * (rect.centerY() - rect.height() / 2)
    val width = width() * rect.width()
    val height = height() * rect.height()

    Log.d("Crop", "$rect; x: $x, y: $y, width: $width, height: $height")

    return submat(Rect(x.toInt().coerceAtLeast(0), y.toInt().coerceAtLeast(0), width.toInt(), height.toInt()))
}


private fun fillBytes(planes: Array<Image.Plane>, yuvBytes: Array<ByteArray?>) {
    // Because of the variable row stride it's not possible to know in
    // advance the actual necessary dimensions of the yuv planes.
    for (i in planes.indices) {
        val buffer = planes[i].buffer
        if (yuvBytes[i] == null) {
            yuvBytes[i] = ByteArray(buffer.capacity())
        }
        buffer.get(yuvBytes[i])
    }
}

package iu.quaraseequi.erzhan.data

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.media.Image
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

fun Bitmap.transform(
    rotate: Float = 0f,
    scaleWidth: Int = width,
    scaleHeight: Int = height
): Bitmap {
    val matrix = Matrix()

    matrix.postRotate(rotate)

    val scaledBitmap = Bitmap.createScaledBitmap(this, scaleWidth, scaleHeight, true)

    return Bitmap.createBitmap(
        scaledBitmap,
        0,
        0,
        scaledBitmap.width,
        scaledBitmap.height,
        matrix,
        true
    )
}

fun Mat.cropRect(rect: RectF): Mat {
    val x = width() * rect.left.coerceAtLeast(0f)
    val y = height() * rect.top.coerceAtLeast(0f)
    val width = width() * rect.width().coerceAtMost(1f)
    val height = height() * rect.height().coerceAtMost(1f)

    // Log.d("Crop", "$rect; x: $x, y: $y, width: $width, height: $height")

    return submat(
        Rect(
            x.toInt(),
            y.toInt(),
            width.toInt(),
            height.toInt()
        )
    )
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

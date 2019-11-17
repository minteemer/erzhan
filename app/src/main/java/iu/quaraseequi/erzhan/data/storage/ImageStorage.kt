package iu.quaraseequi.erzhan.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import io.reactivex.Completable
import io.reactivex.Single
import iu.quaraseequi.erzhan.domain.entities.logger.log
import java.io.File
import java.io.FileOutputStream


class ImageStorage(
    context: Context
) {

    companion object {
        private const val ALBUM_NAME = "saved_target_photos"

        const val IMG_EXTENSION = ".png"
    }

    private val albumDir = File(
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
    fun saveImage(bitmap: Bitmap, imageName: String): Single<File> = Single.fromCallable {
        val file = File(albumDir, "$imageName$IMG_EXTENSION")

        if (file.exists())
            file.delete()
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 99, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.log("ImageStorage")
        }

        file
    }


    fun getImagePathList(): List<String> =
        albumDir.listFiles()
            ?.map { it.absolutePath }
            ?: emptyList()

    fun removeImage(imageName: String): Completable = Completable.fromCallable {
        File(albumDir, "$imageName$IMG_EXTENSION").delete()
    }

}
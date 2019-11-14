package iu.quaraseequi.erzhan.domain.interactors.images

import android.graphics.RectF
import android.media.Image
import io.reactivex.Single
import iu.quaraseequi.erzhan.data.storage.ImageStorage
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage
import iu.quaraseequi.erzhan.repositories.imageSrorage.ImageStorageRepository
import org.opencv.core.Rect
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File
import org.opencv.android.OpenCVLoader


class ImagesInteractor(
    private val imageStorageRepository: ImageStorageRepository,
    private val imageStorage: ImageStorage
) {

    fun getSavedImages(): Single<List<TargetImage>> = imageStorageRepository.getSavedImages()


    fun saveImage(saveImage: Image) {
        val file = imageStorageRepository.saveImage(saveImage, "${System.currentTimeMillis()}.jpg")
        val rectf = RectF(0.2f, 0.2f, 0.2f, 0.2f)
        OpenCVLoader.initDebug()
        val image = Imgcodecs.imread(file.absolutePath, Imgcodecs.IMREAD_COLOR)
        Imgproc.cvtColor(image, image, Imgproc.COLOR_RGB2GRAY, 4)
        val x = image.width() * rectf.left
        val y = image.height() * rectf.bottom
        val width = image.width() * (1 - rectf.right) - x
        val height = image.height() * (1 - rectf.top) - y

        val rect = Rect(x.toInt(), y.toInt(), width.toInt(), height.toInt())
        val cropped = image.submat(rect)
        Imgcodecs.imwrite(File(imageStorage.albumDir, "${System.currentTimeMillis()}.jpg").absolutePath, cropped)
    }

}
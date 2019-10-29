package iu.quaraseequi.erzhan.domain.interactors.images

import io.reactivex.Single
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage
import iu.quaraseequi.erzhan.repositories.imageSrorage.ImageStorageRepository

class ImagesInteractor(
    private val imageStorageRepository: ImageStorageRepository
) {

    fun getSavedImages(): Single<List<TargetImage>> = imageStorageRepository.getSavedImages()

}
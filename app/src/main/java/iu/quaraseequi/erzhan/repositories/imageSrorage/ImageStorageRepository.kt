package iu.quaraseequi.erzhan.repositories.imageSrorage

import io.reactivex.Single
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage

interface ImageStorageRepository {
    fun getSavedImages(): Single<List<TargetImage>>
}
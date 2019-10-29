package iu.quaraseequi.erzhan.presentation.main.presenter

import com.arellomobile.mvp.MvpView
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage

interface MainScreenView : MvpView {

    fun openAddPhotoScreen()

    fun showSavedImages(images: List<TargetImage>)

}
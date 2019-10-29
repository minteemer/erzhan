package iu.quaraseequi.erzhan.presentation.main.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.warefly.checkscan.presentation.BasePresenter

@InjectViewState
class MainScreenPresenter() : BasePresenter<MainScreenView>() {

    fun onAddPhotoClick() {
        viewState.openAddPhotoScreen()
    }

}
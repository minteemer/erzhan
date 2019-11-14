package iu.quaraseequi.erzhan.presentation.main.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.warefly.checkscan.presentation.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage
import iu.quaraseequi.erzhan.domain.entities.logger.log
import iu.quaraseequi.erzhan.domain.interactors.images.ImagesInteractor

@InjectViewState
class MainScreenPresenter(
    private val imagesInteractor: ImagesInteractor
) : BasePresenter<MainScreenView>() {

    companion object {
        private const val LOG_TAG = "MainScreenPresenter"
    }

    fun onAddPhotoClick() {
        viewState.openAddPhotoScreen()
    }

    fun onRefreshImages() {
        imagesInteractor.getSavedImages()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { viewState.showSavedImages(it) },
                { it.log(LOG_TAG, "Error while getting saved images") }
            )
            .disposeOnDestroy()
    }

    fun onRemoveImageClicked(image: TargetImage) {
        imagesInteractor.removeImage(image.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { onRefreshImages() },
                { it.log(LOG_TAG, "Error while removing image") }
            )
            .disposeOnTermination()
    }

}
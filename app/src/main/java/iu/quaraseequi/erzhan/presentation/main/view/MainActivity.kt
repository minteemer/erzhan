package iu.quaraseequi.erzhan.presentation.main.view

import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import iu.quaraseequi.erzhan.R
import iu.quaraseequi.erzhan.presentation.camera.view.CameraActivity
import iu.quaraseequi.erzhan.presentation.main.presenter.MainScreenPresenter
import iu.quaraseequi.erzhan.presentation.main.presenter.MainScreenView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : MvpAppCompatActivity(), MainScreenView {

    @InjectPresenter
    lateinit var presenter: MainScreenPresenter

    @ProvidePresenter
    fun providePresenter() = MainScreenPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_add_photo.setOnClickListener { presenter.onAddPhotoClick() }
    }

    override fun openAddPhotoScreen() {
        CameraActivity.start(this)
    }
}

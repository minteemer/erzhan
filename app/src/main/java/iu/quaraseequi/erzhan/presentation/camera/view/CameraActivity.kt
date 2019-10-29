package iu.quaraseequi.erzhan.presentation.camera.view

import android.content.Context
import android.content.Intent
import android.media.ImageReader
import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import iu.quaraseequi.erzhan.R
import iu.quaraseequi.erzhan.presentation.camera.presenter.CameraPresenter
import iu.quaraseequi.erzhan.presentation.camera.presenter.CameraView

class CameraActivity : MvpAppCompatActivity(), CameraView, ImageReader.OnImageAvailableListener {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, CameraActivity::class.java))
        }
    }

    @InjectPresenter
    lateinit var presenter: CameraPresenter

    @ProvidePresenter
    fun providePresenter() = CameraPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

    }

    override fun onImageAvailable(reader: ImageReader?) {

    }
}

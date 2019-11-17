package iu.quaraseequi.erzhan.presentation.main.view

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.bumptech.glide.Glide
import com.warefly.checkscan.presentation.base.view.recycler.SimpleRecyclerAdapter
import iu.quaraseequi.erzhan.R
import iu.quaraseequi.erzhan.domain.entities.images.TargetImage
import iu.quaraseequi.erzhan.presentation.main.presenter.MainScreenPresenter
import iu.quaraseequi.erzhan.presentation.main.presenter.MainScreenView
import iu.quaraseequi.erzhan.tf.CameraActivity
import iu.quaraseequi.erzhan.tf.DetectorActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_saved_photo.view.*
import org.koin.android.ext.android.get
import java.io.File

class MainActivity : MvpAppCompatActivity(), MainScreenView {

    @InjectPresenter
    lateinit var presenter: MainScreenPresenter

    @ProvidePresenter
    fun providePresenter() = MainScreenPresenter(get())

    private val imagesAdapter by lazy {
        SimpleRecyclerAdapter<TargetImage>(R.layout.item_saved_photo) { view, image ->
            Glide.with(this)
                .load(File(image.path))
                .into(view.iv_saved_photo)

            view.iv_remove_photo_btn.setOnClickListener {
                presenter.onRemoveImageClicked(image)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_add_photo.setOnClickListener { presenter.onAddPhotoClick() }
        btn_set_alarm_time.setOnClickListener { presenter.onSetAlarmTimeClick() }


        rv_saved_photos.layoutManager = GridLayoutManager(this, 3)
        rv_saved_photos.adapter = imagesAdapter
    }


    override fun onResume() {
        super.onResume()
        presenter.onRefreshImages()
    }

    override fun openAddPhotoScreen() {
        startActivity(Intent(this, DetectorActivity::class.java))
    }

    override fun showSavedImages(images: List<TargetImage>) {
        imagesAdapter.setItems(images)
    }

    override fun startAlarm() {
        Intent(this, DetectorActivity::class.java)
            .apply { putExtra(CameraActivity.DETECT_SAVED_IMAGE, true) }
            .let { startActivity(it) }
    }
}

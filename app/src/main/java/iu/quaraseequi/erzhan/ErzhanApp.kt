package iu.quaraseequi.erzhan


import androidx.multidex.MultiDexApplication
import androidx.appcompat.app.AppCompatDelegate

import com.arellomobile.mvp.MvpFacade
import com.facebook.stetho.Stetho
import iu.quaraseequi.erzhan.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level


class ErzhanApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            if (BuildConfig.DEBUG)
                androidLogger(Level.DEBUG)
            androidContext(this@ErzhanApp)
            modules(appModule)
        }

        MvpFacade.init()

        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

    }

}

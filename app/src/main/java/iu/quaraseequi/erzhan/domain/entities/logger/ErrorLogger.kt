package iu.quaraseequi.erzhan.domain.entities.logger

import android.util.Log
import iu.quaraseequi.erzhan.BuildConfig

object ErrorLogger {

    fun log(logTag: String, message: String? = null, throwable: Throwable) {
        if (BuildConfig.DEBUG)
            Log.e(logTag.take(23), message ?: "", throwable)
    }

}

fun Throwable.log(logTag: String, message: String? = null) = ErrorLogger.log(logTag, message, this)
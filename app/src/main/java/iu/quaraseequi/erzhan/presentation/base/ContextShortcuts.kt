package iu.quaraseequi.erzhan.presentation.base

import android.content.Context
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

fun Context.showShortToast(@StringRes resId: Int) =
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()

fun Context.showShortToast(text: CharSequence) =
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

fun Context.showLongToast(@StringRes resId: Int) =
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show()

fun Context.showLongToast(text: CharSequence) =
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()


fun Context.getColorCompat(@ColorRes id: Int) = ContextCompat.getColor(this, id)
fun Context.getDrawableCompat(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)
package com.warefly.checkscan.presentation

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePresenter<V : MvpView> : MvpPresenter<V>() {

    private val compositeDisposable = CompositeDisposable()
    private val taggedDisposablesOnDestroy = HashMap<String, Disposable>()
    private val taggedDisposablesOnTermination = HashMap<String, Disposable>()

    override fun onDestroy() {
        compositeDisposable.dispose()
        taggedDisposablesOnDestroy.forEach { (_, disposable) ->
            disposable.dispose()
        }
    }

    /**
     * Disposes this [Disposable] when onDestroy of this presenter is called.
     * If [tag] is specified, this [Disposable] will also be disposed if this method is called
     * on another disposable with the same [tag].
     */
    protected fun Disposable.disposeOnDestroy(tag: String? = null) {
        if (tag != null) {
            synchronized(this) {
                taggedDisposablesOnDestroy[tag]?.takeUnless { it.isDisposed }?.dispose()
                taggedDisposablesOnDestroy[tag] = this
            }
        } else {
            compositeDisposable.add(this)
        }
    }

    /**
     * Does not dispose this [Disposable] even when presenter dies, allowing the task to
     * be executed until it is finished or until the application is terminated.
     * If [tag] is specified, this [Disposable] will be disposed if this method is called
     * on another disposable with the same [tag].
     *
     * CAUTION: Use this method only when you are sure that the task will finish execution shortly,
     * usage of this method on tasks that never get completed will lead to memory leaks!
     */
    protected fun Disposable.disposeOnTermination(tag: String? = null) {
        if (tag != null) {
            synchronized(this) {
                taggedDisposablesOnDestroy[tag]?.takeUnless { it.isDisposed }?.dispose()
                taggedDisposablesOnDestroy[tag] = this
            }
        }
    }
}
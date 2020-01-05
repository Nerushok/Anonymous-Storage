package io.anonymous.storage.presentation.base

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import io.anonymous.storage.BuildConfig
import io.anonymous.storage.presentation.ui.dialog.LoadingDialog
import org.koin.core.KoinComponent

abstract class BaseActivity : AppCompatActivity(), KoinComponent {

    protected val LOG_TAG = this::class.java.canonicalName
    private val TAG_LOADING_DIALOG = "${BuildConfig.APPLICATION_ID}.LoadingDialog"

    protected fun observeLoading(loadingLiveData: LiveData<Boolean>) {
        loadingLiveData.observe(this, Observer { setLoading(it) })
    }

    protected fun observeError(errorLiveData: LiveData<Exception?>) {
        errorLiveData.observe(this, Observer { setError(it) })
    }

    protected open fun setLoading(isLoading: Boolean) {}

    protected open fun setError(error: Exception?) {}

    protected fun setLoadingDialogVisibility(isVisible: Boolean) {
        if (isVisible) showLoadingDialog() else hideLoadingDialog()
    }

    private fun showLoadingDialog() {
        LoadingDialog().show(supportFragmentManager, TAG_LOADING_DIALOG)
    }

    private fun hideLoadingDialog() {
        val loadingDialog = supportFragmentManager.findFragmentByTag(TAG_LOADING_DIALOG)

        if (loadingDialog != null && loadingDialog is LoadingDialog) {
            try {
                loadingDialog.dismissAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
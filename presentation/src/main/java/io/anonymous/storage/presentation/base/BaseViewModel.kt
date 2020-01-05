package io.anonymous.storage.presentation.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.anonymous.storage.presentation.utils.extentions.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel(), CoroutineScope {

    protected val LOG_TAG = this::class.java.canonicalName

    private val coroutineCancelJob = Job()
    override val coroutineContext: CoroutineContext
        get() = coroutineCancelJob + Dispatchers.Default

    protected val _loading = MutableLiveData(false)
    val loading = _loading.asLiveData()

    protected val _error = MutableLiveData<Exception?>()
    val error = _error.asLiveData()

    override fun onCleared() {
        try {
            coroutineCancelJob.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            super.onCleared()
        }
    }

    fun isLoading(): Boolean = loading.value ?: false

    protected fun setLoading(isLoading: Boolean) {
        _loading.value = isLoading
    }

    protected fun postLoading(isLoading: Boolean) {
        _loading.postValue(isLoading)
    }

    protected fun setError(exception: Exception?) {
        _error.value = exception
    }

    protected fun postError(exception: Exception?) {
        _error.postValue(exception)
    }
}
package io.anonymous.storage.presentation.utils

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class LiveEventCallback<T>(
    lifecycleOwner: LifecycleOwner,
    private val errorCallback: ((Throwable) -> Unit)? = null,
    private val callback: (T?) -> Unit
) {

    private val resultLiveData = MutableLiveData<T>()
    private val errorLiveData = MutableLiveData<Throwable>()

    init {
        resultLiveData.observe(lifecycleOwner, Observer { callback(it) })

        if (errorCallback != null) errorLiveData.observe(lifecycleOwner, Observer { errorCallback.invoke(it) })
    }

    @MainThread
    operator fun invoke(result: T?) {
        resultLiveData.value = result
    }

    fun post(result: T?) {
        resultLiveData.postValue(result)
    }

    @MainThread
    fun error(error: Throwable) {
        errorLiveData.value = error
    }

    fun postError(error: Throwable) {
        errorLiveData.postValue(error)
    }
}
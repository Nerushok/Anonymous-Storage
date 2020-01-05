package io.anonymous.storage.presentation.utils

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class LiveEventCallback<T>(lifecycleOwner: LifecycleOwner, private val callback: (T?) -> Unit) {

    private val resultLiveData = MutableLiveData<T>()

    init {
        resultLiveData.observe(lifecycleOwner, Observer { callback(it) })
    }

    @MainThread
    operator fun invoke(result: T?) {
        resultLiveData.value = result
    }

    fun post(result: T?) {
        resultLiveData.postValue(result)
    }
}
package io.anonymous.storage.presentation.utils.extentions

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

internal fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>

internal fun Context.toast(text: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, length).show()
}

internal fun Context.toast(@StringRes textRes: Int, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, textRes, length).show()
}
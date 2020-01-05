package io.anonymous.storage.data.utils

import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal suspend fun <T> Task<T>.getTaskResult(): T {
    return suspendCoroutine { continuation ->
        this.addOnSuccessListener { data -> continuation.resume(data) }
            .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
    }
}
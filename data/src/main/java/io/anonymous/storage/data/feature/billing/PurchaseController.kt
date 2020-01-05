package io.anonymous.storage.data.feature.billing

import android.app.Activity
import io.anonymous.storage.domain.common.model.DocumentKey

interface PurchaseController {

    suspend fun init(): Boolean

    fun buyDocumentLifetime(activity: Activity, documentKey: DocumentKey, purchaseCallback: PurchaseCallback)


    interface PurchaseCallback {

        fun onError(errorCode: Int)
    }
}
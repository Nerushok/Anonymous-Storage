package io.anonymous.storage.data.feature.billing

import android.app.Activity
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import io.anonymous.storage.domain.common.model.DocumentKey

interface PurchaseController {

    suspend fun init(): Boolean

    suspend fun setPurchaseCallback(purchaseCallback: PurchaseCallback?)

    suspend fun buyDocumentLifetime(activity: Activity, documentKey: DocumentKey, skuDetails: SkuDetails)

    suspend fun getDocumentLifetimeSkuDetails(): SkuDetails?


    interface PurchaseCallback {

        fun onPurchased(purchase: Purchase)

        fun onError(errorCode: Int)
    }
}
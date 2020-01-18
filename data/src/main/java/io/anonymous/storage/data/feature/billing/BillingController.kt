package io.anonymous.storage.data.feature.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.Purchase.PurchaseState
import io.anonymous.storage.domain.common.model.DocumentKey
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.android.billingclient.api.BillingClient.BillingResponseCode as ResponseCodes

class BillingController(
    private val context: Context
) : PurchaseController, PurchasesUpdatedListener {

    private val LOG_TAG = "BillingController"
    private val PRODUCT_ID_DOCUMENT_YEAR_LIFETIME = "document_year_lifetime_1024"

    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build()
    }
    private var purchaseCallback: PurchaseController.PurchaseCallback? = null
    private var isBillingClientConnected = false

    override suspend fun init(): Boolean {
        return connectBillingClient()
    }

    override suspend fun setPurchaseCallback(purchaseCallback: PurchaseController.PurchaseCallback?) {
        this.purchaseCallback = purchaseCallback
    }

    override suspend fun buyDocumentLifetime(
        activity: Activity,
        documentKey: DocumentKey,
        skuDetails: SkuDetails
    ) {
        val billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
        val responseCode = billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    override suspend fun getDocumentLifetimeSkuDetails(): SkuDetails? {
        return getSkuDetails().firstOrNull { details -> details.sku == PRODUCT_ID_DOCUMENT_YEAR_LIFETIME }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode == ResponseCodes.OK && !purchases.isNullOrEmpty()) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else purchaseCallback?.onError(result.responseCode)
    }

    private suspend fun connectBillingClient(): Boolean {
        if (isBillingClientConnected && billingClient.isReady) return true

        return suspendCoroutine { continuation ->
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult?) {
                    val responseCode = billingResult?.responseCode ?: ResponseCodes.SERVICE_UNAVAILABLE

                    Log.d(LOG_TAG, "onBillingSetupFinished - code: ${responseCode}, message: ${billingResult?.debugMessage}")

                    try {
                        if (responseCode == ResponseCodes.OK) {
                            isBillingClientConnected = true
                            continuation.resume(true)
                        } else {
                            isBillingClientConnected = false
                            continuation.resume(false)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.d(LOG_TAG, "connectBillingClient.onBillingServiceDisconnected")

                    isBillingClientConnected = false
                    try {
                        continuation.resume(false)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
        }
    }

    private suspend fun getSkuDetails(): List<SkuDetails> {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(listOf(PRODUCT_ID_DOCUMENT_YEAR_LIFETIME))
            .setType(BillingClient.SkuType.INAPP)

        return suspendCoroutine { continuation ->
            billingClient.querySkuDetailsAsync(params.build()) { result, details ->
                if (result != null && details != null && result.responseCode == ResponseCodes.OK) {
                    continuation.resume(details)
                } else {
                    continuation.resumeWithException(RuntimeException("querySkuDetailsAsync - code: ${result?.responseCode}, message: ${result?.debugMessage}"))
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        when (purchase.purchaseState) {
            PurchaseState.PURCHASED -> {
                // TODO: validate purchase on backend and call billingClient.acknowledge()
            }
            PurchaseState.PENDING -> {
                // TODO: handle pending transaction
            }
            PurchaseState.UNSPECIFIED_STATE -> { /* Unspecified situation */  }
        }
    }
}
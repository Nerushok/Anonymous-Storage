package io.anonymous.storage.data.feature.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import io.anonymous.storage.domain.common.model.DocumentKey
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BillingController(private val context: Context) : PurchaseController, PurchasesUpdatedListener {

    private val LOG_TAG = "BillingController"

    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(context).setListener(this).build()
    }
    private var isBillingClientConnected = false

    override suspend fun init(): Boolean {
        return connectBillingClient()
    }

    override fun buyDocumentLifetime(
        activity: Activity,
        documentKey: DocumentKey,
        purchaseCallback: PurchaseController.PurchaseCallback
    ) {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPurchasesUpdated(p0: BillingResult?, p1: MutableList<Purchase>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private suspend fun connectBillingClient(): Boolean {
        if (isBillingClientConnected && billingClient.isReady) return true

        return suspendCoroutine { continuation ->
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult?) {
                    val responseCode = billingResult?.responseCode ?: BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE

                    Log.d(LOG_TAG, "onBillingSetupFinished - code: ${responseCode}, message: ${billingResult?.debugMessage}")

                    try {
                        if (responseCode == BillingClient.BillingResponseCode.OK) {
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
}
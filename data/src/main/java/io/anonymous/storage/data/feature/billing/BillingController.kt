package io.anonymous.storage.data.feature.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.android.billingclient.api.Purchase.PurchaseState
import io.anonymous.storage.domain.common.model.DocumentKey
import io.anonymous.storage.domain.extentions.execute
import io.anonymous.storage.domain.feature.purchase.PurchaseRepository
import io.anonymous.storage.domain.feature.purchase.ValidatePurchaseUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.android.billingclient.api.BillingClient.BillingResponseCode as ResponseCodes

class BillingController(
    private val context: Context,
    private val validatePurchaseUseCase: ValidatePurchaseUseCase
) : PurchaseController, PurchasesUpdatedListener, CoroutineScope {

    private val LOG_TAG = "BillingController"

    private val PRODUCT_ID_DOCUMENT_YEAR_LIFETIME = "document_year_lifetime_1024"

    override val coroutineContext: CoroutineContext = Dispatchers.Default + Job()

    private val billingClient: BillingClient by lazy {
        BillingClient.newBuilder(context).enablePendingPurchases().setListener(this).build()
    }
    private var purchaseCallback: PurchaseController.PurchaseCallback? = null
    private var isBillingClientConnected = false

    private var currentDocumentKey: DocumentKey? = null
    private var currentSkuDetails: SkuDetails? = null

    override suspend fun init(): Boolean {
        return connectBillingClient()
    }

    override fun setPurchaseCallback(purchaseCallback: PurchaseController.PurchaseCallback?) {
        this.purchaseCallback = purchaseCallback
    }

    override suspend fun buyDocumentLifetime(
        activity: Activity,
        documentKey: DocumentKey,
        skuDetails: SkuDetails
    ) {
        Log.d(LOG_TAG, "buyDocumentLifetime")

        if (currentDocumentKey != null || currentSkuDetails != null) {
            purchaseCallback?.onError(ResponseCodes.BILLING_UNAVAILABLE)
            return
        }

        currentDocumentKey = documentKey
        currentSkuDetails = skuDetails

        val billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
        val result = billingClient.launchBillingFlow(activity, billingFlowParams)
        Log.d(LOG_TAG, "buyDocumentLifetime. ${billingResultLogMessage(result)}")

        if (result.responseCode == ResponseCodes.OK) return
        else if (result.responseCode == ResponseCodes.ITEM_ALREADY_OWNED) {
            handlePendingPurchases()
        } else {
            resetCurrentPurchase()
            purchaseCallback?.onError(result.responseCode)
        }
    }

    override suspend fun getDocumentLifetimeSkuDetails(): SkuDetails? {
        return getSkuDetails().firstOrNull { details -> details.sku == PRODUCT_ID_DOCUMENT_YEAR_LIFETIME }
            .apply { Log.d(LOG_TAG, "getDocumentLifetimeSkuDetails - $this") }
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        Log.d(LOG_TAG, "onPurchasesUpdated. ${billingResultLogMessage(result)}")

        if (!purchases.isNullOrEmpty() && (result.responseCode == ResponseCodes.OK || result.responseCode == ResponseCodes.ITEM_ALREADY_OWNED)) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else {
            resetCurrentPurchase()
            purchaseCallback?.onError(result.responseCode)
        }
    }

    private suspend fun connectBillingClient(): Boolean {
        if (isBillingClientConnected && billingClient.isReady) return true

        return suspendCoroutine { continuation ->
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult?) {
                    val responseCode = billingResult?.responseCode ?: ResponseCodes.SERVICE_UNAVAILABLE

                    billingResult?.let { Log.d(LOG_TAG, "onBillingSetupFinished. ${billingResultLogMessage(it)}") }

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

    private fun handlePendingPurchases() {
        val result = billingClient.queryPurchases(BillingClient.SkuType.INAPP)
        Log.d(LOG_TAG, "handlePendingPurchases. ${billingResultLogMessage(result.billingResult)}")

        if (result.responseCode == ResponseCodes.OK) {
            for (purchase in result.purchasesList) handlePurchase(purchase)
        } else {
            resetCurrentPurchase()
            purchaseCallback?.onError(result.responseCode)
        }
    }

    private suspend fun getSkuDetails(): List<SkuDetails> {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(listOf(PRODUCT_ID_DOCUMENT_YEAR_LIFETIME))
            .setType(BillingClient.SkuType.INAPP)

        return try {
            suspendCoroutine { continuation ->
                billingClient.querySkuDetailsAsync(params.build()) { result, details ->
                    if (result != null && details != null && result.responseCode == ResponseCodes.OK) {
                        continuation.resume(details)
                    } else {
                        continuation.resumeWithException(RuntimeException("querySkuDetailsAsync. ${billingResultLogMessage(result)}"))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Collections.emptyList()
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        Log.d(LOG_TAG, "handlePurchase. Purchase - $purchase")
        when (purchase.purchaseState) {
            PurchaseState.PURCHASED -> validatePurchase(purchase)
            PurchaseState.PENDING -> {
                /* TODO: handle pending transaction */
                resetCurrentPurchase()
                purchaseCallback?.onError(ResponseCodes.DEVELOPER_ERROR)
            }
            PurchaseState.UNSPECIFIED_STATE -> {
                /* Unspecified situation */
                resetCurrentPurchase()
                purchaseCallback?.onError(ResponseCodes.DEVELOPER_ERROR)
            }
        }
    }

    private fun validatePurchase(purchase: Purchase) {
        Log.d(LOG_TAG, "validatePurchase. Document key - ${currentDocumentKey?.key}. Purchase token - ${purchase.purchaseToken}")

        val request = ValidatePurchaseUseCase.Request(
            currentDocumentKey ?: return,
            purchase.purchaseToken,
            currentSkuDetails?.sku ?: return
        )

        launch {
            validatePurchaseUseCase.execute(
                request,
                onSuccess = { acknowledgePurchase(purchase) },
                onFailure = { error ->
                    resetCurrentPurchase()
                    purchaseCallback?.onError(ResponseCodes.DEVELOPER_ERROR)
                    Log.d(LOG_TAG, error.message ?: "Unknown error")
                }
            )
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        Log.d(LOG_TAG, "acknowledgePurchase")
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .setDeveloperPayload(createPayloadForPurchase(purchase.purchaseToken))
            .build()

        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            Log.d(LOG_TAG, "acknowledgePurchase. ${billingResultLogMessage(billingResult)}")
            if (billingResult.responseCode == ResponseCodes.OK) {
                Log.d(LOG_TAG, "acknowledgePurchase. Success purchase - $purchase")
                purchaseCallback?.onPurchased(purchase)
            } else {
                purchaseCallback?.onError(billingResult.responseCode)
            }
            resetCurrentPurchase()
        }
    }

    private fun createPayloadForPurchase(purchaseToken: String): String {
        return JSONObject().apply {
            put("documentKey", currentDocumentKey)
            put("purchaseToken", purchaseToken)
        }.toString()
    }

    private fun resetCurrentPurchase() {
        currentDocumentKey = null
        currentSkuDetails = null
    }

    private fun billingResultLogMessage(billingResult: BillingResult): String {
        return "Response code - ${billingResult.responseCode}. Message - ${billingResult.debugMessage}"
    }
}
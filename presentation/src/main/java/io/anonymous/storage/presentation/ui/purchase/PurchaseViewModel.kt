package io.anonymous.storage.presentation.ui.purchase

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import io.anonymous.storage.data.feature.billing.PurchaseController
import io.anonymous.storage.domain.base.exception.AppException
import io.anonymous.storage.domain.base.exception.UnknownException
import io.anonymous.storage.domain.common.model.DocumentKey
import io.anonymous.storage.presentation.base.BaseViewModel
import io.anonymous.storage.presentation.utils.extentions.asLiveData
import kotlinx.coroutines.launch

class PurchaseViewModel(private val purchaseController: PurchaseController) : BaseViewModel() {

    private var documentKey: String? = null

    private val _skuDetails = MutableLiveData<SkuDetails>()
    val skuDetails = _skuDetails.asLiveData()

    private val _successPurchase = MutableLiveData<Purchase>()
    val successPurchase = _successPurchase.asLiveData()

    init {
        purchaseController.setPurchaseCallback(object : PurchaseController.PurchaseCallback {
            override fun onPurchased(purchase: Purchase) {
                _successPurchase.postValue(purchase)
            }

            override fun onError(errorCode: Int) {
                postError(UnknownException())
            }
        })
        loadSkuDetails()
    }

    fun setDocumentKey(documentKey: String) {
        if (documentKey.isNotBlank()) this.documentKey = documentKey
    }

    fun purchaseDocumentLifetime(activity: Activity) {
        if (isLoading()) return

        val documentKey = DocumentKey(documentKey ?: return)
        val skuDetails = skuDetails.value ?: return

        setLoading(true)
        launch {
            purchaseController.buyDocumentLifetime(activity, documentKey, skuDetails)
            postLoading(false)
        }
    }

    private fun loadSkuDetails() {
        if (isLoading()) return

        setLoading(true)
        launch {
            purchaseController.init()

            val documentLifetimeSkuDetails = purchaseController.getDocumentLifetimeSkuDetails()

            if (documentLifetimeSkuDetails == null) postError(UnknownException())
            else _skuDetails.postValue(documentLifetimeSkuDetails)
            postLoading(false)
        }
    }
}
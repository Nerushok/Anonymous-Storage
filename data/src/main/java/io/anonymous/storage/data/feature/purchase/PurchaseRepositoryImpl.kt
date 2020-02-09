package io.anonymous.storage.data.feature.purchase

import android.util.Log
import io.anonymous.storage.data.common.exception.ServerException
import io.anonymous.storage.data.remote.cloud_functions.CloudFunctionsController
import io.anonymous.storage.data.utils.getTaskResult
import io.anonymous.storage.domain.base.exception.AppException
import io.anonymous.storage.domain.common.model.DocumentKey
import io.anonymous.storage.domain.feature.purchase.PurchaseRepository

class PurchaseRepositoryImpl(private val cloudFunctionsController: CloudFunctionsController) : PurchaseRepository {

    private val LOG_TAG = "PurchaseRepositoryImpl"

    override suspend fun buyDocumentLifetime(key: DocumentKey, purchaseToken: String, sku: String) {
        val request = mapOf(
            "sku" to sku,
            "documentKey" to key.key,
            "purchaseToken" to purchaseToken
        )

        val response = cloudFunctionsController.getInstance()
            .getHttpsCallable("buyDocumentLifetime")
            .call(request)
            .getTaskResult()

        Log.d(LOG_TAG, response.data.toString())

        if (response.data != "success") throw ServerException()
    }
}
package io.anonymous.storage.domain.feature.purchase

import io.anonymous.storage.domain.base.interactor.EmptyResponseInteractor
import io.anonymous.storage.domain.common.model.DocumentKey

class ValidatePurchaseUseCase(
    private val purchaseRepository: PurchaseRepository
) : EmptyResponseInteractor<ValidatePurchaseUseCase.Request>() {

    override suspend fun action(request: Request) {
        purchaseRepository.buyDocumentLifetime(request.documentKey, request.purchaseToken, request.sku)
    }

    data class Request(val documentKey: DocumentKey, val purchaseToken: String, val sku: String)
}
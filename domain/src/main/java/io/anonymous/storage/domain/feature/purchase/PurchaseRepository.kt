package io.anonymous.storage.domain.feature.purchase

import io.anonymous.storage.domain.common.model.DocumentKey

interface PurchaseRepository {

    suspend fun buyDocumentLifetime(key: DocumentKey, purchaseToken: String)
}
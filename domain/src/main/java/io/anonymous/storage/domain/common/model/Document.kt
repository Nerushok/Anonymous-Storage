package io.anonymous.storage.domain.common.model

data class Document(
    val key: String,
    val dateOfCreationTimestamp: Long,
    val documentPurchasingType: DocumentPurchasingType,
    val rawData: String,
    val lifeTimeInDays: Int
)
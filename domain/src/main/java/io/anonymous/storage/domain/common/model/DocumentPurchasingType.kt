package io.anonymous.storage.domain.common.model

sealed class DocumentPurchasingType {

    object Trial : DocumentPurchasingType()
    object Purchased : DocumentPurchasingType()
}
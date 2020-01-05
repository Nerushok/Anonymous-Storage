package io.anonymous.storage.data.mapper

import io.anonymous.storage.data.base.BaseMapper
import io.anonymous.storage.domain.common.model.DocumentPurchasingType

class DocumentPurchasingTypeMapper : BaseMapper<Int, DocumentPurchasingType>() {

    private val STATE_PURCHASED = 1024

    override fun map(fromModel: Int): DocumentPurchasingType = when (fromModel) {
        STATE_PURCHASED -> DocumentPurchasingType.Purchased
        else -> DocumentPurchasingType.Trial
    }
}
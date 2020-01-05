package io.anonymous.storage.domain.feature.documents

import io.anonymous.storage.domain.base.interactor.Interactor
import io.anonymous.storage.domain.common.model.Document
import io.anonymous.storage.domain.common.model.DocumentKey
import io.anonymous.storage.domain.common.model.DocumentPurchasingType

class CreateEmptyDocumentUseCase : Interactor<DocumentKey, Document>() {

    override suspend fun action(request: DocumentKey): Document {
        return Document(
            request.key,
            -1,
            DocumentPurchasingType.Trial,
            "",
            -1
        )
    }
}
package io.anonymous.storage.domain.feature.documents

import io.anonymous.storage.domain.base.Result
import io.anonymous.storage.domain.base.interactor.Interactor
import io.anonymous.storage.domain.common.model.Document
import io.anonymous.storage.domain.common.model.DocumentKey

class GetDocumentUseCase(
    private val documentsRepository: DocumentsRepository,
    private val createEmptyDocumentUseCase: CreateEmptyDocumentUseCase
) : Interactor<DocumentKey, Document>() {

    override suspend fun action(request: DocumentKey): Document {
        val document = documentsRepository.getDocumentByKey(request)

        return document ?: createEmptyDocument(request)
    }

    private suspend fun createEmptyDocument(documentKey: DocumentKey): Document {
        return when (val result = createEmptyDocumentUseCase.execute(documentKey)) {
            is Result.Success -> result.data
            is Result.Failure -> throw result.error
        }
    }
}
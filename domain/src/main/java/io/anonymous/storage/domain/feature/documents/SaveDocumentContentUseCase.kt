package io.anonymous.storage.domain.feature.documents

import io.anonymous.storage.domain.base.interactor.EmptyResponseInteractor
import io.anonymous.storage.domain.common.model.DocumentKey

class SaveDocumentContentUseCase(
    private val documentsRepository: DocumentsRepository
) : EmptyResponseInteractor<SaveDocumentContentUseCase.Request>() {

    override suspend fun action(request: Request) {
        documentsRepository.saveDocumentContent(request.documentKey, request.documentContent)
    }

    data class Request(val documentKey: DocumentKey, val documentContent: String)
}
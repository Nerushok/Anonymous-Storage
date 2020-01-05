package io.anonymous.storage.domain.feature.documents

import io.anonymous.storage.domain.common.model.Document
import io.anonymous.storage.domain.common.model.DocumentKey

interface DocumentsRepository {

    suspend fun getDocumentByKey(key: DocumentKey): Document?

    suspend fun saveDocumentContent(key: DocumentKey, content: String)
}
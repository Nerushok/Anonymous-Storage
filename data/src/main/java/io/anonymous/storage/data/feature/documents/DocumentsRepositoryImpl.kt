package io.anonymous.storage.data.feature.documents

import android.util.Log
import io.anonymous.storage.data.mapper.DocumentRemoteMapper
import io.anonymous.storage.data.remote.cloud_functions.CloudFunctionsController
import io.anonymous.storage.data.remote.firestore.FirestoreController
import io.anonymous.storage.data.utils.getTaskResult
import io.anonymous.storage.domain.common.model.Document
import io.anonymous.storage.domain.common.model.DocumentKey
import io.anonymous.storage.domain.feature.documents.DocumentsRepository

class DocumentsRepositoryImpl(
    private val firestoreController: FirestoreController,
    private val cloudFunctionsController: CloudFunctionsController,
    private val documentRemoteMapper: DocumentRemoteMapper
) : DocumentsRepository {

    override suspend fun getDocumentByKey(key: DocumentKey): Document? {
        val documentSnapshot = firestoreController.getDocumentsDatabase()
            .document(key.key)
            .get()
            .getTaskResult()

        return documentRemoteMapper.map(documentSnapshot ?: return null)
    }

    override suspend fun saveDocumentContent(key: DocumentKey, content: String) {
        val request = mapOf(
            "documentKey" to key.key,
            "documentContent" to content
        )
        val response = cloudFunctionsController.getInstance()
            .getHttpsCallable("putDocumentContent")
            .call(request)
            .getTaskResult()

        Log.d("TEST", response.data.toString())
    }
}
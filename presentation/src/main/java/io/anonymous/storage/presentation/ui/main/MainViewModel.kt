package io.anonymous.storage.presentation.ui.main

import androidx.lifecycle.MutableLiveData
import io.anonymous.storage.domain.common.model.Document
import io.anonymous.storage.domain.common.model.DocumentKey
import io.anonymous.storage.domain.extentions.execute
import io.anonymous.storage.domain.feature.documents.GetDocumentUseCase
import io.anonymous.storage.presentation.base.BaseViewModel
import io.anonymous.storage.presentation.common.DocumentLinkHolder
import io.anonymous.storage.presentation.utils.extentions.asLiveData
import kotlinx.coroutines.launch

class MainViewModel(
    private val getDocumentUseCase: GetDocumentUseCase,
    private val documentLinkHolder: DocumentLinkHolder
) : BaseViewModel() {

    private val _foundedDocument = MutableLiveData<Document?>(null)
    val foundedDocument = _foundedDocument.asLiveData()

    fun getDocumentByKey(documentKey: String) {
        if (isLoading()) return

        setLoading(true)
        launch {
            getDocumentUseCase.execute(
                DocumentKey(documentKey),
                onSuccess = { data ->
                    documentLinkHolder.putDocument(data)
                    _foundedDocument.postValue(data)
                },
                onFailure = { error -> postError(error) })

            postLoading(false)
        }
    }
}
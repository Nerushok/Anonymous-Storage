package io.anonymous.storage.presentation.ui.document

import androidx.lifecycle.MutableLiveData
import io.anonymous.storage.domain.common.model.DocumentKey
import io.anonymous.storage.domain.extentions.execute
import io.anonymous.storage.domain.feature.documents.SaveDocumentContentUseCase
import io.anonymous.storage.presentation.base.BaseViewModel
import io.anonymous.storage.presentation.common.DocumentLinkHolder
import io.anonymous.storage.presentation.utils.LiveEventCallback
import io.anonymous.storage.presentation.utils.extentions.asLiveData
import kotlinx.coroutines.launch

class DocumentContentViewModel(
    documentLinkHolder: DocumentLinkHolder,
    private val saveDocumentContentUseCase: SaveDocumentContentUseCase
) : BaseViewModel() {

    private val _document = MutableLiveData(documentLinkHolder.getLastDocumentAndReset())
    val document = _document.asLiveData()

    private val _isSaveEnabled = MutableLiveData(false)
    val isSaveEnabled = _isSaveEnabled.asLiveData()

    fun onDocumentContentChanged(newDocumentContent: String) {
        updateIsSaveEnabledState(newDocumentContent)
    }

    fun saveDocumentContent(content: String, onSuccessCallback: LiveEventCallback<Unit>) {
        if (isLoading()) return

        if (document.value?.rawData == content) return

        setLoading(true)
        val documentKey = DocumentKey(document.value?.key ?: return)
        launch {
            saveDocumentContentUseCase.execute(
                SaveDocumentContentUseCase.Request(documentKey, content),
                onSuccess = {
                    _document.postValue(document.value?.copy(rawData = content))
                    _isSaveEnabled.postValue(false)
                    onSuccessCallback.post(Unit)
                },
                onFailure = { error -> postError(error) }
            )
            postLoading(false)
        }
    }

    private fun updateIsSaveEnabledState(newDocumentContent: String) {
        val isContentChanged = newDocumentContent != document.value?.rawData ?: ""

        if (isSaveEnabled.value != isContentChanged) _isSaveEnabled.value = isContentChanged
    }
}
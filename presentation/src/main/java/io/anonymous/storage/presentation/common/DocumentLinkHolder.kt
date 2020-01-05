package io.anonymous.storage.presentation.common

import io.anonymous.storage.domain.common.model.Document

/**
 * Use this class for transferring io.anonymous.storage.domain.common.model.Document object.
 * Object can be very large for serialising.
 */
class DocumentLinkHolder {

    @Volatile
    private var document: Document? = null


    fun putDocument(document: Document) {
        this.document = document
    }

    fun getLastDocument(): Document? {
        return document
    }

    fun getLastDocumentAndReset(): Document? {
        val documentLink = document

        document = null

        return documentLink
    }
}
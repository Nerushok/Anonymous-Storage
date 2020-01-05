package io.anonymous.storage.data.mapper

import com.google.firebase.firestore.DocumentSnapshot
import io.anonymous.storage.data.base.FirestoreMapper
import io.anonymous.storage.data.remote.firestore.FirestoreDbScheme
import io.anonymous.storage.domain.common.model.Document
import io.anonymous.storage.domain.common.model.DocumentPurchasingType

class DocumentRemoteMapper constructor(
    private val documentPurchasingTypeMapper: DocumentPurchasingTypeMapper
) : FirestoreMapper<Document> {

    override fun map(documentSnapshot: DocumentSnapshot): Document = with(documentSnapshot) {
        Document(
            id,
            getLong(FirestoreDbScheme.TableDocuments.FIELD_DATE_CREATION) ?: 0,
            mapPurchasingType(get(FirestoreDbScheme.TableDocuments.FIELD_PURCHASING_TYPE, Int::class.java) ?: -1),
            getString(FirestoreDbScheme.TableDocuments.FIELD_RAW_DATA) ?: "",
            getLong(FirestoreDbScheme.TableDocuments.FIELD_LIFE_TIME_IN_DAYS)?.toInt() ?: -1
        )
    }

    private fun mapPurchasingType(state: Int): DocumentPurchasingType = documentPurchasingTypeMapper.map(state)
}
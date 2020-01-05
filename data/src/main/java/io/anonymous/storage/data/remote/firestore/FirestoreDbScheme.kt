package io.anonymous.storage.data.remote.firestore

object FirestoreDbScheme {

    object TableDocuments {

        val TABLE_NAME = "documents"

        val FIELD_DATE_CREATION = "date_creation_ts"
        val FIELD_PURCHASING_TYPE = "document_purchasing_type"
        val FIELD_RAW_DATA = "raw_data"
        val FIELD_LIFE_TIME_IN_DAYS = "life_time_in_days"
    }
}
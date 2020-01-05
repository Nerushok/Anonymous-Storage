package io.anonymous.storage.data.remote.firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreController {

    private var firebaseFirestore: FirebaseFirestore? = null

    fun getInstance(): FirebaseFirestore {
        if (firebaseFirestore == null) initFirebaseFirestore()

        return firebaseFirestore!!
    }

    private fun initFirebaseFirestore() {
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    fun getDocumentsDatabase(): CollectionReference {
        return getInstance().collection(FirestoreDbScheme.TableDocuments.TABLE_NAME)
    }
}
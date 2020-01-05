package io.anonymous.storage.data.base

import com.google.firebase.firestore.DocumentSnapshot

interface FirestoreMapper<Out> {

    fun map(documentSnapshot: DocumentSnapshot): Out
}
package com.example.cantinadigital.data.repository

import com.example.cantinadigital.data.model.AuditLog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuditLogRepository @Inject constructor (
  private val firestore: FirebaseFirestore,
) {

    private val logPath: String = "audit_logs"

    suspend fun logAction(
        type: String,
        action: String,
        description: String,
        username: String,
        userClass: String = ""
    ) {

        val log = AuditLog(
            type = type,
            action = action,
            description = description,
            username = username,
            userClass = userClass,
            dateTime = Timestamp.now()
        )

        firestore.collection(logPath).add(log).await()

    }

    fun getLogs() : Flow<List<AuditLog>> = callbackFlow {
        val listener = firestore.collection(logPath)
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val logs = snapshots?.toObjects(AuditLog::class.java) ?: emptyList()
                trySend(logs)

            }
        awaitClose { listener.remove() }
    }

    suspend fun getLogsOnce(): List<AuditLog> {
        val snapshot = firestore.collection("audit_logs")
            .orderBy("data_hora", Query.Direction.DESCENDING)
            .get()
            .await()
        return snapshot.toObjects(AuditLog::class.java)
    }

}
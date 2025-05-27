package com.project.socialhabittracker.data.remote.firestore

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.project.socialhabittracker.data.local.habit_completion_db.HabitCompletion
import com.project.socialhabittracker.data.local.habit_db.Habit
import kotlinx.coroutines.tasks.await

class FirestoreService {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private fun userDoc(): DocumentReference {
        val uid = auth.currentUser?.uid ?: throw Exception("User not logged in")
        return firestore.collection("users").document(uid)
    }

    suspend fun fetchHabits(): List<Habit> {
        val snapshot = userDoc()
            .collection("habits")
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(Habit::class.java) }
    }

    suspend fun fetchCompletions(habitId: String): List<HabitCompletion> {
        val snapshot = userDoc()
            .collection("habits")
            .document(habitId)
            .collection("completions")
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(HabitCompletion::class.java) }
    }

    suspend fun uploadHabits(habits: List<Habit>) {

        try {
            val habitsRef = userDoc().collection("habits")
            habits.forEach { habit ->
                habitsRef.document(habit.id.toString()).set(habit).await()
            }
        } catch (e: Exception) {
            Log.d("error123456", e.localizedMessage ?: "some error occurred")
        }
    }

    suspend fun uploadCompletions(completions: List<HabitCompletion>) {

        try {
            completions.forEach { completion ->
                val completionId = "${completion.habitId}_${completion.date}" // unique ID
                userDoc().collection("habits")
                    .document(completion.habitId.toString())
                    .collection("completions")
                    .document(completionId) // completion Id is required so that when inserting completions firestore doesn't store duplicates for same completion
                    .set(completion)
                    .await()
            }
        } catch (e: Exception) {
            Log.d("error123456", e.localizedMessage ?: "some error occurred")
        }
    }

    suspend fun deleteHabitWithCompletion(habitId: Int) {
        try {
            val habitRef = userDoc()
                .collection("habits")
                .document(habitId.toString())

            // Delete completions first
            val completionsSnapshot = habitRef
                .collection("completions")
                .get()
                .await()

            completionsSnapshot.documents.forEach { it.reference.delete().await() }

            // Now delete the habit document itself
            habitRef.delete().await()
        } catch (e: Exception) {
            Log.d("error123456", e.localizedMessage ?: "some error occurred")
        }
    }
}

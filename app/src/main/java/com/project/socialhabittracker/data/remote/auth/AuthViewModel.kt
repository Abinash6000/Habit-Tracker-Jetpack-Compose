package com.project.socialhabittracker.data.remote.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.project.socialhabittracker.domain.model.UserModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val auth = Firebase.auth

    private val firestore = Firebase.firestore

    fun logout() {
        auth.signOut()
    }

    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    onResult(true, "Login Successful")
                } else {
                    onResult(false, "Login failed: ${task.exception?.localizedMessage}")
                }
            }
    }

    fun signup(email: String, name: String, password: String, onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result.user?.uid ?: ""
                    val userModel = UserModel(name = name, email = email, uid = userId)

                    firestore.collection("users").document(userId)
                        .set(userModel)
                        .addOnCompleteListener { dbTask ->
                            if(dbTask.isSuccessful) {
                                onResult(true, "Signup Successful")
                            } else {
                                onResult(false, "Signup failed: ${dbTask.exception?.localizedMessage}")
                            }
                        }
                } else {
                    onResult(false, "Signup failed: ${task.exception?.localizedMessage}")
                }
            }
    }
}
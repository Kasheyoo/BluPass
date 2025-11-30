package com.example.eldroidproject.Model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun registerUser(
        email: String,
        password: String,
        user: User,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userRef = database.getReference("users").child(uid)

                    // Admin auto-approved, Homeowner pending
                    val finalUser = if (user.role == "Admin") {
                        user.copy(status = "approved")
                    } else {
                        user.copy(status = "pending")
                    }

                    userRef.setValue(finalUser)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e ->
                            onFailure(e.message ?: "Failed to save user data")
                        }

                } else {
                    onFailure(task.exception?.message ?: "Registration failed")
                }
            }
    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,   // return role
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val userRef = database.getReference("users").child(uid)

                    userRef.get().addOnSuccessListener { snapshot ->
                        val role = snapshot.child("role").value as? String ?: ""
                        val status = snapshot.child("status").value as? String ?: ""

                        if (role == "Admin") {
                            onSuccess(role) // Admin always allowed
                        } else if (role == "Homeowner" && status == "approved") {
                            onSuccess(role) // Homeowner only if approved
                        } else {
                            auth.signOut()
                            onFailure("Your account is still pending admin approval.")
                        }
                    }.addOnFailureListener {
                        onFailure("Failed to verify user status.")
                    }
                } else {
                    onFailure(task.exception?.message ?: "Invalid credentials")
                }
            }
    }

    fun logoutUser() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
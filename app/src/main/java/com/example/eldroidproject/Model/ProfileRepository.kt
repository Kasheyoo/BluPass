package com.example.eldroidproject.Model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    fun saveProfileData(
        name: String,
        email: String,
        phone: String,
        plate: String,
        model: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = auth.currentUser ?: return onFailure("User not logged in")
        val userRef = db.child("Users").child(user.uid)

        val updates = mapOf(
            "username" to name,
            "email" to email,
            "phone" to phone,
            "plate" to plate,
            "model" to model
        )

        userRef.updateChildren(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Update failed") }
    }

    fun updatePassword(
        newPassword: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = auth.currentUser ?: return onFailure("User not logged in")

        user.updatePassword(newPassword)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Password update failed") }
    }

    fun getProfileData(
        onSuccess: (Map<String, String>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = auth.currentUser ?: return onFailure("User not logged in")
        val userRef = db.child("Users").child(user.uid)

        userRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val data = snapshot.value as? Map<String, String> ?: emptyMap()
                    onSuccess(data)
                } else {
                    onFailure("No user data found.")
                }
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "Failed to fetch user data.")
            }
    }
}

package com.example.eldroidproject.Model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.uuid.Uuid

class ProfileRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    fun saveProfileData(
        username: String,
        email: String,
        phone: String,
        plate: String,
        model: String,
        uuid: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = auth.currentUser ?: return onFailure("User not logged in")
        val userRef = db.child("Users").child(user.uid)

        val updates = mapOf(
            "username" to username,
            "email" to email,
            "phone" to phone,
            "plate" to plate,
            "model" to model,
            "uuid" to uuid
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
                    val data = mutableMapOf<String, String>()

                    data["username"] = snapshot.child("username").value?.toString() ?: ""
                    data["email"] = snapshot.child("email").value?.toString() ?: ""
                    data["phone"] = snapshot.child("phone").value?.toString() ?: ""
                    data["plate"] = snapshot.child("plate").value?.toString() ?: ""
                    data["model"] = snapshot.child("model").value?.toString() ?: ""
                    data["uuid"] = snapshot.child("uuid").value?.toString() ?: ""

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

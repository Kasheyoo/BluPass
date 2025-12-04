package com.example.eldroidproject.Model

import android.util.Log
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
                    val rolePath = if (user.role == "Admin") "admins" else "homeowners"
                    val userRef = database.getReference("users").child(rolePath).child(uid)

                    val finalUser = user.copy(
                        status = if (user.role == "Admin") "approved" else "pending",
                        password = password
                    )

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

    fun loginUserByUsername(
        username: String,
        password: String,
        onSuccess: (String) -> Unit,
        onPending: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val usersRef = database.getReference("users")
        Log.d("AuthRepository", "Attempting login for username: $username")

        fun checkSnapshot(snapshot: com.google.firebase.database.DataSnapshot, roleType: String) {
            for (child in snapshot.children) {
                val storedUsername = child.child("username").value as? String ?: ""
                val storedPassword = child.child("password").value as? String ?: ""
                val status = child.child("status").value as? String ?: ""
                val role = child.child("role").value as? String ?: roleType

                Log.d("AuthRepository", "Found user: username=$storedUsername, password=$storedPassword, status=$status, role=$role")

                if (storedUsername == username && storedPassword == password) {
                    if (status == "approved") {
                        onSuccess(role)
                        return
                    } else {
                        onPending(role)
                        return
                    }
                }
            }
            onFailure("Invalid credentials.")
        }

        usersRef.child("admins").orderByChild("username").equalTo(username).get()
            .addOnSuccessListener { snapshot ->
                Log.d("AuthRepository", "Admin snapshot exists: ${snapshot.exists()}")
                if (snapshot.exists()) {
                    checkSnapshot(snapshot, "Admin")
                } else {
                    usersRef.child("homeowners").orderByChild("username").equalTo(username).get()
                        .addOnSuccessListener { homeSnap ->
                            Log.d("AuthRepository", "Homeowner snapshot exists: ${homeSnap.exists()}")
                            if (homeSnap.exists()) {
                                checkSnapshot(homeSnap, "Homeowner")
                            } else {
                                onFailure("Username not found.")
                            }
                        }
                        .addOnFailureListener {
                            Log.e("AuthRepository", "Homeowner query failed", it)
                            onFailure("Failed to verify homeowner username.")
                        }
                }
            }
            .addOnFailureListener {
                Log.e("AuthRepository", "Admin query failed", it)
                onFailure("Failed to verify admin username.")
            }
    }

    fun logoutUser() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}

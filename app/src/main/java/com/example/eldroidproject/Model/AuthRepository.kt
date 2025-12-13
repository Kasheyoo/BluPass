package com.example.eldroidproject.Model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
        // 1. Create Account in Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                    val rolePath = if (user.role == "Admin") "admins" else "homeowners"
                    val userRef = database.getReference("users").child(rolePath).child(uid)

                    // 2. Save User Details (No username)
                    userRef.setValue(user)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e ->
                            onFailure(e.message ?: "Failed to save user data")
                        }

                } else {
                    onFailure(task.exception?.message ?: "Registration failed")
                }
            }
    }

    fun loginUserByEmail(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onPending: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        // 1. Authenticate with Firebase Auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid
                if (uid != null) {
                    checkUserRoleAndStatus(uid, onSuccess, onPending, onFailure)
                } else {
                    onFailure("Authentication failed.")
                }
            }
            .addOnFailureListener { e ->
                // Handles wrong password or user not existing in Auth
                onFailure(e.message ?: "Login failed.")
            }
    }

    private fun checkUserRoleAndStatus(
        uid: String,
        onSuccess: (String) -> Unit,
        onPending: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val usersRef = database.getReference("users")

        fun handleLogin(status: String, role: String) {
            if (status == "approved") {
                onSuccess(role)
            } else {
                auth.signOut()
                onPending(role) // Shows Dialog
            }
        }

        // 2. Check Admin
        usersRef.child("admins").child(uid).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val status = snapshot.child("status").value.toString()
                handleLogin(status, "Admin")
            } else {
                // 3. Check Homeowner
                usersRef.child("homeowners").child(uid).get().addOnSuccessListener { homeSnap ->
                    if (homeSnap.exists()) {
                        val status = homeSnap.child("status").value.toString()
                        handleLogin(status, "Homeowner")
                    } else {
                        // 4. User in Auth but NOT in Database -> Treat as Error (Toast)
                        auth.signOut()
                        onFailure("User profile not found in database.")
                    }
                }.addOnFailureListener {
                    auth.signOut()
                    onFailure("Database error.")
                }
            }
        }.addOnFailureListener {
            auth.signOut()
            onFailure("Database error.")
        }
    }

    fun fetchLotNumber(onResult: (String) -> Unit) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            onResult("Welcome")
            return
        }

        val dbRef = database.reference

        // 1. Check Homeowners
        dbRef.child("users").child("homeowners").child(uid).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Get Lot Number
                    val lot = snapshot.child("lotNumber").value?.toString() ?: "Homeowner"
                    onResult(lot)
                } else {
                    // 2. Check Admins if not a homeowner
                    dbRef.child("users").child("admins").child(uid).get().addOnSuccessListener { adminSnap ->
                        val name = adminSnap.child("username").value?.toString() ?: "Admin"
                        onResult(name)
                    }
                }
            }
            .addOnFailureListener {
                onResult("Welcome")
            }
    }

    fun logoutUser() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}

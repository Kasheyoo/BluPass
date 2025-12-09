package com.example.eldroidproject.Model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GuestRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    // Function 1: Save a new invite to "Guests" path
    fun saveGuestInvite(name: String, code: String, onComplete: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onComplete(false, "User not logged in")
            return
        }

        // ✅ Updated Path: Guests -> userID
        val guestRef = db.child("Guests").child(uid).push()

        val guestData = mapOf(
            "name" to name,
            "code" to code,
            "status" to "active",
            "timestamp" to System.currentTimeMillis()
        )

        guestRef.setValue(guestData)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }

    // Function 2: Fetch the list from "Guests" path
    fun getGuestList(callback: (List<Guest>) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            callback(emptyList())
            return
        }

        // ✅ Updated Path: Guests -> userID
        db.child("Guests").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val guests = snapshot.children.mapNotNull { child ->
                    Guest(
                        name = child.child("name").value?.toString() ?: "Unknown",
                        code = child.child("code").value?.toString() ?: "",
                        status = child.child("status").value?.toString() ?: "active",
                        timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L
                    )
                }
                callback(guests)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun getGateStatus(): Gate {
        return Gate("Closed")
    }

    // ✅ THIS IS THE MISSING FUNCTION. IT MUST BE HERE.
    fun getUserLotNumber(onResult: (String) -> Unit) {
        val uid = auth.currentUser?.uid

        if (uid == null) {
            onResult("Welcome")
            return
        }

        // 1. Check Homeowners path for Lot Number
        db.child("users").child("homeowners").child(uid).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Fetch "lotNumber", default to "Homeowner" if empty
                    val lot = snapshot.child("lotNumber").value?.toString() ?: "Homeowner"
                    onResult(lot)
                } else {
                    // 2. Check Admin path for Username
                    db.child("users").child("admins").child(uid).get().addOnSuccessListener { adminSnap ->
                        val name = adminSnap.child("username").value?.toString() ?: "Admin"
                        onResult(name)
                    }
                }
            }
            .addOnFailureListener {
                onResult("Welcome") // Fallback in case of database error
            }
    }
}
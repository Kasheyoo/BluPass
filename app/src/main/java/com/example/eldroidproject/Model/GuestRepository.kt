package com.example.eldroidproject.Model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GuestRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    // Function 1: Save a new invite (invitedBy = Lot Number)
    fun saveGuestInvite(name: String, vehicle: String, code: String, onComplete: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onComplete(false, "User not logged in")
            return
        }

        // 1. Fetch User's Lot Number first
        db.child("users").child("homeowners").child(uid).get().addOnSuccessListener { snapshot ->
            val lotNumber = snapshot.child("lotNumber").value?.toString() ?: "Unknown Lot"

            // 2. Prepare Guest Data
            val guestData = mapOf(
                "name" to name,
                "vehicle" to vehicle, // ✅ Saving Vehicle Info
                "code" to code,
                "lotNumber" to lotNumber,
                "invitedBy" to lotNumber,
                "status" to "active",
                "timestamp" to System.currentTimeMillis()
            )

            // 3. Save to Database
            val guestRef = db.child("Guests").child(uid).push()
            guestRef.setValue(guestData)
                .addOnSuccessListener { onComplete(true, null) }
                .addOnFailureListener { e -> onComplete(false, e.message) }

        }.addOnFailureListener {
            onComplete(false, "Failed to fetch user details")
        }
    }

    // Function 2: Fetch the list
    fun getGuestList(callback: (List<Guest>) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            callback(emptyList())
            return
        }

        db.child("Guests").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val guests = snapshot.children.mapNotNull { child ->
                    try {
                        Guest(
                            name = child.child("name").value?.toString() ?: "Unknown",
                            vehicle = child.child("vehicle").value?.toString() ?: "", // ✅ Retrieve Vehicle
                            code = child.child("code").value?.toString() ?: "",
                            status = child.child("status").value?.toString() ?: "active",
                            timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L
                        )
                    } catch (e: Exception) { null }
                }
                callback(guests.reversed())
            }
            override fun onCancelled(error: DatabaseError) { callback(emptyList()) }
        })
    }

    // Function 3: Get User Info Helper (Used for Welcome messages)
    fun getUserLotNumber(onResult: (String) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            onResult("Welcome")
            return
        }

        db.child("users").child("homeowners").child(uid).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val lot = snapshot.child("lotNumber").value?.toString() ?: "Homeowner"
                    onResult(lot)
                } else {
                    db.child("users").child("admins").child(uid).get().addOnSuccessListener { adminSnap ->
                        val name = adminSnap.child("username").value?.toString() ?: "Admin"
                        onResult(name)
                    }
                }
            }
            .addOnFailureListener {
                onResult("Welcome")
            }
    }
}
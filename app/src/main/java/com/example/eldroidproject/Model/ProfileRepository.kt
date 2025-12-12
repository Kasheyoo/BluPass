package com.example.eldroidproject.Model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    // 1. Save Profile Data
    fun saveProfileData(
        phone: String,
        plate: String,
        model: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = auth.currentUser ?: return onFailure("User not logged in")
        val uid = user.uid

        // Check if Homeowner or Admin to determine path
        db.child("users").child("homeowners").child(uid).get().addOnSuccessListener { snapshot ->
            val path = if (snapshot.exists()) "homeowners" else "admins"
            val userRef = db.child("users").child(path).child(uid)

            val updates = mapOf<String, Any>(
                "mobile" to phone,
                "plateNumber" to plate,
                "carModel" to model
            )

            userRef.updateChildren(updates)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e.message ?: "Update failed") }
        }
    }

    // 2. Get Profile Data (Includes BLE UUID Fetching)
    fun getProfileData(onSuccess: (Profile) -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser ?: return onFailure("User not logged in")
        val uid = user.uid
        val email = user.email ?: ""

        // Helper function to fetch BLE UUID after getting user details
        fun fetchBleAndReturn(mobile: String, plate: String, model: String) {
            // ✅ QUERY: Find the device where 'userUUID' == current user's UID
            db.child("registeredDevices").orderByChild("userUUID").equalTo(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var advertisedUuid = "No Device Linked"

                        if (snapshot.exists()) {
                            // Get the first matching device
                            val deviceSnapshot = snapshot.children.firstOrNull()
                            advertisedUuid = deviceSnapshot?.child("advertisedUuid")?.value?.toString()
                                ?: "No Device Linked"
                        }

                        // Return full profile
                        onSuccess(Profile(email, mobile, plate, model, advertisedUuid))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Return profile without UUID on error
                        onSuccess(Profile(email, mobile, plate, model, "Error loading UUID"))
                    }
                })
        }

        // Fetch Basic User Info First
        db.child("users").child("homeowners").child(uid).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val mobile = snapshot.child("mobile").value?.toString() ?: ""
                val plate = snapshot.child("plateNumber").value?.toString() ?: ""
                val model = snapshot.child("carModel").value?.toString() ?: ""
                fetchBleAndReturn(mobile, plate, model)
            } else {
                // Check Admin path
                db.child("users").child("admins").child(uid).get().addOnSuccessListener { adminSnap ->
                    if (adminSnap.exists()) {
                        val mobile = adminSnap.child("mobile").value?.toString() ?: ""
                        fetchBleAndReturn(mobile, "", "") // Admins usually don't have cars
                    } else {
                        onFailure("Profile not found")
                    }
                }
            }
        }.addOnFailureListener {
            onFailure(it.message ?: "Database error")
        }
    }

    // 3. Update Password
    fun updatePassword(newPassword: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.currentUser?.updatePassword(newPassword)
            ?.addOnSuccessListener { onSuccess() }
            ?.addOnFailureListener { e -> onFailure(e.message ?: "Failed to update password") }
    }
}
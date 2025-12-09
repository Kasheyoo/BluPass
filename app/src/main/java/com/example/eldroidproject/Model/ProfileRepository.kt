package com.example.eldroidproject.Model

import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    // ✅ FIXED: Removed 'uuid' parameter
    fun saveProfileData(
        phone: String,
        plate: String,
        model: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val user = auth.currentUser ?: return onFailure("User not logged in")
        val uid = user.uid

        // Determine if Admin or Homeowner
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

    // ... (keep getProfileData and updatePassword as they are) ...
    // Note: Ensure getProfileData is the one that fetches 'registeredDevices' as provided previously.
    fun getProfileData(onSuccess: (Profile) -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser ?: return onFailure("User not logged in")
        val uid = user.uid

        fun fetchDeviceUuidAndReturn(profileSnapshot: com.google.firebase.database.DataSnapshot) {
            val deviceName = Build.PRODUCT
            db.child("registeredDevices").child(deviceName).child("userUUID").get()
                .addOnSuccessListener { deviceSnap ->
                    val fetchedUuid = deviceSnap.value?.toString() ?: "Not Registered"
                    val profile = Profile(
                        email = user.email ?: "",
                        phone = profileSnapshot.child("mobile").value?.toString() ?: "",
                        plateNumber = profileSnapshot.child("plateNumber").value?.toString() ?: "",
                        carModel = profileSnapshot.child("carModel").value?.toString() ?: "",
                        uuid = fetchedUuid
                    )
                    onSuccess(profile)
                }
                .addOnFailureListener {
                    onSuccess(Profile(
                        email = user.email ?: "",
                        phone = profileSnapshot.child("mobile").value?.toString() ?: "",
                        plateNumber = profileSnapshot.child("plateNumber").value?.toString() ?: "",
                        carModel = profileSnapshot.child("carModel").value?.toString() ?: ""
                    ))
                }
        }

        db.child("users").child("homeowners").child(uid).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) fetchDeviceUuidAndReturn(snapshot)
            else {
                db.child("users").child("admins").child(uid).get().addOnSuccessListener { adminSnap ->
                    if (adminSnap.exists()) fetchDeviceUuidAndReturn(adminSnap)
                    else onFailure("Profile not found.")
                }
            }
        }
    }

    fun updatePassword(newPassword: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.currentUser?.updatePassword(newPassword)
            ?.addOnSuccessListener { onSuccess() }
            ?.addOnFailureListener { e -> onFailure(e.message ?: "Failed") }
    }
}
package com.example.eldroidproject.Model

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminRepository {

    private val db = FirebaseDatabase.getInstance().reference

    data class GateLog(val openedBy: String = "", val timestamp: String = "")

    // 1. Fetch Homeowners (Now captures UID)
    fun fetchHomeowners(onSuccess: (List<User>) -> Unit, onFailure: (String) -> Unit) {
        db.child("users").child("homeowners").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<User>()

                for (child in snapshot.children) {
                    try {
                        // ✅ CRITICAL: Capture the Firebase Key as the UID
                        val uid = child.key ?: ""

                        Log.d("AdminRepo", "Found User: $uid")

                        // 1. Get Status
                        val status = child.child("status").value?.toString() ?: "pending"

                        // 2. Robust Field Fetching (Checks mobile/phone, lot/LotNumber)
                        val email = child.child("email").value?.toString()
                            ?: child.child("Email").value?.toString() ?: ""

                        val mobile = child.child("mobile").value?.toString()
                            ?: child.child("Mobile").value?.toString()
                            ?: child.child("phone").value?.toString()
                            ?: child.child("Phone").value?.toString()
                            ?: ""

                        val lotNumber = child.child("lotNumber").value?.toString()
                            ?: child.child("LotNumber").value?.toString()
                            ?: child.child("lot").value?.toString()
                            ?: child.child("Lot").value?.toString()
                            ?: ""

                        // 3. Create User Object including the UID
                        val user = User(
                            uid = uid, // ✅ Pass the UID here
                            email = email,
                            mobile = mobile,
                            role = "Homeowner",
                            lotNumber = lotNumber,
                            status = status
                        )
                        userList.add(user)

                    } catch (e: Exception) {
                        Log.e("AdminRepo", "Error parsing user: ${e.message}")
                    }
                }
                onSuccess(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }
        })
    }

    // 2. Fetch Guests
    fun fetchAllGuests(onSuccess: (List<Guest>) -> Unit, onFailure: (String) -> Unit) {
        db.child("Guests").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allGuests = mutableListOf<Guest>()

                for (userNode in snapshot.children) {
                    for (guestNode in userNode.children) {
                        try {
                            val name = guestNode.child("name").value?.toString() ?: "Unknown"
                            val code = guestNode.child("code").value?.toString() ?: ""
                            val status = guestNode.child("status").value?.toString() ?: "active"
                            val vehicle = guestNode.child("vehicle").value?.toString() ?: ""

                            // ✅ Read Lot Number (Default to "Unknown" if missing)
                            val lot = guestNode.child("lotNumber").value?.toString() ?: "Unknown Unit"

                            allGuests.add(Guest(name, vehicle, lot, code, status))
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                }
                onSuccess(allGuests)
            }
            override fun onCancelled(error: DatabaseError) { onFailure(error.message) }
        })
    }

    fun fetchGateHistory(onSuccess: (List<GateLog>) -> Unit, onFailure: (String) -> Unit) {
        db.child("gateHistory").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val logs = mutableListOf<GateLog>()
                // Iterate through children (e.g., "37589")
                for (child in snapshot.children) {
                    val openedBy = child.child("openedBy").value?.toString() ?: "Unknown"
                    val timestamp = child.child("timestamp").value?.toString() ?: ""
                    logs.add(GateLog(openedBy, timestamp))
                }
                // Reverse to show newest first
                onSuccess(logs.reversed())
            }
            override fun onCancelled(error: DatabaseError) {
                onFailure(error.message)
            }
        })
    }

    // 3. Approve Homeowner
    fun updateHomeownerStatus(uid: String, newStatus: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.child("users").child("homeowners").child(uid).child("status").setValue(newStatus)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Update failed") }
    }

    // 4. Reject/Delete Homeowner
    fun deleteHomeowner(uid: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.child("users").child("homeowners").child(uid).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Delete failed") }
    }

    fun setGateOverride(status: String) {
        db.child("manualOverride").setValue(status)
    }
}
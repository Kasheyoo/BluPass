package com.example.eldroidproject

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.ParcelUuid
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.eldroidproject.Model.Gate
import com.example.eldroidproject.Model.GateRepository
import com.example.eldroidproject.Presenter.HomePresenter
import com.example.eldroidproject.View.HomeView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.UUID

class HomeActivity : AppCompatActivity(), HomeView.View {

    private lateinit var presenter: HomeView.Presenter
    private lateinit var statusText: TextView
    private lateinit var openGateButton: Button
    private lateinit var historyButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var guestAccessButton: ImageButton

    // 🔹 BLE variables
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var advertiser: BluetoothLeAdvertiser? = null
    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Toast.makeText(this@HomeActivity, "BLE advertising started", Toast.LENGTH_SHORT).show()
        }

        override fun onStartFailure(errorCode: Int) {
            Toast.makeText(this@HomeActivity, "BLE advertising failed: $errorCode", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        presenter = HomePresenter(this, GateRepository())

        statusText = findViewById(R.id.statusText)
        openGateButton = findViewById(R.id.openGateButton)
        historyButton = findViewById(R.id.home_history)
        profileButton = findViewById(R.id.profile)
        homeButton = findViewById(R.id.home)
        guestAccessButton = findViewById(R.id.guest_access)

        presenter.loadGateStatus()

        // ✅ Navigation buttons
        homeButton.setOnClickListener { presenter.onHomeClicked() }
        historyButton.setOnClickListener { presenter.onHistoryClicked() }
        profileButton.setOnClickListener { presenter.onProfileClicked() }
        guestAccessButton.setOnClickListener { presenter.onGuestAccessClicked() }

        openGateButton.setOnClickListener { presenter.onOpenGateClicked() }

        // 🔹 Initialize Bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show()
            return
        }

        // 🔹 Check and request Bluetooth permissions
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 1001)
        } else {
            fetchUserUUID()
        }
    }

    private fun fetchUserUUID() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(user.uid)
        dbRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val uuid = snapshot.child("uuid").getValue(String::class.java)
                if (!uuid.isNullOrEmpty()) {
                    Toast.makeText(this, "UUID loaded: $uuid", Toast.LENGTH_SHORT).show()
                    startBluetoothAdvertising(uuid)
                } else {
                    Toast.makeText(this, "No UUID found for this user", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "User data not found in database", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startBluetoothAdvertising(uuidString: String) {
        val adapter = bluetoothAdapter ?: return

        if (!adapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1002)
            return
        }

        // ✅ Explicitly check permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Bluetooth permission not granted", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT),
                1001
            )
            return
        }

        try {
            advertiser = adapter.bluetoothLeAdvertiser
            if (advertiser == null) {
                Toast.makeText(this, "BLE advertising not supported", Toast.LENGTH_SHORT).show()
                return
            }

            val parcelUuid = ParcelUuid(UUID.nameUUIDFromBytes(uuidString.toByteArray()))
            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .build()

            val data = AdvertiseData.Builder()
                .addServiceUuid(parcelUuid)
                .setIncludeDeviceName(true)
                .build()

            advertiser?.startAdvertising(settings, data, advertiseCallback)
            Toast.makeText(this, "Advertising UUID: $uuidString", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to start BLE advertising: permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            advertiser?.stopAdvertising(advertiseCallback)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun displayGateStatus(gate: Gate) {
        statusText.text = "Status: ${gate.status}"
    }

    override fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    override fun navigateToHistory() {
        startActivity(Intent(this, HistoryActivity::class.java))
        finish()
    }

    override fun navigateToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }

    override fun navigateToGuestAccess() {
        startActivity(Intent(this, GuestAccessActivity::class.java))
        finish()
    }
}

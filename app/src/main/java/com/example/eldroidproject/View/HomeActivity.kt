package com.example.eldroidproject

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.eldroidproject.Model.Gate
import com.example.eldroidproject.Model.GateRepository
import com.example.eldroidproject.Presenter.HomePresenter
import com.example.eldroidproject.View.HomeView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : Activity(), HomeView.View {

    private lateinit var presenter: HomeView.Presenter
    private lateinit var statusText: TextView
    private lateinit var openGateButton: Button
    private lateinit var historyButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var guestAccessButton: ImageButton

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var advertiser: BluetoothLeAdvertiser? = null
    private var currentServoStatus: String? = null
    private var isConnectedToESP: Boolean = false // Tracks BLE connection to ESP

    private val SERVICE_UUID = ParcelUuid.fromString("0000A001-0000-1000-8000-00805f9b34fb")

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Toast.makeText(this@HomeActivity, "BLE Advertising started", Toast.LENGTH_SHORT).show()
        }

        override fun onStartFailure(errorCode: Int) {
            Toast.makeText(this@HomeActivity, "BLE Advertising failed: $errorCode", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        presenter = HomePresenter(this, GateRepository())

        statusText = findViewById(R.id.gateStatus)
        openGateButton = findViewById(R.id.openGateButton)
        historyButton = findViewById(R.id.home_history)
        profileButton = findViewById(R.id.profile)
        homeButton = findViewById(R.id.home)
        guestAccessButton = findViewById(R.id.guest_access)

        presenter.loadGateStatus()

        // Navigation buttons
        homeButton.setOnClickListener { presenter.onHomeClicked() }
        historyButton.setOnClickListener { presenter.onHistoryClicked() }
        profileButton.setOnClickListener { presenter.onProfileClicked() }
        guestAccessButton.setOnClickListener { presenter.onGuestAccessClicked() }

        // Open gate button disabled by default until BLE connects
        openGateButton.isEnabled = false
        openGateButton.setOnClickListener {
            if (isConnectedToESP) {
                toggleServoStatus()
            } else {
                Toast.makeText(this, "Cannot open gate: BLE not connected", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize Bluetooth
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show()
            return
        }

        checkBluetoothPermissions()
        fetchServoStatusFromFirebase()
    }

    private fun checkBluetoothPermissions() {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 1001)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && !grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            Toast.makeText(this, "Bluetooth permissions are required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        startAdvertising()
    }

    override fun onPause() {
        super.onPause()
        try {
            advertiser?.stopAdvertising(advertiseCallback)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun startAdvertising() {
        val adapter = bluetoothAdapter ?: return

        // ✅ Check permissions (Android 12+ requires BLUETOOTH_ADVERTISE and BLUETOOTH_CONNECT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Missing Bluetooth permissions", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // ✅ Ensure Bluetooth is enabled
        if (!adapter.isEnabled) {
            startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            return
        }

        try {
            advertiser = adapter.bluetoothLeAdvertiser
            if (advertiser == null) {
                Toast.makeText(this, "BLE advertising not supported", Toast.LENGTH_SHORT).show()
                return
            }

            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .build()

            val data = AdvertiseData.Builder()
                .addServiceUuid(SERVICE_UUID) // your fixed UUID
                .setIncludeDeviceName(true)
                .build()


            advertiser?.startAdvertising(settings, data, advertiseCallback)
            val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter
            val deviceName = bluetoothAdapter?.name ?: "UnknownDevice"
            // ✅ Feedback
            Toast.makeText(this, "Started advertising UUID: ${SERVICE_UUID.uuid}", Toast.LENGTH_SHORT).show()

            // ✅ Store advertised UUID + timestamp in Firebase
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val dbRef = FirebaseDatabase.getInstance().getReference("registeredDevices").child(deviceName)
                val advertisedInfo = mapOf(
                    "advertisedUuid" to SERVICE_UUID.uuid.toString(),
                    "userUUID" to user.uid,
                    "advertisedAt" to System.currentTimeMillis()
                )
                dbRef.updateChildren(advertisedInfo)
                    .addOnSuccessListener {
                        Toast.makeText(this, "UUID stored in Firebase", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to store UUID", Toast.LENGTH_SHORT).show()
                    }
            }

        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchServoStatusFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("servoStatus")
        dbRef.get().addOnSuccessListener { snapshot ->
            val servoStatus = snapshot.getValue(String::class.java)
            currentServoStatus = servoStatus ?: "close"
            statusText.text = getString(R.string.gate_status, currentServoStatus)
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to fetch servo status", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleServoStatus() {
        val newStatus = if (currentServoStatus == "open") "close" else "open"
        updateServoStatus(newStatus)
    }

    private fun updateServoStatus(newStatus: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("servoStatus")
        dbRef.setValue(newStatus).addOnSuccessListener {
            statusText.text = getString(R.string.gate_status, newStatus)
            currentServoStatus = newStatus
            Toast.makeText(this, "Gate set to $newStatus", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to change gate status", Toast.LENGTH_SHORT).show()
        }
    }

    // Call this when BLE connects to ESP
    private fun onESPConnected() {
        isConnectedToESP = true
        openGateButton.isEnabled = true
        Toast.makeText(this, "Connected to ESP", Toast.LENGTH_SHORT).show()
    }

    // Call this when BLE disconnects
    private fun onESPDisconnected() {
        isConnectedToESP = false
        openGateButton.isEnabled = false
        Toast.makeText(this, "Disconnected from ESP", Toast.LENGTH_SHORT).show()
    }

    // MVP navigation
    override fun displayGateStatus(gate: Gate) {
        statusText.text = getString(R.string.gate_status, gate.status)
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
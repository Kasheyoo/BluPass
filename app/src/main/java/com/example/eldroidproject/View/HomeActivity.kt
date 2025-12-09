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
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.eldroidproject.Model.AuthRepository
import com.example.eldroidproject.Model.Gate
import com.example.eldroidproject.Model.GateRepository
import com.example.eldroidproject.Presenter.HomePresenter
import com.example.eldroidproject.View.HomeView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class HomeActivity : Activity(), HomeView.View {

    private lateinit var presenter: HomeView.Presenter

    // UI Components
    private lateinit var statusInfoText: TextView
    private lateinit var tvUserName: TextView
    private lateinit var pulseRing: View

    // Navigation Buttons
    private lateinit var historyButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var homeButton: ImageButton
    private lateinit var guestAccessButton: ImageButton

    // Bluetooth
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var advertiser: BluetoothLeAdvertiser? = null
    private val SERVICE_UUID = ParcelUuid.fromString("0000A001-0000-1000-8000-00805f9b34fb")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        try {
            // 1. Initialize Presenter
            presenter = HomePresenter(this, GateRepository(), AuthRepository())

            // 2. Initialize Views
            statusInfoText = findViewById(R.id.statusText)
            pulseRing = findViewById(R.id.pulseRing)
            tvUserName = findViewById(R.id.tvUserName)

            historyButton = findViewById(R.id.home_history)
            profileButton = findViewById(R.id.profile)
            homeButton = findViewById(R.id.home)
            guestAccessButton = findViewById(R.id.guest_access)

            // 3. Navigation Listeners
            homeButton.setOnClickListener { presenter.onHomeClicked() }
            historyButton.setOnClickListener { presenter.onHistoryClicked() }
            profileButton.setOnClickListener { presenter.onProfileClicked() }
            guestAccessButton.setOnClickListener { presenter.onGuestAccessClicked() }

            // 4. Load User Info
            presenter.loadUserInfo()

            // 5. Initialize Bluetooth
            val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager.adapter

            // 6. Check Permissions
            checkBluetoothPermissions()

        } catch (e: Exception) {
            Log.e("HomeActivity", "Error in onCreate: ${e.message}")
        }
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            runOnUiThread {
                statusInfoText.text = "Broadcasting digital key...\nKeep phone near the gate."
            }
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            // ✅ DEBUG CHANGE: I commented this out.
            // If BLE fails, we keep animating so you know the UI is working.
            // stopPulseAnimation()

            runOnUiThread {
                // Show the specific error code so we know WHY it failed
                val errorMsg = when(errorCode) {
                    ADVERTISE_FAILED_DATA_TOO_LARGE -> "Data too large"
                    ADVERTISE_FAILED_FEATURE_UNSUPPORTED -> "BLE Advertising not supported"
                    ADVERTISE_FAILED_INTERNAL_ERROR -> "Internal Bluetooth Error"
                    ADVERTISE_FAILED_TOO_MANY_ADVERTISERS -> "Too many advertisers"
                    else -> "Error Code: $errorCode"
                }
                statusInfoText.text = "Signal Error: $errorMsg"
                Log.e("HomeActivity", "Advertise failed: $errorMsg")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Always try to animate on resume to ensure UI looks alive
        startPulseAnimation()

        if (hasPermissions()) {
            startAdvertising()
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    override fun onPause() {
        super.onPause()
        try {
            if (hasPermissions()) {
                advertiser?.stopAdvertising(advertiseCallback)
            }
        } catch (e: Exception) { e.printStackTrace() }
        stopPulseAnimation()
    }

    // --- Animation Logic (FIXED) ---

    private fun startPulseAnimation() {
        runOnUiThread {
            try {
                // ✅ FORCE the animation. No "if visible" checks.
                pulseRing.visibility = View.VISIBLE
                val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation)
                pulseRing.startAnimation(pulseAnimation)
            } catch (e: Exception) {
                Log.e("HomeActivity", "Animation Error: ${e.message}")
            }
        }
    }

    private fun stopPulseAnimation() {
        runOnUiThread {
            pulseRing.clearAnimation()
            pulseRing.visibility = View.INVISIBLE
        }
    }

    // --- Bluetooth Logic ---

    private fun startAdvertising() {
        val adapter = bluetoothAdapter ?: return

        // 1. Explicit Permission Check to satisfy Lint and prevent crash
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        // 2. Enable Check
        if (!adapter.isEnabled) {
            try {
                startActivity(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            } catch (e: SecurityException) { e.printStackTrace() }
            return
        }

        try {
            advertiser = adapter.bluetoothLeAdvertiser
            if (advertiser == null) {
                // Device doesn't support advertising, but we keep animation running
                statusInfoText.text = "BLE Advertising hardware missing."
                return
            }

            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .build()

            val data = AdvertiseData.Builder()
                .addServiceUuid(SERVICE_UUID)
                .setIncludeDeviceName(true)
                .build()

            advertiser?.startAdvertising(settings, data, advertiseCallback)

            registerDeviceInFirebase()

        } catch (e: Exception) {
            Log.e("HomeActivity", "Error starting advertising: ${e.message}")
        }
    }

    private fun registerDeviceInFirebase() {
        try {
            val user = FirebaseAuth.getInstance().currentUser
            val deviceName = Build.MODEL ?: "UnknownDevice" // Safe Device Name

            if (user != null) {
                val safeDeviceName = deviceName.replace(".", "_").replace("#", "_")
                val dbRef = FirebaseDatabase.getInstance().getReference("registeredDevices").child(safeDeviceName)
                val advertisedInfo = mapOf(
                    "advertisedUuid" to SERVICE_UUID.uuid.toString(),
                    "userUUID" to user.uid,
                    "advertisedAt" to System.currentTimeMillis()
                )
                dbRef.updateChildren(advertisedInfo)
            }
        } catch (e: Exception) {
            Log.e("HomeActivity", "Firebase Error: ${e.message}")
        }
    }

    // --- Permission Helper ---

    private fun hasPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun checkBluetoothPermissions() {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), 1001)
        } else {
            startAdvertising()
            startPulseAnimation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startAdvertising()
                startPulseAnimation()
            }
        }
    }

    // --- MVP Methods ---

    override fun setWelcomeMessage(message: String) {
        runOnUiThread { tvUserName.text = message }
    }

    override fun displayGateStatus(gate: Gate) { }
    override fun navigateToHome() { }
    override fun navigateToHistory() { startActivity(Intent(this, HistoryActivity::class.java)); finish() }
    override fun navigateToProfile() { startActivity(Intent(this, ProfileActivity::class.java)); finish() }
    override fun navigateToGuestAccess() { startActivity(Intent(this, GuestAccessActivity::class.java)); finish() }
}
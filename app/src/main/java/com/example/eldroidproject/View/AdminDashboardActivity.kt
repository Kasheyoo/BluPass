package com.example.eldroidproject.View

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import com.example.eldroidproject.R

class AdminDashboardActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val btnManageHomeowner = findViewById<LinearLayout>(R.id.btnManageHomeowner)
        btnManageHomeowner.setOnClickListener {
            val intent = Intent(this, AdminManageHomeownersActivity::class.java)
            startActivity(intent)
        }
    }
}
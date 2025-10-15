package com.example.eldroidproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class LoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)


        btnSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
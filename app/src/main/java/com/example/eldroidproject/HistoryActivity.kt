package com.example.eldroidproject

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.eldroidproject.Model.HistoryRecord
import com.example.eldroidproject.Model.HistoryRepository
import com.example.eldroidproject.Presenter.HistoryPresenter
import com.example.eldroidproject.View.HistoryView

class HistoryActivity : AppCompatActivity(), HistoryView.View {

    private lateinit var presenter: HistoryView.Presenter
    private lateinit var homeButton: ImageButton
    private lateinit var historyButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var guestAccessButton: ImageButton
    private lateinit var historyContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        presenter = HistoryPresenter(this, HistoryRepository())

        homeButton = findViewById(R.id.home)
        historyButton = findViewById(R.id.home_history)
        profileButton = findViewById(R.id.profile)
        guestAccessButton = findViewById(R.id.guest_access)
        historyContainer = findViewById(R.id.historyContainer)

        homeButton.setOnClickListener { presenter.onHomeClicked() }
        historyButton.setOnClickListener { presenter.onHistoryClicked() }
        profileButton.setOnClickListener { presenter.onProfileClicked() }
        guestAccessButton.setOnClickListener { presenter.onGuestAccessClicked() }

        presenter.loadHistory()
    }

    override fun displayHistory(records: List<HistoryRecord>) {
        historyContainer.removeAllViews()
        for (record in records) {
            val textView = TextView(this)
            textView.text = "${record.type} - ${record.vehicle} - ${record.date}"
            textView.textSize = 14f
            textView.setTextColor(android.graphics.Color.BLACK)
            historyContainer.addView(textView)
        }
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

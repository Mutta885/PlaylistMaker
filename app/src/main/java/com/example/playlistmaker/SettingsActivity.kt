package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbarSets = findViewById<Toolbar>(R.id.my_toolbar)
        val shareBtn = findViewById<FrameLayout>(R.id.share_btn)
        val mailToSupport = findViewById<FrameLayout>(R.id.support)
        val termsOfUse = findViewById<FrameLayout>(R.id.terms_of_use)

        toolbarSets.setNavigationOnClickListener {
            finish()
        }

        shareBtn.setOnClickListener {
            val message = getString(R.string.course_link)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }

        mailToSupport.setOnClickListener {
            val message = getString(R.string.support_message_text)
            val subject = getString(R.string.support_message_subject)
            val mailIntent = Intent(Intent.ACTION_SENDTO)
            mailIntent.data = Uri.parse("mailto:")
            mailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.dev_email)))
            mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            mailIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(mailIntent)
        }

        termsOfUse.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.practicum_offer)))
            startActivity(shareIntent)
        }


    }
}
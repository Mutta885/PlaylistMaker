package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri


import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.SharingRepository

class SharingRepositoryImpl(private val context: Context) : SharingRepository {
    override fun shareApp() {
        val message = context.getString(R.string.course_link)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    override fun mailToSupport() {
        val message = context.getString(R.string.support_message_text)
        val subject = context.getString(R.string.support_message_subject)
        val mailIntent = Intent(Intent.ACTION_SENDTO)
        mailIntent.data = Uri.parse("mailto:")
        mailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.dev_email)))
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        mailIntent.putExtra(Intent.EXTRA_TEXT, message)
        mailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(mailIntent)
    }

    override fun readTerms() {
        val shareIntent = Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.practicum_offer)))
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

}





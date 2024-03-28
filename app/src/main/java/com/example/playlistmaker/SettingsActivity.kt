package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backBtn = findViewById<View>(R.id.back_btn)
        val shareBtn = findViewById<View>(R.id.share_btn)
        val supportBtn = findViewById<View>(R.id.support_btn)
        val agreementBtn = findViewById<View>(R.id.user_agreement_btn)

        backBtn.setOnClickListener {
            this.finish()
        }

        shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.practicum_android_link))
            intent.type = getString(R.string.intent_type_text)
            startActivity(Intent.createChooser(intent, null))
        }

        supportBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse(getString(R.string.mail_intent_data))
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_title))
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            startActivity(intent)
        }

        agreementBtn.setOnClickListener {
            val webpage: Uri = Uri.parse(getString(R.string.user_agreement_link))
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        }

    }
}
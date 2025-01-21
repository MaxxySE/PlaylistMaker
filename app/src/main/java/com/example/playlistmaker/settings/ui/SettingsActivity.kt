package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.data.settings.ConstData
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private val constData = ConstData()

    private lateinit var backBtn : View
    private lateinit var shareBtn : View
    private lateinit var supportBtn : View
    private lateinit var agreementBtn : View
    private lateinit var themeSwitcher : SwitchMaterial
    private lateinit var playlistPrefs : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        playlistPrefs = getSharedPreferences(constData.getPlaylistPref(), MODE_PRIVATE)

        backBtn = findViewById(R.id.back_btn)
        shareBtn = findViewById(R.id.share_btn)
        supportBtn = findViewById(R.id.support_btn)
        agreementBtn = findViewById(R.id.user_agreement_btn)
        themeSwitcher = findViewById(R.id.themeSwitch)

        themeSwitcher.isChecked = playlistPrefs.getBoolean(constData.getThemeSwitchKey(), false)

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

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            playlistPrefs.edit()
                .putBoolean(constData.getThemeSwitchKey(), checked)
                .apply()

            (applicationContext as App).switchTheme(checked)
        }

    }
}
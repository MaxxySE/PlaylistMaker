package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(Creator.provideSettingsInteractor())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener { finish() }

        binding.shareBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, getString(R.string.practicum_android_link))
                type = getString(R.string.intent_type_text)
            }
            startActivity(Intent.createChooser(intent, null))
        }

        binding.supportBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(getString(R.string.mail_intent_data))
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_title))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_message))
            }
            startActivity(intent)
        }

        binding.userAgreementBtn.setOnClickListener {
            val webpage: Uri = Uri.parse(getString(R.string.user_agreement_link))
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        }

        binding.themeSwitch.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeSwitchChanged(checked)
        }

        viewModel.themeState.observe(this) { isChecked ->
            binding.themeSwitch.isChecked = isChecked
            (applicationContext as App).switchTheme(isChecked)
        }
    }
}
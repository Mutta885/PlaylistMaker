package com.example.playlistmaker.settings.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.myToolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel.themeStateLiveData.observe(this){
            state -> binding.themeSwitcher.isChecked = state
        }

        binding.themeSwitcher.setOnCheckedChangeListener { switcher, checked -> viewModel.setTheme(checked)}

        binding.shareBtn.setOnClickListener {
            viewModel.share()
        }

        binding.support.setOnClickListener {
            viewModel.mail()
        }

        binding.termsOfUse.setOnClickListener {
            viewModel.readTerms()
        }


    }
}
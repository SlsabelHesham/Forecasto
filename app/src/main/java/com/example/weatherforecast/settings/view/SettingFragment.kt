package com.example.weatherforecast.settings.view

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.lifecycle.lifecycleScope
import com.example.weatherforecast.utils.Constants.LANG_ARABIC
import com.example.weatherforecast.utils.Constants.LANG_ENGLISH
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentSettingBinding
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.util.*

@Suppress("DEPRECATION")
class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        val preferences = context?.getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = preferences?.edit()

        binding.languageRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton: RadioButton? = view?.findViewById(checkedId)
            lifecycleScope.launch {
                if (radioButton?.text == getString(R.string.arabic)) {
                    AppLanguageManager.updateAppLanguage(LANG_ARABIC)
                    editor?.putString("language", "ar")
                    editor?.apply()
                    convertSettingLanguage(binding)
                } else {
                    AppLanguageManager.updateAppLanguage(LANG_ENGLISH)
                    editor?.putString("language", "en")
                    editor?.apply()
                    convertSettingLanguage(binding)
                }
            }
        }
        binding.tempRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton: RadioButton? = view?.findViewById(checkedId)
            when (radioButton?.text) {
                getString(R.string.kelvin) -> {
                    editor?.putString("temperature", "")
                    editor?.putString("wind", "m/s")
                    editor?.apply()
                    binding.msRBtn.isChecked = true
                }
                getString(R.string.celsius) -> {
                    editor?.putString("temperature", "metric")
                    editor?.putString("wind", "m/s")
                    editor?.apply()
                    binding.msRBtn.isChecked = true
                }
                else -> {
                    editor?.putString("temperature", "imperial")
                    editor?.putString("wind", "m/h")
                    editor?.apply()
                    binding.mhRBtn.isChecked = true
                }
            }
        }
        binding.windSpeedRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton: RadioButton? = view?.findViewById(checkedId)
            if (radioButton?.text == getString(R.string.ms)) {
                editor?.putString("wind", "m/s")
                if (preferences?.getString("temperature", "").toString() == "imperial") {
                    editor?.putString("temperature", "")
                    editor?.apply()
                    binding.kelvinRBtn.isChecked = true
                }
            } else {
                editor?.putString("wind", "m/h")
                editor?.putString("temperature", "imperial")
                editor?.apply()
                binding.fahrenheitRBtn.isChecked = true
            }
        }

        lifecycleScope.launch {
            AppLanguageManager.selectedLanguage.collect { language ->
                if (language == LANG_ENGLISH) {
                    updateAppLanguage(language, View.LAYOUT_DIRECTION_LTR)
                } else {
                    updateAppLanguage(language, View.LAYOUT_DIRECTION_RTL)
                }
            }
        }
        return binding.root
    }

    private fun convertSettingLanguage(binding: FragmentSettingBinding) {
        binding.settings.text = getString(R.string.settings)

        binding.temperatureTV.text = getString(R.string.temperature)
        binding.kelvinRBtn.text = getString(R.string.kelvin)
        binding.celsiusRBtn.text = getString(R.string.celsius)
        binding.fahrenheitRBtn.text = getString(R.string.fahrenheit)

        binding.windSpeedTV.text = getString(R.string.wind_speed)
        binding.msRBtn.text = getString(R.string.ms)
        binding.mhRBtn.text = getString(R.string.mh)

        binding.languageTV.text = getString(R.string.language)
        binding.englishRBtn.text = getString(R.string.english)
        binding.arabicRBtn.text = getString(R.string.arabic)

    }

    private fun updateAppLanguage(language: String, direction: Int) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        requireActivity().window.decorView.layoutDirection = direction
        requireContext().resources.updateConfiguration(
            config,
            requireContext().resources.displayMetrics
        )
    }

    object AppLanguageManager {
        private val _selectedLanguage = MutableSharedFlow<String>()
        val selectedLanguage: SharedFlow<String> = _selectedLanguage

        suspend fun updateAppLanguage(language: String) {
            _selectedLanguage.emit(language)
        }
    }
}
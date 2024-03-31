package com.example.weatherforecast.settings.view

import android.content.Context
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
                    convertSettingLanguage(binding)
                } else {
                    AppLanguageManager.updateAppLanguage(LANG_ENGLISH)
                    convertSettingLanguage(binding)
                }
            }
        }

        binding.tempRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton: RadioButton? = view?.findViewById(checkedId)
            when (radioButton?.text) {
                getString(R.string.kelvin) -> {
                    editor?.putString("unit", "")
                    editor?.apply()
                    binding.msRBtn.isChecked = true
                }
                getString(R.string.celsius) -> {
                    editor?.putString("unit", "metric")
                    editor?.apply()
                    binding.msRBtn.isChecked = true
                }
                else -> {
                    editor?.putString("unit", "imperial")
                    editor?.apply()
                    binding.mhRBtn.isChecked = true
                }
            }
        }
        binding.windSpeedRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton: RadioButton? = view?.findViewById(checkedId)
            if (radioButton?.text == getString(R.string.ms)) {
                if (preferences?.getString("unit", "").toString() == "imperial") {
                    editor?.putString("unit", "")
                    editor?.apply()
                    binding.kelvinRBtn.isChecked = true
                }
            } else {
                editor?.putString("unit", "imperial")
                editor?.apply()
                binding.fahrenheitRBtn.isChecked = true
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
    object AppLanguageManager {
        private val _selectedLanguage = MutableSharedFlow<String>()
        val selectedLanguage: SharedFlow<String> = _selectedLanguage

        suspend fun updateAppLanguage(language: String) {
            _selectedLanguage.emit(language)
        }
    }
}
package com.lgtm.simple_speedometer

import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat

class SettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_setting, rootKey)

        findPreference<SwitchPreferenceCompat>(getString(R.string.key_sp_keep_the_screen_on))?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue == true) {
                Toast.makeText(activity, "화면을 켜진 상태로 유지합니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "화면의 켜진 상태를 유지하지 않습니다.", Toast.LENGTH_SHORT).show()
            }

            true
        }

    }

    companion object {
        fun newInstance() = SettingFragment()
    }

}
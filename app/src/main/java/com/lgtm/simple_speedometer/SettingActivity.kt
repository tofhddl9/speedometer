package com.lgtm.simple_speedometer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lgtm.simple_speedometer.databinding.ActivitySettingBinding

class SettingActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySettingBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, SettingFragment.newInstance())
            .commit()
    }
}
package com.lgtm.simple_speedometer

import android.Manifest
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.MenuItem
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.location.LocationListenerCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.model.LatLng
import com.lgtm.simple_speedometer.databinding.ActivitySpeedometerBinding
import com.lgtm.simple_speedometer.permission.PermissionManager
import com.lgtm.simple_speedometer.utils.locationManager
import java.util.*

class SpeedometerActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener, LocationListenerCompat {

    private val binding by lazy { ActivitySpeedometerBinding.inflate(layoutInflater) }

    private val exitToast: Toast by lazy {
        Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG)
    }

    private var permissionManager = PermissionManager(this)

    private var isTerminateMessageShown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initMenu()
        initSpeedometer()
    }

    private fun initMenu() {
        binding.menuButton.setOnClickListener {
            PopupMenu(this, it).apply {
                setOnMenuItemClickListener(this@SpeedometerActivity)
                inflate(R.menu.menu_speedometer)
                show()
            }
        }
    }

    private fun initSpeedometer() {
        showRequiredPermissionPopup()
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 16, 10f, this)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 16, 10f, this)
        } catch (e: SecurityException) {
            Toast.makeText(this, "위치 정보 업데이트에 실패했습니다.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onLocationChanged(location: Location) {
        updateSpeedometerView(location)
        updateAddressInfoView(location)
    }

    private fun updateSpeedometerView(location: Location) {
        binding.speedometerView.currentSpeed = location.speed * 3.6f
    }

    private fun updateAddressInfoView(location: Location) {
        val position = LatLng(location.latitude, location.longitude)
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(position.latitude, position.longitude, 1)
            .getOrNull(0)

        binding.addressView.text = if (address != null) {
            "${address.subLocality ?: ""} ${address.thoroughfare ?: ""}"
        } else {
            "-"
        }
    }

    private fun showRequiredPermissionPopup() {
        permissionManager.setPermissions(PERMISSIONS_REQUEST_CODE, REQUIRED_PERMISSIONS)
            .onPermissionDenied { showPermissionSupportDialog() }
            .request()
    }

    private fun showPermissionSupportDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("앱 권한 설정 안내")
            setMessage("속도계를 정상적으로 사용하기 위해 애플리케이션 정보 > 권한의 모든 권한이 필요합니다")
            setPositiveButton("권한설정") { dialog, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                    Uri.parse("package:" + applicationContext.packageName)
                )
                startActivity(intent)
                dialog.cancel()
            }
            setNegativeButton("취소") { dialog, _ ->
                dialog.cancel()
            }
        }.show()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager.handleRequestPermissionResult(requestCode, grantResults)
    }

    override fun onResume() {
        super.onResume()

        applySettings()
    }

    private fun applySettings() {
        val sp = PreferenceManager.getDefaultSharedPreferences(this)

        updateKeepTheScreenOn(sp.getBoolean(getString(R.string.key_sp_keep_the_screen_on), false))
    }

    private fun updateKeepTheScreenOn(isKeepTheScreenOn: Boolean) {
        if (isKeepTheScreenOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
                true
            }
            else -> false
        }
    }

    override fun onBackPressed() {
        if (!isTerminateMessageShown) {
            isTerminateMessageShown = true
            exitToast.show()
            Handler(Looper.getMainLooper()).postDelayed({ isTerminateMessageShown = false }, 3000)
        } else {
            exitToast.cancel()
            finish()
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_CODE: Int = 1024

        val REQUIRED_PERMISSIONS : Array<String> = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

}

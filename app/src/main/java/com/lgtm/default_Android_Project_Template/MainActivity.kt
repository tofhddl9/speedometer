package com.lgtm.default_Android_Project_Template

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.lgtm.default_Android_Project_Template.databinding.ActivityMainBinding
import com.lgtm.default_Android_Project_Template.permission.PermissionManager

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val exitToast: Toast by lazy {
        Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_LONG)
    }

    private var permissionManager = PermissionManager(this)

    private var isTerminateMessageShown: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        showRequiredPermissionPopup()
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

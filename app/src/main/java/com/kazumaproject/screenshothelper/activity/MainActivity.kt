package com.kazumaproject.screenshothelper.activity

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.kazumaproject.screenshothelper.R
import com.kazumaproject.screenshothelper.databinding.ActivityMainBinding
import com.kazumaproject.screenshothelper.service.FloatingService
import com.kazumaproject.screenshothelper.service.NavigationService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        if (Settings.canDrawOverlays(this) && NavigationService.navigationService != null){
            hideViews()
            binding.progress.isVisible = true
            CoroutineScope(Dispatchers.Default).launch {
                delay(1000)
                if (isMyServiceRunning(FloatingService::class.java)){
                    val intent = Intent(this@MainActivity, FloatingService::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    this@MainActivity.stopService(intent)
                }else{
                    val intent = Intent(this@MainActivity, FloatingService::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    this@MainActivity.startService(intent)
                }
                this@MainActivity.finish()
            }
        } else {
            binding.progress.isVisible = false
            showViews()

            if (Settings.canDrawOverlays(this)){
                binding.ivPermissionStatus1.setImageResource(R.drawable.ic_check)
                binding.tvStatus1.text = "Granted"
            }else {
                binding.ivPermissionStatus1.setImageResource(R.drawable.ic_cross)
                binding.tvStatus1.text = "Not Granted"
            }

            if (NavigationService.navigationService != null){
                binding.ivPermissionStatus2.setImageResource(R.drawable.ic_check)
                binding.tvStatus2.text = "Granted"
            }else {
                binding.ivPermissionStatus2.setImageResource(R.drawable.ic_cross)
                binding.tvStatus2.text = "Not Granted"
            }

        }


        binding.btnPermission1.setOnClickListener {
            requestDrawOverOtherAppPermission()
        }

        binding.btnPermission2.setOnClickListener {
            requestAccessibility()
        }

        binding.srlMain.setOnRefreshListener {
            recreate()
            binding.srlMain.isRefreshing = false
        }

    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun requestDrawOverOtherAppPermission(){
        if (!Settings.canDrawOverlays(this)){
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = Uri.parse("package:" + this.packageName)
            this.startActivity(intent)
        }
    }

    private fun requestAccessibility() {
        val intent = Intent("android.settings.ACCESSIBILITY_SETTINGS")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun showViews(){
        binding.tvHeader.isVisible = true
        binding.tvHeader.isVisible = true
        binding.tvPermission1.isVisible = true
        binding.tvStatus1.isVisible = true
        binding.ivPermissionStatus1.isVisible = true
        binding.btnPermission1.isVisible = true
        binding.tvPermission2.isVisible = true
        binding.btnPermission2.isVisible = true
        binding.ivPermissionStatus2.isVisible = true
        binding.tvStatus2.isVisible = true
    }

    private fun hideViews(){
        binding.tvHeader.isVisible = false
        binding.tvHeader.isVisible = false
        binding.tvPermission1.isVisible = false
        binding.tvStatus1.isVisible = false
        binding.ivPermissionStatus1.isVisible = false
        binding.btnPermission1.isVisible = false
        binding.tvPermission2.isVisible = false
        binding.btnPermission2.isVisible = false
        binding.ivPermissionStatus2.isVisible = false
        binding.tvStatus2.isVisible = false
    }

}
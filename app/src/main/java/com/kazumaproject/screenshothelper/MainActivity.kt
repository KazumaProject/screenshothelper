package com.kazumaproject.screenshothelper

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val mainViewModel: MainViewModel by viewModels()

    private lateinit var rootLayout: ConstraintLayout
    private lateinit var progress: ProgressBar
    private lateinit var tvHeader: TextView
    private lateinit var tvPermission1: TextView
    private lateinit var tvPermission2: TextView
    private lateinit var ivPermission1: ImageView
    private lateinit var ivPermission2: ImageView
    private lateinit var btnPermission1: Button
    private lateinit var btnPermission2: Button
    private lateinit var tvPermissionStatus1: TextView
    private lateinit var tvPermissionStatus2: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViews()
        supportActionBar?.hide()

        mainViewModel.bothPermissions.observe(this,{
            if (it){
                this.finish()
            }else {
                showViews()
            }
        })

        mainViewModel.drawOverOtherAppPermission.observe(this, {

        })

        mainViewModel.accessibilityServicePermission.observe(this,{

        })

    }

    private fun findViews(){
        rootLayout = findViewById(R.id.cl_parent_layout)
        progress = findViewById(R.id.progress)
        tvHeader = findViewById(R.id.tv_header)
        tvPermission1 = findViewById(R.id.tv_permission_1)
        tvPermission2 = findViewById(R.id.tv_permission_2)
        tvPermissionStatus1 = findViewById(R.id.tv_status_1)
        tvPermissionStatus2 = findViewById(R.id.tv_status_2)
        ivPermission1 = findViewById(R.id.iv_permission_status_1)
        ivPermission2 = findViewById(R.id.iv_permission_status_2)
        btnPermission1 = findViewById(R.id.btn_permission_1)
        btnPermission2 = findViewById(R.id.btn_permission_2)
    }

    private fun showViews(){
        tvHeader.isVisible = true
        tvPermission1.isVisible = true
        tvPermission2.isVisible = true
        tvPermissionStatus1.isVisible = true
        tvPermissionStatus2.isVisible = true
        ivPermission1.isVisible = true
        ivPermission2.isVisible = true
        btnPermission1.isVisible = true
        btnPermission2.isVisible = true
    }

    private fun hideViews(){
        tvHeader.isVisible = false
        tvPermission1.isVisible = false
        tvPermission2.isVisible = false
        tvPermissionStatus1.isVisible = false
        tvPermissionStatus2.isVisible = false
        ivPermission1.isVisible = false
        ivPermission2.isVisible = false
        btnPermission1.isVisible = false
        btnPermission2.isVisible = false
    }

}
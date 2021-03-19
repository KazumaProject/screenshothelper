package com.kazumaproject.screenshothelper.service

import android.accessibilityservice.AccessibilityService
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.kazumaproject.screenshothelper.R
import com.kazumaproject.screenshothelper.activity.MainActivity
import com.kazumaproject.screenshothelper.activity.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class FloatingService: Service() {

    private lateinit var rootView: View
    private lateinit var iconView: ImageView

    private lateinit var windowManager: WindowManager
    private lateinit var layoutparams: WindowManager.LayoutParams

    var initialX = 0
    var initialY = 0
    var initialTouchX = 0F
    var initialTouchY = 0F

    var visibleState = true

    private val gestureDetector = GestureDetector(this.baseContext , object : GestureDetector.SimpleOnGestureListener(){

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {

            if (NavigationService.navigationService == null){
                requestAccessibility()
            }else {
                CoroutineScope(Dispatchers.Default).launch {
                    iconView.alpha = 0f
                    delay(500)
                    NavigationService.navigationService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT)
                    delay(2000)
                    iconView.alpha = 1.0f
                    visibleState = true
                }
            }
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            super.onLongPress(e)
            if (visibleState){
                iconView.alpha = 0.3f
                visibleState = false
            }else {
                iconView.alpha = 1.0f
                visibleState = true
            }
        }

    })

    private fun requestAccessibility() {
        val intent = Intent("android.settings.ACCESSIBILITY_SETTINGS")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        rootView = LayoutInflater.from(this).inflate(R.layout.service_layout, null)
        iconView = rootView.findViewById(R.id.touch_img)

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        layoutparams = WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        )
        windowManager.addView(rootView, layoutparams)

        iconView.setOnTouchListener { v, event ->

            gestureDetector.onTouchEvent(event)

            when(event.action){
                MotionEvent.ACTION_DOWN ->{
                    initialX = layoutparams.x
                    initialY = layoutparams.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_UP ->{

                    val height = displayMetrics.heightPixels
                    val heightSize = 4
                    val heightDif = height - heightSize
                    val width = displayMetrics.widthPixels
                    val detectionX = width/2
                    val valueAnimator: ValueAnimator?

                    when{
                        event.rawX > detectionX &&
                        event.rawY > heightSize &&
                        event.rawY < heightDif ->{
                            valueAnimator = ValueAnimator.ofFloat(layoutparams.x.toFloat(), width.toFloat() - detectionX)
                            valueAnimator?.let {
                                it.duration = 500
                                it.addUpdateListener { animation ->
                                    layoutparams.x = (animation.animatedValue as Float).roundToInt()
                                    windowManager.updateViewLayout(rootView,layoutparams)
                                }
                            }
                            valueAnimator?.start()
                        }

                        event.rawX < detectionX &&
                        event.rawY > heightSize &&
                        event.rawY < heightDif ->{
                            valueAnimator = ValueAnimator.ofFloat(layoutparams.x.toFloat(), 0.0f - detectionX)
                            valueAnimator?.let {
                                it.duration = 500
                                it.addUpdateListener { animation ->
                                    layoutparams.x = (animation.animatedValue as Float).roundToInt()
                                    windowManager.updateViewLayout(rootView,layoutparams)
                                }
                            }
                            valueAnimator?.start()
                        }

                    }

                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_MOVE ->{
                    layoutparams.x = initialX + (event.rawX - initialTouchX).toInt()
                    layoutparams.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(rootView,layoutparams)
                    return@setOnTouchListener true
                }
            }

            return@setOnTouchListener false
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(rootView)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setForeGroundNotification()
        return START_STICKY
    }

    private fun setForeGroundNotification() {
        val NOTIFICATION_CHANNEL_ID = "Screenshot Helper"
        val ChannelName = "Screenshot Helper"
        val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                ChannelName,
                NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.lightColor = R.color.purple_200
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(notificationChannel)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val intent2 = Intent(this, SettingsActivity::class.java)
        intent2.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent2 = PendingIntent.getActivity(this, 0, intent2, PendingIntent.FLAG_CANCEL_CURRENT)
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder
                .setOngoing(true)
                .setContentTitle("Screenshot Helper is active.")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setSmallIcon(R.drawable.logo2)
                .setShowWhen(false)
                .setStyle(NotificationCompat.BigTextStyle().bigText("Single tap -> Take a screenshot \n" +
                        "Long tap -> Toggle transparent"))
                .addAction(R.drawable.logo2,"Stop",pendingIntent)
                .addAction(R.drawable.logo2,"Setting",pendingIntent2)
                .build()
        startForeground(2, notification)
    }
}
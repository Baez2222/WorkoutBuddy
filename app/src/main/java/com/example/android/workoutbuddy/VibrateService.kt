package com.example.android.workoutbuddy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


class VibrateService: Service(){

//    private val tickIntent = Intent(ACTION_TICK)
//
//    private val finishedIntent = Intent(ACTION_FINISHED)
//
//    private lateinit var timer: CountDownTimer

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private val CHANNEL_ID = "ForegroundService Kotlin"
    companion object {
        private var isRunning: Boolean = false

        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, VibrateService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context, shouldVibrate: Boolean) {
            if(shouldVibrate){
                val vibratorService = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibratorService.vibrate(VibrationEffect.createOneShot(1000, 150))
            }

            val stopIntent = Intent(context, VibrateService::class.java)
            context.stopService(stopIntent)
        }

        fun isRunning(): Boolean {
            return isRunning
        }
    }

    override fun onDestroy() {
        isRunning = false
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //do heavy work on a background thread
        isRunning = true
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()

        val mChannel = NotificationChannel(CHANNEL_ID, "Notifications", NotificationManager.IMPORTANCE_MIN)
        mChannel.enableLights(false)
        mChannel.vibrationPattern = longArrayOf(0L)
        mChannel.enableVibration(true)
        val notificationManager = NotificationManagerCompat.from(this).createNotificationChannel(mChannel)
        Log.println(Log.DEBUG, "should vibrate: ", mChannel.shouldVibrate().toString())

        val notificationIntent = Intent(this, StartWorkoutMainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0
        )
//        notification = NotificationCompat.Builder(this, CHANNEL_ID2)
//                .setContentTitle("Foreground Service Kotlin Example")
//                .setContentText(input)
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentIntent(pendingIntent)
//                .setVibrate(longArrayOf(150L))
//                .build()
//        startForeground(1, notification)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Rest Timer Foreground Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)

        return START_STICKY
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
        val manager = getSystemService(NotificationManager::class.java)
        manager!!.createNotificationChannel(serviceChannel)
    }




}
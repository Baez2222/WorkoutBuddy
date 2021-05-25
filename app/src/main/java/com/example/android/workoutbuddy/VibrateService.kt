package com.example.android.workoutbuddy

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.TimeUnit


class VibrateService: Service(){
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private val CHANNEL_ID = "ForegroundService Kotlin"

    companion object {

        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, VibrateService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context, shouldVibrate: Boolean) {
            if(shouldVibrate){
                val vibratorService = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibratorService.vibrate(VibrationEffect.createOneShot(1000, 100))
            }

            val stopIntent = Intent(context, VibrateService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //do heavy work on a background thread
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()

        val mChannel = NotificationChannel(CHANNEL_ID, "Notifications", NotificationManager.IMPORTANCE_MIN)
        mChannel.enableLights(false)
        mChannel.vibrationPattern = longArrayOf(0L)
        mChannel.enableVibration(true)
        val notificationManager = NotificationManagerCompat.from(this).createNotificationChannel(mChannel)
        Log.println(Log.DEBUG, "should vibrate: ", mChannel.shouldVibrate().toString())

        val notificationIntent = Intent(this, StartWorkoutActivity::class.java)
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
                .setContentTitle("Foreground Service Kotlin Example")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }

}
//class VibrateService: Service() {
//    companion object{
//        var vibratorService: Vibrator? = null
//    }
//    private var mgr : PowerManager.WakeLock? = null
//
//    override fun onBind(p0: Intent?): IBinder? {
//        return null
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//        return START_STICKY
//    }
//
//    override fun onDestroy() {
//        val mgr : PowerManager.WakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "vibrate:mytag")
//        mgr.acquire(60*1000L)
//        vibratorService?.vibrate(VibrationEffect.createOneShot(1000, 100))
//        mgr.release()
//        super.onDestroy()
//    }
//}
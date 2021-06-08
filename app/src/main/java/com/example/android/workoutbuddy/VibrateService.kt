package com.example.android.workoutbuddy

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.concurrent.TimeUnit


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

//        const val ACTION_TICK: String = "your.pkg.name.ACTION_TICK"
//        const val ACTION_FINISHED: String = "your.pkg.name.ACTION_FINISHED"

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
                .setContentTitle("Rest Timer Foreground Service")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)



//        val timer = CounterClass(90000, 1000, this@VibrateService)
//        timer.start()

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }


    class CounterClass(millisInFuture: Long, countDownInterval: Long, context_: Context) : CountDownTimer(millisInFuture, countDownInterval) {
        private val context = context_

        override fun onTick(millisUntilFinished: Long) {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)%60
            var time = ""
            if (seconds < 10){
                time = "00:0" + seconds.toString()
            }
            else{
                time = "$minutes:$seconds"
            }


            val timerInfoIntent = Intent("TIME_INFO")
            timerInfoIntent.putExtra("VALUE", time)
            LocalBroadcastManager.getInstance(context).sendBroadcast(timerInfoIntent)
        }

        override fun onFinish() {
            val timerInfoIntent = Intent("TIME_INFO")
            timerInfoIntent.putExtra("VALUE", "Completed")
            LocalBroadcastManager.getInstance(context).sendBroadcast(timerInfoIntent)
            stopService(context, true)
        }
    }


//    private fun createTimer(COUNTDOWN_LENGTH: Long): CountDownTimer =
//        object : CountDownTimer(COUNTDOWN_LENGTH, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                tickIntent.putExtra("timeLeft", millisUntilFinished)
//                sendBroadcast(tickIntent)
//            }
//
//            override fun onFinish() {
//                sendBroadcast(finishedIntent)
//                stopSelf() // Stop the service within itself NOT the activity
//            }
//        }



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
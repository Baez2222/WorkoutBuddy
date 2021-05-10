package com.example.android.workoutbuddy

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*


class VibrateService: Service() {
    companion object{
        var vibratorService: Vibrator? = null
    }
    private var mgr : PowerManager.WakeLock? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        vibratorService = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        mgr = (getSystemService(Context.POWER_SERVICE) as PowerManager).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "vibrate:mytag")
        mgr?.acquire(10*60*1000L /*10 minutes*/)


        return START_STICKY
    }

    override fun onDestroy() {
        vibratorService?.vibrate(VibrationEffect.createOneShot(1000, 100))
        mgr?.release()
        super.onDestroy()
    }
}
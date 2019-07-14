package com.youngwon.mediacollector

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat

class NotificationHelper(private val mContext: Context) {
    private var mBuilder: NotificationCompat.Builder? = null
    private var mNotificationManager:NotificationManager  = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    /**
     * Create and push the notification
     */
    fun createNotification(title: String, message: String) {
        /**Creates an explicit intent for an Activity in your app */
        val resultIntent = Intent(mContext, StartActivity::class.java)
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val resultPendingIntent = PendingIntent.getActivity(
            mContext,
            0 ,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        mBuilder = NotificationCompat.Builder(mContext)
        mBuilder!!.setSmallIcon(R.mipmap.sym_def_app_icon)
        mBuilder!!.setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(false)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setContentIntent(resultPendingIntent)
            .setOngoing(true);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance)
            mBuilder!!.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager!!.createNotificationChannel(notificationChannel)
        }
        mNotificationManager!!.notify(0 /* Request Code */, mBuilder!!.build())
    }

    fun deleteNotification() {
        mNotificationManager?.cancelAll()
    }
    companion object {
        val NOTIFICATION_CHANNEL_ID = "10001"
    }
}
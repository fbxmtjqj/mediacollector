package com.youngwon.mediacollector

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationHelper(private val mContext: Context) {
    private var mBuilder: NotificationCompat.Builder? = null
    private var mNotificationManager:NotificationManager  = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    /**
     * Create and push the notification
     */
    fun createNotification(title: String, message: String?, channelId:Int) {
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

        mBuilder = NotificationCompat.Builder(mContext, mContext.toString())
        mBuilder!!.setSmallIcon(R.mipmap.sym_def_app_icon)
        mBuilder!!.setContentTitle(title)
            .setAutoCancel(false)
            .setContentIntent(resultPendingIntent)
            .setOngoing(true)
        if(message != null) {
            mBuilder!!.setContentText(message)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId.toString(), "NOTIFICATION_CHANNEL_NAME", importance)
            mBuilder!!.setChannelId(channelId.toString())
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(0 /* Request Code */, mBuilder!!.build())
    }

    fun deleteNotification(channelId:Int) {
        mNotificationManager.cancelAll()
    }
}

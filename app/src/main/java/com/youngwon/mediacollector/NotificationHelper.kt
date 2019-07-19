package com.youngwon.mediacollector

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class NotificationHelper(private val mContext: Context) {

    private var mNotificationManager  = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun createNotification(title: String, message: String?, channelId:Int) {
        val resultIntent = Intent(mContext, StartActivity::class.java)
            .setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val resultPendingIntent = PendingIntent.getActivity(
            mContext,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val mBuilder = NotificationCompat.Builder(mContext, mContext.toString())
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
        mBuilder.setContentTitle(title)
            .setAutoCancel(false)
            .setContentIntent(resultPendingIntent)
            .setOngoing(true)
        if(message != null) {
            mBuilder.setContentText(message)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel(channelId.toString(), "자동다운로드", importance)
            mBuilder.setChannelId(channelId.toString())
            mNotificationManager.createNotificationChannel(notificationChannel)
        }
        mNotificationManager.notify(0, mBuilder.build())
    }

    fun deleteNotification() {
        mNotificationManager.cancelAll()
    }
}

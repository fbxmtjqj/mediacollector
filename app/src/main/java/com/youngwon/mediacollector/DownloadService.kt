package com.youngwon.mediacollector

import android.app.Activity
import android.app.Service
import android.content.ClipboardManager
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.ClipData
import android.content.Context
import android.util.Log


class DownloadService : ClipboardManager.OnPrimaryClipChangedListener,Service() {

    var mManager: ClipboardManager? = null

    override fun onPrimaryClipChanged() {
        if (mManager != null && mManager!!.getPrimaryClip() != null) {
            val data = mManager?.getPrimaryClip()?.getItemAt(0)?.getText()
            Log.e("클립보드 테스트","$data")
        }
    }

    private val mIBinder = MyBinder()

    internal inner class MyBinder : Binder() {
        val service: DownloadService
            get() = this@DownloadService
    }
    override fun onBind(intent: Intent): IBinder {
        return mIBinder
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        mManager?.addPrimaryClipChangedListener(this)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mManager?.removePrimaryClipChangedListener(this);
    }

    override fun onUnbind(intent: Intent): Boolean {
        return super.onUnbind(intent)
    }

    fun onHandleIntent(intent: Intent) {
        val intent:Intent = Intent("test code")
        intent.putExtra("test",Activity.RESULT_OK)
        intent.putExtra("testcode","값 전달 확인좀")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}

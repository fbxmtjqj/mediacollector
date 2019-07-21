package com.youngwon.mediacollector

import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.*


class DownloadService : ClipboardManager.OnPrimaryClipChangedListener,Service() {

    private var mManager: ClipboardManager? = null
    private var i = true

    override fun onBind(intent: Intent): IBinder {
        return mMessenger.binder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        mManager?.addPrimaryClipChangedListener(this)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        mManager?.removePrimaryClipChangedListener(this)
    }

    override fun onPrimaryClipChanged() {
        if (mManager != null && mManager!!.primaryClip != null) {
            val data = mManager?.primaryClip?.getItemAt(0)?.text
            if(data!!.contains("http",true)) {
                i = if (i) {
                    sendMsgToActivity(data as String)
                    false
                } else {
                    true
                }
            }
        }
    }

    private val mMessenger = Messenger(Handler(Handler.Callback { msg ->
        when (msg.what) {
            DownloadService().msgRegisterClient -> {
                mClient = msg.replyTo
            }
        }
        false
    }))

    val sendToActivity = 4
    val msgRegisterClient = 1
    private var mClient: Messenger? = null
    private fun sendMsgToActivity(sendUrl: String) {
        try {
            val bundle = Bundle()
            bundle.putString("url", sendUrl)
            val msg = Message.obtain(null, sendToActivity)
            msg.data = bundle
            if (mClient != null) {
                mClient!!.send(msg)
            }      // msg 보내기
        } catch (e: RemoteException) {
        }
    }
}

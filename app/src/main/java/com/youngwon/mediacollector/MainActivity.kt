package com.youngwon.mediacollector

import android.content.*
import android.os.*
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_main.*
import java.io.*
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val externalStoragePath = Environment.getExternalStorageDirectory().toString()
    private var oldFilePath = externalStoragePath
    private var newFilePath = externalStoragePath
    private var mediaRvPath = externalStoragePath
    private var media = ArrayList<CheckClass>()
    private var mediaRVTemp = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

        val mainFolder = PreferenceManager.getDefaultSharedPreferences(this@MainActivity).getString("DownloadFolder", "MediaDownloader")
        val settings: SharedPreferences = getSharedPreferences("dico", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = settings.edit()
        val fileUrl = arrayListOf<CheckClass>()
        val temp = arrayListOf<CheckClass>()

        MediaView.setOnClickListener{
            startActivity(Intent(this@MainActivity,MediaActivity::class.java))
            setStopService()
        }

        HistoryView.setOnClickListener{
            startActivity(Intent(this@MainActivity,HistoryActivity::class.java))
            setStopService()
        }

        if(settings.getBoolean("switch", false)) {
            setStartService()
            ActiveText.text = "활성화"
            ActiveSwitch.isChecked = true
        } else {
            ActiveText.text = "비활성화"
            ActiveSwitch.isChecked = false
        }
        //스위치버튼 클릭시 활성화로 텍스트변경
        ActiveSwitch.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked){ //만약 스위치를 On시킨다면
                ActiveText.text = "활성화"
                editor.putBoolean("switch", isChecked)
                editor.apply()
                setStartService()
            }
            else { //만약 스위치를 Off시킨다면
                ActiveText.text = "비활성화"
                editor.putBoolean("switch", isChecked)
                editor.commit()
                setStopService()
            }
        }

        if(settings.getBoolean("first", true)) {
            if (!File("$externalStoragePath/MediaDownloader").exists()) {
                File("$externalStoragePath/MediaDownloader").mkdir()
            }
            editor.putBoolean("first", false)
            editor.putString("FolderName", "MediaDownloader")
            editor.apply()
        }

        if (intent.hasExtra("setting")) {
            val oldParentFolder = settings.getString("FolderName", "MediaDownloader")
            if(oldParentFolder != mainFolder) {
                newFilePath = newFilePath + "/" +  PreferenceManager.getDefaultSharedPreferences(this@MainActivity).getString("DownloadFolder", "MediaDownloader")
                oldFilePath = oldFilePath + "/" + settings.getString("FolderName", "MediaDownloader")
                editor.putString("FolderName", mainFolder)
                editor.apply()
                FileMoveAsyncTask(this).execute("")
            }
        }

        try {
            val br = BufferedReader(FileReader(filesDir.toString() + "history.txt"))
            var str = br.readLine()
            while (str != null) {
                temp.add(0,CheckClass(str))
                str = br.readLine()
            }
            br.close()
        } catch (e: FileNotFoundException) {
        }

        for(i in temp.indices) {
            if(i >=5) {
                break
            }
            fileUrl.add(CheckClass(temp[i].str))
        }

        history_recycleview_text.adapter = RecycleViewAdapter(2, fileUrl,this@MainActivity,null)
        history_recycleview_text.layoutManager = LinearLayoutManager(this@MainActivity)
        history_recycleview_text.setHasFixedSize(true)

        mediaRvPath = "$mediaRvPath/$mainFolder/"
        mediaRV("")
        recycleview.adapter = RecycleViewAdapter(1, media,this@MainActivity,null)
        recycleview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        recycleview.setHasFixedSize(true)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val settings: SharedPreferences = getSharedPreferences("dico", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = settings.edit()
        val alert = AlertDialog.Builder(this@MainActivity)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            alert.setMessage("정말로 종료하시겠습니까?")
            alert.setPositiveButton("종료") { _, _ ->
                NotificationHelper(this@MainActivity).deleteNotification()
                setStopService()
                editor.putBoolean("switch", false)
                editor.apply()
                super.onBackPressed()
            }
            alert.setNegativeButton("취소") { _, _ ->
            }
            val dialog:AlertDialog = alert.create()
            dialog.show()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this@MainActivity,MainActivity::class.java))
            }
            R.id.nav_download -> {
                startActivity(Intent(this@MainActivity, DownloadActivity::class.java))
                setStopService()
            }
            R.id.nav_file -> {
                startActivity(Intent(this@MainActivity, MediaActivity::class.java))
                setStopService()
            }
            R.id.nav_history -> {
                startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
                setStopService()
            }
            R.id.nav_setting -> {
                startActivity(Intent(this@MainActivity,SettingsActivity::class.java))
                setStopService()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private var isBind: Boolean = false
    private var mServiceMessenger: Messenger? = null
    private val mConnection = object : ServiceConnection {
        //서비스가 실행될 때 호출
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mServiceMessenger = Messenger(service)
            val msg = Message.obtain(null, DownloadService().msgRegisterClient)
            msg.replyTo = mMessenger
            mServiceMessenger?.send(msg)
        }
        //서비스가 종료될 때 호출
        override fun onServiceDisconnected(name: ComponentName) {
            isBind = false
        }
    }

    private fun setStartService() {
        NotificationHelper(this@MainActivity).createNotification("자동다운로드",null,5)
        startService(Intent(this@MainActivity, DownloadService::class.java)) // 서비스 시작
        bindService(Intent(this@MainActivity, DownloadService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        isBind = true
    }

    private fun setStopService() {
        if(isBind) {
            unbindService(mConnection)
            isBind = false
        }
        NotificationHelper(this@MainActivity).deleteNotification()
        stopService(Intent(this@MainActivity, DownloadService::class.java)) // 서비스 종료
    }

    private val mMessenger = Messenger(Handler(Handler.Callback { msg ->
        when (msg.what) {
            DownloadService().sendToActivity -> {
                val url = msg.data.getString("url")
                Toast.makeText(this@MainActivity,"URL 복사됨", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@MainActivity,DownloadActivity::class.java).putExtra("url",url))
                setStopService()
            }
        }
        false
    }))

    private class FileMoveAsyncTask internal constructor(context: MainActivity): AsyncTask<String, String, Boolean>() {

        private val activity: WeakReference<MainActivity> = WeakReference(context)
        private val dialogView: WeakReference<View> = WeakReference(LayoutInflater.from(context).inflate(R.layout.progressbar, context.findViewById(R.id.main),false))
        private val alert: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView.get()).setCancelable(false)
        private val dialog: AlertDialog = alert.create()

        override fun onPreExecute() {
            super.onPreExecute()
            dialog.show()
        }

        override fun doInBackground(vararg url: String?): Boolean {
            activity.get()!!.fileMove("")
            dialog.dismiss()
            return true
        }
    }

    private fun fileMove(name: String){
        oldFilePath = "$oldFilePath/$name"
        newFilePath = "$newFilePath/$name"
        val files = File(oldFilePath).listFiles()
        if(files != null) {
            for (i in files.indices) {
                if (File(files[i].name).isDirectory) {
                    if (!File(newFilePath + "/" + files[i].name).exists()) {
                        File(newFilePath + "/" + files[i].name).mkdir()
                    }
                    fileMove(files[i].name)
                    File(oldFilePath + "/" + files[i].name).delete()
                    oldFilePath = oldFilePath.substring(0, oldFilePath.length - files[i].name.length - 2)
                    newFilePath = newFilePath.substring(0, newFilePath.length - files[i].name.length - 2)
                } else {
                    val oldFile = File(oldFilePath + "/" + files[i].name)
                    val newFile = File(newFilePath + "/" + files[i].name)
                    if(!oldFile.renameTo(newFile)) {
                        var fin: FileInputStream? = null
                        var fout: FileOutputStream? = null
                        try {
                            fin = FileInputStream(oldFile)
                            fout = FileOutputStream(newFile)
                            val data = ByteArray(1024)
                            var count = 0
                            while (count != -1) {
                                count = fin.read(data)
                                fout.write(data, 0, count)
                            }
                        } catch (e: IOException) {
                        } finally {
                            fin?.close()
                            fout?.close()
                            oldFile.delete()
                        }
                    }
                }
            }
        }
    }

    private fun mediaRV(name: String) {
        mediaRvPath = "$mediaRvPath$name"
        val files = File(mediaRvPath).listFiles()
        if(files != null) {
            for (i in files.indices) {
                if (File(mediaRvPath + files[i].name).isDirectory) {
                    mediaRV(files[files.size-i-1].name+"/")
                } else {
                    media.add(CheckClass("file://" + mediaRvPath + files[i].name))
                }
                mediaRVTemp += 1
                if(mediaRVTemp >= 5) {
                    break
                }
            }
        }
    }
}

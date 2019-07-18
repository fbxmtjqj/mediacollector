package com.youngwon.mediacollector

import android.annotation.SuppressLint
import android.content.*
import android.os.*
import android.util.Log
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_home.*
import java.io.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, RecycleViewClick {

    private var parentFolder: String? = null
    private var oldparentFolder: String? = null
    private var media = ArrayList<CheckClass>()
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

        parentFolder = PreferenceManager.getDefaultSharedPreferences(this@MainActivity).getString("DownloadFolder", "MediaDownloader")

        MediaView.setOnClickListener{
            startActivity(Intent(this@MainActivity,DownloadActivity::class.java))
            setStopService()
            finish()
        }

        HistoryView.setOnClickListener{
            startActivity(Intent(this@MainActivity,HistoryActivity::class.java))
            setStopService()
            finish()
        }

        val settings: SharedPreferences = getSharedPreferences("dico", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = settings.edit()
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
            if (!File(Environment.getExternalStorageDirectory().toString() + "/MediaDownloader").exists()) {
                File(Environment.getExternalStorageDirectory().toString() + "/MediaDownloader").mkdir()
            }
            editor.putBoolean("first", false)
            editor.putString("FolderName", "MediaDownloader")
            editor.apply()
        }
        if (intent.hasExtra("setting")) {
            oldparentFolder = settings.getString("FolderName", "MediaDownloader")
            if(oldparentFolder != parentFolder) {
                newfilepath = newfilepath + "/" +  PreferenceManager.getDefaultSharedPreferences(this@MainActivity).getString("DownloadFolder", "MediaDownloader")
                filepath = filepath + "/" + settings.getString("FolderName", "MediaDownloader")
                editor.putString("FolderName", parentFolder)
                editor.apply()
                FileMoveAsyncTask().execute("")
            }
        }

        val fileurl = arrayListOf<CheckClass>()
        try {
            val br = BufferedReader(FileReader(filesDir.toString() + "history.txt"))
            var str = br.readLine()
            while (str != null) {
                fileurl.add(0,CheckClass(str))
                str = br.readLine()
            }
            br.close()
        } catch (e: FileNotFoundException) {
        }
        val mAdapter1 = RecycleViewAdapter(2, fileurl,this@MainActivity,this@MainActivity)
        history_recycleview_text.adapter = mAdapter1
        history_recycleview_text.layoutManager = LinearLayoutManager(this@MainActivity)
        history_recycleview_text.setHasFixedSize(true)


        testssss = "$testssss/$parentFolder/"
        test("")
        val mAdapter2 = RecycleViewAdapter(1, media,this@MainActivity,this@MainActivity)
        recycleview.adapter = mAdapter2
        recycleview.layoutManager = GridLayoutManager(this@MainActivity,3)
        recycleview.setHasFixedSize(true)
    }

    override fun onBackPressed() {
        val settings: SharedPreferences = getSharedPreferences("dico", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = settings.edit()
        val alert = AlertDialog.Builder(this@MainActivity)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this@MainActivity,MainActivity::class.java))
            }
            R.id.nav_download -> {
                startActivity(Intent(this@MainActivity, DownloadActivity::class.java))
                setStopService()
                finish()
            }
            R.id.nav_file -> {
                startActivity(Intent(this@MainActivity, MediaActivity::class.java))
                setStopService()
                finish()
            }
            R.id.nav_history -> {
                startActivity(Intent(this@MainActivity, HistoryActivity::class.java))
                setStopService()
                finish()
            }
            R.id.nav_setting -> {
                startActivity(Intent(this@MainActivity,SettingsActivity::class.java))
                setStopService()
                finish()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun viewclick(value: String) {
    }

    private var isBind: Boolean = false
    private var mServiceMessenger: Messenger? = null
    private val mConnection = object : ServiceConnection {
        //서비스가 실행될 때 호출
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mServiceMessenger = Messenger(service)
            val msg = Message.obtain(null, DownloadService().MSG_REGISTER_CLIENT)
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
            DownloadService().SEND_TO_ACTIVITY -> {
                val url = msg.data.getString("url")
                Toast.makeText(this@MainActivity,"URL 복사됨", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@MainActivity,DownloadActivity::class.java).putExtra("url",url))
                setStopService()
                finish()
            }
        }
        false
    }))

    @SuppressLint("StaticFieldLeak")
    inner class FileMoveAsyncTask : AsyncTask<String, String, Boolean>() {

        @SuppressLint("InflateParams")
        private val dialogView: View = LayoutInflater.from(this@MainActivity).inflate(R.layout.progressbar, null)
        private val alert: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity).setView(dialogView).setCancelable(false)
        private val dialog: AlertDialog = alert.create()

        override fun onPreExecute() {
            super.onPreExecute()
            dialog.show()
        }

        override fun doInBackground(vararg url: String?): Boolean {
            fileMove(" ")
            return true
        }

        @SuppressLint("RestrictedApi")
        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            dialog.dismiss()
        }
    }

    private var filepath = Environment.getExternalStorageDirectory().toString()
    private var newfilepath = Environment.getExternalStorageDirectory().toString()
    fun fileMove(name: String){
        Log.e("테스트",name)
        if(name != " ") {
            filepath = "$filepath/$name"
            newfilepath = "$newfilepath/$name"
        }
        val files = File(filepath).listFiles()
        if(files != null) {
            for (i in files.indices) {
                if (File(files[i].name).isDirectory) {
                    if (!File(newfilepath + "/" + files[i].name).exists()) {
                        File(newfilepath + "/" + files[i].name).mkdir()
                    }
                    fileMove(files[i].name)
                    File(filepath + "/" + files[i].name).delete()
                    filepath = filepath.substring(0, filepath.length - files[i].name.length - 2)
                    newfilepath = newfilepath.substring(0, newfilepath.length - files[i].name.length - 2)
                } else {
                    val oldFile = File(filepath + "/" + files[i].name)
                    val newFile = File(newfilepath + "/" + files[i].name)
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
                            if (fin != null) {
                                fin.close()
                            }
                            if (fout != null) {
                                fout.close()
                            }
                            oldFile.delete()
                        }
                    }
                }
            }
        }
    }

    fun testt() : ArrayList<CheckClass>{
        test("")

        return media
    }

    private var testssss = Environment.getExternalStorageDirectory().toString()
    fun test(name: String) {
        testssss = "$testssss$name"
        Log.e("테스트",testssss)
        var temp = 0
        val files = File(testssss).listFiles()
        if(files != null) {
            for (i in files.indices) {
                if (File(testssss + files[i].name).isDirectory) {
                    test(files[i].name+"/")
                } else {
                    media.add(CheckClass("file://" + testssss + files[i].name))
                }
                temp += 1
                if(temp == 5) {
                    break
                }
            }
        }
    }
}

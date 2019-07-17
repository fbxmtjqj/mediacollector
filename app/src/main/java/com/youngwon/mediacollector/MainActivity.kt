package com.youngwon.mediacollector

import android.content.*
import android.os.*
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_home.*
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, RecycleViewClick {

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

        if (!File(Environment.getExternalStorageDirectory().toString() + "/MediaDownloader").exists()) {
            File(Environment.getExternalStorageDirectory().toString() + "/MediaDownloader").mkdir()
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
        val mAdapter = RecycleViewAdapter(2, fileurl,this@MainActivity,this@MainActivity)
        history_recycleview_text.adapter = mAdapter
        val lm = LinearLayoutManager(this@MainActivity)
        history_recycleview_text.layoutManager = lm
        history_recycleview_text.setHasFixedSize(true)
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
                startActivity(Intent(this@MainActivity,SettingActivity::class.java))
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
}

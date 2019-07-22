package com.youngwon.mediacollector

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_download.*
import java.io.BufferedWriter
import java.io.FileWriter
import java.lang.ref.WeakReference


class DownloadActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var imgUrlList: ArrayList<CheckClass>? = null
    private var checkAll = true
    private var menuChange = 1
    private var mAdapter: RecycleViewAdapter? = null
    private var title:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.download)
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

        if (intent.hasExtra("url")) {
            urlinputedit.setText(intent.getStringExtra("url"))
            DownloadAsync(this).execute(intent.getStringExtra("url"))
        }

        urlinputedit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                menuChange = 1
                invalidateOptionsMenu()
                (urldownload as View).visibility = VISIBLE
                (medeadownload as View).visibility = INVISIBLE
            }
        })

        urlinputedit.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                if(urlinputedit.text.toString() != "") {
                    DownloadAsync(this).execute(urlinputedit.text.toString())
                }
            }
            false
        }

        url_clear.setOnClickListener{
            menuChange = 1
            invalidateOptionsMenu()
            urlinputedit.text = null
        }

        urldownload.setOnClickListener {
            if(urlinputedit.text.toString() != "") {
                DownloadAsync(this).execute(urlinputedit.text.toString())
            }
        }

        medeadownload.setOnClickListener {
            val builder = AlertDialog.Builder(this@DownloadActivity)
            builder.setTitle("파일 다운로드")
            builder.setMessage("선택한 파일 다운로드 하시겠습니까?")
            builder.setPositiveButton("다운받기") { _, _ ->
                startActivity(Intent(this, Download2Activity::class.java)
                    .putExtra("title",title)
                    .putExtra("urlCheckList",imgUrlList))
            }
            builder.setNegativeButton("취소") { _, _ ->
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            finish()
            startActivity(Intent(this@DownloadActivity,MainActivity::class.java))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this@DownloadActivity, MainActivity::class.java))
                finish()
            }
            R.id.nav_download -> {
                startActivity(Intent(this@DownloadActivity, DownloadActivity::class.java))
            }
            R.id.nav_file -> {
                startActivity(Intent(this@DownloadActivity, MediaActivity::class.java))
                finish()
            }
            R.id.nav_history -> {
                startActivity(Intent(this@DownloadActivity, HistoryActivity::class.java))
                finish()
            }
            R.id.nav_setting -> {
                startActivity(Intent(this@DownloadActivity,SettingsActivity::class.java))
                finish()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        when (menuChange) {
            1 -> menuInflater.inflate(R.menu.download_menu1, menu)
            2 -> menuInflater.inflate(R.menu.download_menu2, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.checkAll -> {
                checkAll = if (checkAll) {
                    for (i in imgUrlList!!.indices) {
                        imgUrlList?.set(i, CheckClass(imgUrlList!![i].str, true))
                    }
                    false
                } else  {
                    for (i in imgUrlList!!.indices) {
                        imgUrlList?.set(i, CheckClass(imgUrlList!![i].str, false))
                    }
                    true
                }
                mAdapter!!.notifyDataSetChanged()
            }
        }
        return true
    }

    private class DownloadAsync internal constructor(context: DownloadActivity) : AsyncTask<String, String, ArrayList<CheckClass>?>() {

        private val activity: WeakReference<DownloadActivity> = WeakReference(context)
        private val activityContext: WeakReference<Context> = WeakReference(context.applicationContext)
        private val dialogView: WeakReference<View> = WeakReference(LayoutInflater.from(context).inflate(R.layout.progressbar, context.findViewById(R.id.download), false))
        private val alert: AlertDialog.Builder = AlertDialog.Builder(context).setView(dialogView.get()).setCancelable(false)
        private val dialog: AlertDialog = alert.create()

        override fun onPreExecute() {
            super.onPreExecute()
            dialog.show()
        }

        override fun doInBackground(vararg url: String?): ArrayList<CheckClass>? {
            val activity = activity.get()!!
            activity.menuChange = 2
            activity.invalidateOptionsMenu()
            activity.imgUrlList = MediaUrlCrawling(activityContext.get()!!).crawling(url[0])
            activity.title = MediaUrlCrawling(activityContext.get()!!).getTitle(url[0])
            if(activity.imgUrlList != null) {
                url[0]?.let { activity.saveToInnerStorage(it) }
            }
            dialog.dismiss()
            return activity.imgUrlList
        }

        override fun onPostExecute(result: ArrayList<CheckClass>?) {
            val activity = activity.get()!!
            super.onPostExecute(result)
            if(result == null) {
                Toast.makeText(activityContext.get(),"URL을 다시 입력해 주세요", Toast.LENGTH_LONG).show()
            }
            else{
                (activity.urldownload as View).visibility = INVISIBLE
                (activity.medeadownload as View).visibility = VISIBLE
                activity.mAdapter = RecycleViewAdapter(3, result, activityContext.get()!!,null)
                activity.recycler.adapter = activity.mAdapter
                activity.recycler.layoutManager = GridLayoutManager(activityContext.get(),3)
                activity.recycler.setHasFixedSize(true)
                Toast.makeText(activityContext.get(),"다운받을 이미지를 클릭 해주세요",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun saveToInnerStorage(url:String) {
        val bw = BufferedWriter(FileWriter(filesDir.toString() + "history.txt", true))
        bw.write(url+"\n")
        bw.close()
    }
}
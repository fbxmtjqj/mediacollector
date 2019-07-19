package com.youngwon.mediacollector

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


class DownloadActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, RecycleViewClick {

    private var imgUrlList: ArrayList<CheckClass>? = null
    private var checkAll = true
    private var menuChange = 1
    private var mAdapter: RecycleViewAdapter? = null

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
            DownloadAsync().execute(intent.getStringExtra("url"))
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
                    DownloadAsync().execute(urlinputedit.text.toString())
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
                DownloadAsync().execute(urlinputedit.text.toString())
            }
        }

        medeadownload.setOnClickListener {
            val builder = AlertDialog.Builder(this@DownloadActivity)
            builder.setTitle("파일 다운로드")
            builder.setMessage("선택한 파일 다운로드 하시겠습니까?")
            builder.setPositiveButton("다운받기") { _, _ ->
                startActivity(Intent(this, Download2Activity::class.java)
                    .putExtra("url",urlinputedit.text.toString())
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
            startActivity(Intent(this@DownloadActivity,MainActivity::class.java))
            finish()
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

    override fun viewClick(value: String) {
    }

    inner class DownloadAsync : AsyncTask<String, String, ArrayList<CheckClass>?>() {

        private val dialogView: View = LayoutInflater.from(this@DownloadActivity).inflate(R.layout.progressbar, null)
        private val alert: AlertDialog.Builder = AlertDialog.Builder(this@DownloadActivity).setView(dialogView).setCancelable(false)
        private val dialog: AlertDialog = alert.create()

        override fun onPreExecute() {
            super.onPreExecute()
            dialog.show()
        }

        override fun doInBackground(vararg url: String?): ArrayList<CheckClass>? {
            menuChange = 2
            invalidateOptionsMenu()
            imgUrlList = MediaUrlCrawling(this@DownloadActivity).crawling(url[0])
            if(imgUrlList != null) {
                url[0]?.let { saveToInnerStorage(it) }
            }
            dialog.dismiss()
            return imgUrlList
        }

        override fun onPostExecute(result: ArrayList<CheckClass>?) {
            super.onPostExecute(result)
            if(result == null) {
                Toast.makeText(this@DownloadActivity,"URL을 다시 입력해 주세요", Toast.LENGTH_LONG).show()
            }
            else{
                (urldownload as View).visibility = INVISIBLE
                (medeadownload as View).visibility = VISIBLE
                mAdapter = RecycleViewAdapter(3, result,this@DownloadActivity,this@DownloadActivity)
                recycler.adapter = mAdapter
                recycler.layoutManager = GridLayoutManager(this@DownloadActivity,3)
                recycler.setHasFixedSize(true)
                Toast.makeText(this@DownloadActivity,"다운받을 이미지를 클릭 해주세요",Toast.LENGTH_LONG).show()
            }
        }
    }

    fun saveToInnerStorage(url:String) {
        val bw = BufferedWriter(FileWriter(filesDir.toString() + "history.txt", true))
        bw.write(url+"\n")
        bw.close()
    }
}
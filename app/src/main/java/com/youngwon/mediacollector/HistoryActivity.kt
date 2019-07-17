package com.youngwon.mediacollector

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_history.*
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader


class HistoryActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history)
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

        val historydelete: FloatingActionButton = findViewById(R.id.historydelete)

        historydelete.setOnClickListener{
            try {
                val br = File(filesDir.toString() + "history.txt")
                if(br.exists()) {
                    val builder = AlertDialog.Builder(this@HistoryActivity)
                    builder.setTitle("히스토리 삭제")
                    builder.setMessage("히스토리 삭제 하시겠습니까? \n삭제하면 복구 못합니다.")
                    builder.setPositiveButton("삭제") { _, _ ->
                        br.delete()
                        Toast.makeText(this@HistoryActivity, "삭제되었습니다", Toast.LENGTH_LONG).show()
                        createRv()
                    }
                    builder.setNegativeButton("취소") { _, _ ->
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }
            } catch (e: FileNotFoundException) {
            }
        }
        createRv()
    }

    fun createRv() {
        val fileurl = arrayListOf<CheckClass>()
        try {
            val br = BufferedReader(FileReader(filesDir.toString() + "history.txt"))
            var str = br.readLine()
            // 파일로부터 한 라인 읽기.
            while (str != null) {
                fileurl.add(0,CheckClass(str))
                str = br.readLine()
            }
            br.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val mAdapter = RecycleViewAdapter(5,this@HistoryActivity, fileurl)
        history_recycleview.adapter = mAdapter
        val lm = LinearLayoutManager(this@HistoryActivity)
        history_recycleview.layoutManager = lm
        history_recycleview.setHasFixedSize(true)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            startActivity(Intent(this@HistoryActivity,MainActivity::class.java))
            finish()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this@HistoryActivity,MainActivity::class.java))
                finish()
            }
            R.id.nav_download -> {
                startActivity(Intent(this@HistoryActivity, DownloadActivity::class.java))
                finish()
            }
            R.id.nav_file -> {
                startActivity(Intent(this@HistoryActivity, MediaActivity::class.java))
                finish()
            }
            R.id.nav_history -> {
                startActivity(Intent(this@HistoryActivity, HistoryActivity::class.java))
            }
            R.id.nav_setting -> {
                startActivity(Intent(this@HistoryActivity,SettingActivity::class.java))
                finish()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

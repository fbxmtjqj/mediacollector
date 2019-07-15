package com.youngwon.mediacollector

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.content_home.*
import java.io.BufferedReader
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

        val fileurl = arrayListOf<String>()
        try {
            val br = BufferedReader(FileReader(filesDir.toString() + "history.txt"))
            var str = br.readLine()
            // 파일로부터 한 라인 읽기.
            while (str != null) {
                fileurl.add(str)
                str = br.readLine()
            }
            br.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val mAdapter = RecycleViewAdapter(4,this@HistoryActivity, fileurl)
        main_history_recycleview.adapter = mAdapter
        val lm = LinearLayoutManager(this@HistoryActivity)
        main_history_recycleview.layoutManager = lm
        main_history_recycleview.setHasFixedSize(true)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
                startActivity(Intent(this@HistoryActivity,MainActivity::class.java))
            }
            R.id.nav_history -> {
                startActivity(Intent(this@HistoryActivity,HistoryActivity::class.java))
            }
            R.id.nav_download -> {
                startActivity(Intent(this@HistoryActivity,DownloadActivity::class.java))
            }
            R.id.nav_setting -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}

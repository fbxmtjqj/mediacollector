package com.youngwon.mediacollector

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_history.*
import java.io.File


class MediaActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, RecycleViewClick {

    var filelist = arrayListOf<CheckClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.media)
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

        fileList(null)

        val mAdapter = RecycleViewAdapter(5, filelist, this@MediaActivity,this@MediaActivity)
        history_recycleview.adapter = mAdapter
        val lm = LinearLayoutManager(this@MediaActivity)
        history_recycleview.layoutManager = lm
        history_recycleview.setHasFixedSize(true)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            startActivity(Intent(this@MediaActivity,MainActivity::class.java))
            finish()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this@MediaActivity,MainActivity::class.java))
                finish()
            }
            R.id.nav_download -> {
                startActivity(Intent(this@MediaActivity, DownloadActivity::class.java))
                finish()
            }
            R.id.nav_file -> {
                startActivity(Intent(this@MediaActivity, MediaActivity::class.java))
            }
            R.id.nav_history -> {
                startActivity(Intent(this@MediaActivity, HistoryActivity::class.java))
                finish()
            }
            R.id.nav_setting -> {
                startActivity(Intent(this@MediaActivity,SettingsActivity::class.java))
                finish()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun viewclick(value: String) {
    }

    private fun fileList(path: String?) {
        val filepath = Environment.getExternalStorageDirectory().toString() + "/MediaDownloader" + path
        val files = File(filepath).listFiles()
        for(i in files!!.indices) {
            Log.e("파일 출력",files[i].name)
            if(File(filepath + "/" + files[i].name).isDirectory) {
                filelist.add(CheckClass(files[i].name, true))
            } else {
                filelist.add(CheckClass(files[i].name))
            }
        }
    }

    fun fileDelete(dir: String) {
        val path = Environment.getExternalStorageDirectory().toString() + "/MediaDownloader/" + dir
        if(File(path).exists()) {
            for(childFile in File(path).listFiles()!!) {
                if(childFile.isDirectory) {
                    fileDelete(childFile.name)
                } else {
                    childFile.delete()
                }
            }
            File(path).delete()
        }
    }

}

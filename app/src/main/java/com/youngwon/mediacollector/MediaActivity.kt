package com.youngwon.mediacollector

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_media.*
import java.io.File


class MediaActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, RecycleViewClick {

    private var fileList = arrayListOf<CheckClass>()
    private var mainFolder: String? = null
    private var filepath: String? = null

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

        mainFolder = getDefaultSharedPreferences(this).getString("DownloadFolder", "MediaDownloader")
        /*filepath = "$filepath/$mainFolder"*/

        if (intent.hasExtra("file")) {
            addFileList(intent.getStringExtra("file"))
        } else {
            addFileList(Environment.getExternalStorageDirectory().toString() + "/$mainFolder/")
        }

        media_recycleview.adapter = RecycleViewAdapter(5, fileList, this@MediaActivity,this@MediaActivity)
        media_recycleview.layoutManager = GridLayoutManager(this@MediaActivity,3)
        media_recycleview.setHasFixedSize(true)
    }

    override fun onBackPressed() {
        val nowFolderPath = filepath!!.substring(0, filepath!!.length - 1).split("/").last()
        val parentFolderPath = filepath!!.substring(0, filepath!!.length - nowFolderPath.length - 1)
        finish()
        if(filepath == (Environment.getExternalStorageDirectory().toString() + "/$mainFolder/")) {
            startActivity(Intent(this@MediaActivity, MainActivity::class.java))
        } else {
            startActivity(intent.putExtra("file", parentFolderPath))
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

    override fun viewClick(value: String) {
        fileList.clear()
        if(File(value.substring(7)).isDirectory) {
            finish()
            startActivity(intent.putExtra("file", value.substring(7) + "/"))
        }
    }

    private fun addFileList(path: String?) {
        filepath = "$path"
        val files = File(filepath).listFiles()
        for (i in files.indices) {
            if (File(filepath + files[i].name).isDirectory) {
                fileList.add(0, CheckClass("file://" + filepath + files[i].name, true))
            } else {
                fileList.add(0, CheckClass("file://" + filepath + files[i].name))
            }
        }
    }

    fun fileDelete(dir: String) {
        val path = Environment.getExternalStorageDirectory().toString() + "/$mainFolder/$dir"
        if(File(path).exists()) {
            val filepath = File(path).listFiles()
            if(filepath != null) {
                for (childFile in filepath) {
                    if (childFile.isDirectory) {
                        fileDelete(childFile.name)
                    } else {
                        childFile.delete()
                    }
                }
            }
            File(path).delete()
        }
    }
}
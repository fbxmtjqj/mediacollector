package com.youngwon.mediacollector

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.content_download.*
import org.jetbrains.anko.toast

class DownloadActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.download)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val url = textInputEditText.text.toString()
            downloadasync().execute(url)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            startActivity(Intent(this@DownloadActivity,MainActivity::class.java))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
                startActivity(Intent(this@DownloadActivity, MainActivity::class.java))
            }
            R.id.nav_history -> {
                // Handle the camera action
            }
            R.id.nav_download -> {
                startActivity(Intent(this@DownloadActivity, DownloadActivity::class.java))
            }
            R.id.nav_setting -> {
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    inner class downloadasync() : AsyncTask<String, String, Int>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = android.widget.ProgressBar.VISIBLE
        }

        override fun doInBackground(vararg url: String?): Int? {
            //프로그레스바 테스트용, 나중에 제거요망
            for(i in 1..5) {
                Thread.sleep(500)
            }
            return MediaDownload().MediaDownload(url)
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            if(result == 0) {
                progressBar.visibility = android.widget.ProgressBar.INVISIBLE
                toast("잘못된 url")
            }
            if(result == 1) {
                progressBar.visibility = android.widget.ProgressBar.INVISIBLE
                toast("제대로 된 url")
            }
        }
    }
}

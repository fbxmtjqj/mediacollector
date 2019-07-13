package com.youngwon.mediacollector

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.content_download.*

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

    inner class downloadasync() : AsyncTask<String, String, ArrayList<String>?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = android.widget.ProgressBar.VISIBLE
        }

        override fun doInBackground(vararg url: String?): ArrayList<String>? {
            return MediaDownload().MediaDownload(url[0])
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: ArrayList<String>?) {
            super.onPostExecute(result)
            progressBar.visibility = android.widget.ProgressBar.INVISIBLE
            if(result == null) {
                Toast.makeText(this@DownloadActivity,"잘못된 url", Toast.LENGTH_LONG).show()
            }
            else{
                for(test in result.indices) {
                }
            }
        }
    }
}

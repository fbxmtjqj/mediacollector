package com.youngwon.mediacollector

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
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
                startActivity(Intent(this@DownloadActivity,MainActivity::class.java))
            }
            R.id.nav_history -> {
                // Handle the camera action
            }
            R.id.nav_download -> {
                startActivity(Intent(this@DownloadActivity,DownloadActivity::class.java))
            }
            R.id.nav_setting -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    class downloadasync() : AsyncTask<String, String, Int>() {
       // val builder = AlertDialog.Builder(DownloadActivity())
        //val dialogview = LayoutInflater.inflate(R.layout.progress_dialog,null)
        //lateinit var progressDialog:ProgressDialog
        // private val alertDialogBuilder = AlertDialog.Builder(applicationContext)
        // private val dialogView = LayoutInflater.inflate(R.layout.progress_dialog,null)
       // var progressDialog = ProgressDialog(applicationContext)
        override fun onPreExecute() {
            //progressDialog.setMessage("ProgressDialog running...");
          //  progressDialog.setCancelable(true);
           // progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Horizontal);
          //  progressDialog.show()
           /// progressDialog.setMessage("분석중입니다.")
          //  progressDialog.show()
            super.onPreExecute()
            // progressBar2.visibility = android.widget.ProgressBar.VISIBLE
            // alertDialogBuilder.setView(dialogView)
            //alertDialogBuilder.show()
        }

        override fun doInBackground(vararg url: String?): Int? {
            return MediaDownload().MediaDownload(url)
        }

        override fun onProgressUpdate(vararg values: String?) {
            super.onProgressUpdate(*values)
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            if(result == 0) {
            //    progressDialog.dismiss()
                DownloadActivity().viewtoast()
            }
            if(result == 1) {
             //   progressDialog.dismiss()
                toastLong("제대로 된 url")
            }
        }
    }

    fun viewtoast() {
        toastLong("잘못된 url 입력")
    }
}

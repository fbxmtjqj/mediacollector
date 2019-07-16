package com.youngwon.mediacollector

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.progressbar2.view.*
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL


class Download2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    @SuppressLint("InflateParams")
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

        ImageDownload().execute(intent.getSerializableExtra(("urlCheckList")) as ArrayList<CheckClass>)

    }


    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            alert(this@Download2Activity,MainActivity::class.java)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
                alert(this@Download2Activity,MainActivity::class.java)
            }
            R.id.nav_history -> {
                alert(this@Download2Activity,MainActivity::class.java)
            }
            R.id.nav_download -> {
                alert(this@Download2Activity,MainActivity::class.java)
            }
            R.id.nav_setting -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun alert(context: Context, url: Class<*>) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("확인")
        builder.setMessage("다른페이지로 이동하시겠습니까?")
        builder.setPositiveButton("이동") { _, _ ->
            context.startActivity(Intent(context,url))
            finish()
        }
        builder.setNegativeButton("취소") { _, _ ->
        }
        val dialog:AlertDialog = builder.create()
        dialog.show()
    }
    @SuppressLint("StaticFieldLeak")
    inner class ImageDownload : AsyncTask<ArrayList<CheckClass>, Int, Boolean>() {

        @SuppressLint("InflateParams")
        private val dialogView: View = LayoutInflater.from(this@Download2Activity).inflate(R.layout.progressbar2, null)
        private val alert: AlertDialog.Builder = AlertDialog.Builder(this@Download2Activity).setView(dialogView).setCancelable(false)
        private val dialog: AlertDialog = alert.create()
        private lateinit var urllist:ArrayList<CheckClass>
        override fun onPreExecute() {
            super.onPreExecute()
            dialog.show()
        }

        @SuppressLint("SetTextI18n")
        override fun onProgressUpdate(vararg i: Int?) {
            val percent = (i[0]!!.plus(1)) * 100 / (urllist.size+1)
            dialogView.downloadprogress.progress = percent
            dialogView.downloadprogresstext.text = "$percent%"
        }

        @SuppressLint("RestrictedApi")
        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            dialog.dismiss()
        }

        override fun doInBackground(vararg list: ArrayList<CheckClass>?): Boolean? {
            var input: InputStream? = null
            var output: OutputStream? = null
            var connection: HttpURLConnection? = null
            urllist = list[0]!!
            val path = getExternalStorageDirectory().toString() + "/test/"
            var temp = ""
            for(i in urllist!!.indices) {
                publishProgress(i)
                if(urllist[i].selected) {
                    var url = urllist[i].url.split("/".toRegex()).last().split("\\?".toRegex())[0]
                    if(temp == url) {
                        try {
                            url = url + "$i." + url.split('.')[1]
                        } catch (e: Exception) {
                            url += ".jpg"
                        }
                    }else {
                        try {
                            url = url + "." + url.split('.')[1]
                        } catch (e: Exception) {
                            url += ".jpg"
                        }
                    }
                    temp = url
                    Log.e("테스트1",  url)
                    try {
                        val urls = URL(urllist[i].url)
                        connection = urls.openConnection() as HttpURLConnection
                        connection.connect()

                        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                            Log.e("테스트","Server returned HTTP "+connection.responseCode + " " + connection.responseMessage)
                        }

                        input = connection.inputStream
                        output = FileOutputStream(path+ url)
                        Log.e("테스트2",  url)
                        Log.e("테스트3", path+ url)
                        val data = ByteArray(4096)
                        var count = 0
                        while (count != -1) {
                            count = input!!.read(data)
                            output.write(data, 0, count)
                        }
                    } catch (e: Exception) {
                        Log.e("테스트4", e.toString())
                    } finally {
                        try {
                            output?.close()
                            input?.close()
                        } catch (ignored: IOException) {
                        }
                        connection?.disconnect()
                    }
                }
                if(i == urllist.size-1) {
                    dialog.dismiss()
                }
            }
            return true
        }
    }
}

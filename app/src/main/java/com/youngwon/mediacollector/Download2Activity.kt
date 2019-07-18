package com.youngwon.mediacollector

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_download2.*
import kotlinx.android.synthetic.main.progressbar2.view.*
import org.jsoup.Jsoup
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


class Download2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, RecycleViewClick {

    @SuppressLint("InflateParams")
    val filenamelist =  arrayListOf<CheckClass>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.download2)
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

        ImageDownload().execute(intent.getSerializableExtra("urlCheckList") as ArrayList<CheckClass>)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            startActivity(Intent(this@Download2Activity,DownloadActivity::class.java))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                startActivity(Intent(this@Download2Activity,MainActivity::class.java))
            }
            R.id.nav_download -> {
                startActivity(Intent(this@Download2Activity,DownloadActivity::class.java))
            }
            R.id.nav_file -> {
                startActivity(Intent(this@Download2Activity,MediaActivity::class.java))
            }
            R.id.nav_history -> {
                startActivity(Intent(this@Download2Activity,HistoryActivity::class.java))
            }
            R.id.nav_setting -> {
                startActivity(Intent(this@Download2Activity,SettingsActivity::class.java))
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun viewclick(value: String) {
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
            val mAdapter = RecycleViewAdapter(4, filenamelist,this@Download2Activity,this@Download2Activity)
            download2_recycleview.adapter = mAdapter
            download2_recycleview.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            download2_recycleview.setHasFixedSize(true)
        }

        override fun doInBackground(vararg list: ArrayList<CheckClass>?): Boolean? {
            var temp = ""
            var input: InputStream? = null
            var output: OutputStream? = null
            var connection: HttpURLConnection? = null
            val title = Jsoup.connect(intent.getStringExtra("url")).get().title().split(" ")[0]
            val path = getExternalStorageDirectory().toString() + "/MediaDownloader/$title/"

            val folder = File(path)
            if (!folder.exists()) {
                folder.mkdirs()
                if (!folder.mkdir()) {
                    folder.delete()
                    folder.absoluteFile.delete()
                    folder.mkdir()
                }
            } else if (!folder.isDirectory) {
                folder.delete()
                folder.mkdir()
            }

            urllist = list[0]!!
            for(i in urllist.indices) {
                if(urllist[i].selected) {
                    val url = if(urllist[i].url.contains("dcinside")) {
                        urllist[i].url
                    } else {
                        urllist[i].url.split("/").last()
                    }
                    var filename:String
                    if(urllist[i].url.contains("dcinside")) {
                        filename = "img$i.jpg"
                    }
                    else if(temp == url) {
                        filename = url.split(".")[0]
                        try {
                            if(url.split(".").size == 1) {
                                filename += ".jpg"
                            } else {
                                filename = filename + "$i." + url.split(".").last()
                            }
                        } catch (e: ArrayIndexOutOfBoundsException) {
                        }
                    }else {
                        filename = url.split(".")[0]
                        try {
                            if(url.split(".").size == 1) {
                                filename += ".jpg"
                            } else {
                                filename = filename + "." + url.split(".")[1]
                            }
                        } catch (e: ArrayIndexOutOfBoundsException) {
                        }
                    }
                    temp = urllist[i].url.split("/").last()
                    try {
                        val urls = URL(urllist[i].url)
                        connection = urls.openConnection() as HttpURLConnection
                        connection.connect()

                        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                            Log.e("http에러","Server returned HTTP "+connection.responseCode + " " + connection.responseMessage)
                        }
                        filenamelist.add(CheckClass("file://" + (path + filename)))
                        input = connection.inputStream
                        output = FileOutputStream(path + filename)
                        val data = ByteArray(4096)
                        var count = 0
                        while (count != -1) {
                            count = input!!.read(data)
                            output.write(data, 0, count)
                        }
                    } catch (e: Exception) {
                        Log.e("에러", e.toString())
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
                publishProgress(i)
            }
            return true
        }
    }
}

package com.youngwon.mediacollector

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
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_download2.*
import kotlinx.android.synthetic.main.progressbar2.view.*
import org.jsoup.Jsoup
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random


class Download2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val fileList =  arrayListOf<CheckClass>()
    private var mainFolder: String? = null

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

        mainFolder = PreferenceManager.getDefaultSharedPreferences(this@Download2Activity).getString("DownloadFolder", "MediaDownloader")

        ImageDownload().execute(intent.getSerializableExtra("urlCheckList") as ArrayList<CheckClass>)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            finish()
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

    inner class ImageDownload : AsyncTask<ArrayList<CheckClass>, Int, Boolean>() {

        private val dialogView: View = LayoutInflater.from(this@Download2Activity).inflate(R.layout.progressbar2, findViewById(R.id.download2), false)
        private val alert: AlertDialog.Builder = AlertDialog.Builder(this@Download2Activity).setView(dialogView).setCancelable(false)
        private val dialog: AlertDialog = alert.create()
        private lateinit var downloadList:ArrayList<CheckClass>
        override fun onPreExecute() {
            super.onPreExecute()
            dialog.show()
        }

        override fun onProgressUpdate(vararg i: Int?) {
            val percent = (i[0]!!.plus(1)) * 100 / (downloadList.size+1)
            dialogView.downloadprogress.progress = percent
            dialogView.downloadprogresstext.text = ("$percent%").toString()
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            download2_recycleview.adapter = RecycleViewAdapter(4, fileList,this@Download2Activity,null)
            download2_recycleview.layoutManager = GridLayoutManager(this@Download2Activity,3)
            download2_recycleview.setHasFixedSize(true)
        }

        override fun doInBackground(vararg list: ArrayList<CheckClass>?): Boolean? {
            var temp = ""
            var input: InputStream? = null
            var output: OutputStream? = null
            var connection: HttpURLConnection? = null
            val folderName = Jsoup.connect(intent.getStringExtra("url")).get().title().split(" ")[0]
            var folderPath = getExternalStorageDirectory().toString() + "/$mainFolder/$folderName"
            downloadList = list[0]!!

            val folder = File("$folderPath/")
            if (!folder.exists()) {
                folder.mkdirs()
            } else if (!folder.isDirectory) {
                folder.delete()
                folder.mkdir()
            } else {
                folderPath = folderPath + Random.nextInt(1000) + "/"
                File(folderPath).mkdir()
            }

            for(i in downloadList.indices) {
                if(downloadList[i].check) {
                    val url = if(downloadList[i].str.contains("gall.dcinside.com")) {
                        downloadList[i].str
                    } else {
                        downloadList[i].str.split("/").last()
                    }
                    var filename:String
                    when {
                        downloadList[i].str.contains("gall.dcinside.com") -> filename = "img$i.jpg"
                        temp == url -> {
                            filename = url.split(".")[0]
                            try {
                                if(url.split(".").size == 1) {
                                    filename += ".jpg"
                                } else {
                                    filename = filename + "$i." + url.split(".").last()
                                }
                            } catch (e: ArrayIndexOutOfBoundsException) {
                            }
                        }
                        else -> {
                            filename = url.split(".")[0]
                            try {
                                if(url.split(".").size == 1) {
                                    filename += ".jpg"
                                } else {
                                    filename = filename + "." + url.split(".").last()
                                }
                            } catch (e: ArrayIndexOutOfBoundsException) {
                            }
                        }
                    }
                    if(filename.split("?").size == 2) {
                        filename = filename.substring(0, filename.length - filename.split("?").last().length - 1)
                    }
                    temp = downloadList[i].str.split("/").last()
                    try {
                        val urls = URL(downloadList[i].str)
                        connection = urls.openConnection() as HttpURLConnection
                        connection.connect()

                        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                            Log.e("http에러","Server returned HTTP "+connection.responseCode + " " + connection.responseMessage)
                        }
                        fileList.add(CheckClass("file://$folderPath/$filename"))
                        input = connection.inputStream
                        output = FileOutputStream("$folderPath/$filename")
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
                if(i == downloadList.size-1) {
                    dialog.dismiss()
                }
                publishProgress(i)
            }
            publishProgress(downloadList.size)
            dialog.dismiss()
            return true
        }
    }
}

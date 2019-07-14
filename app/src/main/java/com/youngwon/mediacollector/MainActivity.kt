package com.youngwon.mediacollector

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_home.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    val filename = "log.txt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
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

        //media블록의 더보기 클릭시 media로 이동, media액티브추가시 수정요망
        MediaView.setOnClickListener{
            startActivity(Intent(this@MainActivity,DownloadActivity::class.java))
        }

        val settings: SharedPreferences = getSharedPreferences("dico", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = settings.edit()
        if(settings.getBoolean("switch", false)) {
            ActiveText.text = "활성화"
            ActiveSwitch.setChecked(true)
        } else {
            ActiveText.text = "비활성화"
            ActiveSwitch.setChecked(false)
        }

        //스위치버튼 클릭시 활성화로 텍스트변경
        ActiveSwitch.setOnCheckedChangeListener{ buttonView,isChecked ->
            if (isChecked){ //만약 스위치를 On시킨다면
                ActiveText.text = "활성화"
                Toast.makeText(this@MainActivity, "mediacollector가 작동합니다.", Toast.LENGTH_SHORT).show()
                //비활성화버튼을 활성화로
                NotificationHelper(this@MainActivity).createNotification("자동다운로드","")
                editor.putBoolean("switch", isChecked)
                editor.commit()
            }
            else { //만약 스위치를 Off시킨다면
                ActiveText.text = "비활성화"
                Toast.makeText(this@MainActivity, "mediacollector가 비활성화됩니다.", Toast.LENGTH_SHORT).show()
                //활성화를 비활성화로
                NotificationHelper(this@MainActivity).deleteNotification()
                editor.putBoolean("switch", isChecked)
                editor.commit()
            }
        }
    }

    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this@MainActivity)
        alert.setMessage("정말로 종료하시겠습니까?");
        alert.setPositiveButton("취소") {dialog, which ->
        }
        alert.setNegativeButton("종료") {dialog, which ->
            super.onBackPressed()
        }
        val dialog:AlertDialog = alert.create()
        dialog.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
                startActivity(Intent(this@MainActivity,MainActivity::class.java))
            }
            R.id.nav_history -> {
                // Handle the camera action
            }
            R.id.nav_download -> {
                startActivity(Intent(this@MainActivity,DownloadActivity::class.java))
            }
            R.id.nav_setting -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun loadFromInnerStorage(): String {
        val fileInputStream = openFileInput(filename)
        return fileInputStream.reader().readText()
    }

}
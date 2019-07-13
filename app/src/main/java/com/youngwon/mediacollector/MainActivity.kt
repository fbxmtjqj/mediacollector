package com.youngwon.mediacollector

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.content_home.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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

        //스위치버튼 클릭시 활성화로 텍스트변경
        ActiveSwitch.setOnCheckedChangeListener{ buttonView,isChecked ->
            if (isChecked){ //만약 스위치를 On시킨다면
                ActiveText.text = "활성화"
                Toast.makeText(this@MainActivity, "mediacollector가 작동합니다.", Toast.LENGTH_SHORT).show()
                //비활성화버튼을 활성화로
            }
            else { //만약 스위치를 Off시킨다면
                ActiveText.text = "비활성화"
                Toast.makeText(this@MainActivity, "mediacollector가 비활성화됩니다.", Toast.LENGTH_SHORT).show()
                //활성화를 비활성화로
            }
        }
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
}
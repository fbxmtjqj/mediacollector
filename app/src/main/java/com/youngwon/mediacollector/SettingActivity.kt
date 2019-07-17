package com.youngwon.mediacollector

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.setting.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.setting)
        setSupportActionBar(toolbar)

    }

    override fun onBackPressed() {
        startActivity(Intent(this@SettingActivity,MainActivity::class.java))
        finish()
    }
}

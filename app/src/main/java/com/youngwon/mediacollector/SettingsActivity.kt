package com.youngwon.mediacollector

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        /*supportActionBar?.setDisplayHomeAsUpEnabled(true)*/
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener { _: SharedPreferences, _: String ->
            fun onSharedPreferenceChanged( sharedPreferences :SharedPreferences,  key:String) {
                val value = sharedPreferences.getString (key, "");
            }
        };

    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            Log.e("테스트", "changed> $rootKey");
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@SettingsActivity, MainActivity::class.java))
    }
}
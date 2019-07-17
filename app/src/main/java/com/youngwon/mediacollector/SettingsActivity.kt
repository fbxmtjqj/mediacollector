package com.youngwon.mediacollector

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        /*supportActionBar?.setDisplayHomeAsUpEnabled(true)*/
/*        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener { _: SharedPreferences, _: String ->
            fun onSharedPreferenceChanged( sharedPreferences :SharedPreferences,  key:String) {
                val value = sharedPreferences.getString (key, "");
            }
        };*/
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            /*setOnPreferenceChange(findPreference("MediaDownloader")!!)*/
        }


     /*   private val onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                InitPreference(preference, newValue)
                true
        }

        fun InitPreference( preference: Preference,  newValue :Any){
            val stringValue = newValue.toString()
            Log.e("TEST", stringValue)
        }

        private fun setOnPreferenceChange(mPreference: Preference) {
            mPreference.onPreferenceChangeListener = onPreferenceChangeListener
            onPreferenceChangeListener.onPreferenceChange(
                mPreference,
                PreferenceManager.getDefaultSharedPreferences(mPreference.context).getBoolean(mPreference.key,false)
            )
        }*/
    }

    override fun onBackPressed() {
        startActivity(Intent(this@SettingsActivity, MainActivity::class.java))
    }
}
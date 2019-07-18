package com.youngwon.mediacollector

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import java.io.File


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        /*supportActionBar?.setDisplayHomeAsUpEnabled(true)*/
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            setOnPreferenceChange(findPreference("DownloadFolder"))
            setOnPreferenceChange(findPreference("DownloadMethod"))
        }

        private fun setOnPreferenceChange(mPreference: Preference?) {
            mPreference!!.onPreferenceChangeListener = onPreferenceChangeListener
            onPreferenceChangeListener.onPreferenceChange(mPreference, PreferenceManager.getDefaultSharedPreferences(mPreference.context).getString(mPreference.key, ""))
        }

        private val onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference, newValue ->
                val settings: SharedPreferences = preference.sharedPreferences
                val editor: SharedPreferences.Editor = settings.edit()
                if (preference is EditTextPreference) {
                    val folder = if(newValue.toString().isEmpty()) {
                        preference.setSummary("MediaDownloader")
                        File(Environment.getExternalStorageDirectory().toString() + "/MediaDownloader/")
                    } else {
                        preference.setSummary(newValue.toString())
                        File(Environment.getExternalStorageDirectory().toString() + "/" + newValue.toString())
                    }
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
                } else if (preference is ListPreference) {
                    val index = preference.findIndexOfValue(newValue.toString())

                    preference.setSummary(
                            if (index >= 0)
                                preference.entries[index]
                            else
                                null
                        )
                }
                true
            }
    }

    override fun onBackPressed() {
        startActivity(Intent(this@SettingsActivity, MainActivity::class.java))
    }
}

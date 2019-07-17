package com.youngwon.mediacollector

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class MediaDownload(context: Context) {

    private val settings: SharedPreferences = context.getSharedPreferences("dico", AppCompatActivity.MODE_PRIVATE)
    fun mediadownload(url: String?): ArrayList<CheckClass>? {
        var url1: Document? = null
        var i = 0
        while(i < 5) {
            i += 1
            try {
                url1 = Jsoup.connect(url).get()
            } catch (e: Exception) {
            }
        }
        if(url == null) {
            return null
        }
        val imgElements = if(settings.getInt("downloadmethod", 1) == 2) {
            url1!!.select("img[src~=(?i)\\.(gif|png|jpe?g)]")
        } else {
            url1!!.select("img")
        }
        val imgSrc = arrayListOf<CheckClass>()
        if (imgElements != null) {
            for(i in imgElements) {
                if(settings.getInt("downloadmethod", 1) == 3) {
                    try {
                        if (Integer.parseInt(i.attr("width")) > (settings.getInt("downloadsize", 0)) &&
                            Integer.parseInt(i.attr("height")) > (settings.getInt("downloadsize", 0))) {
                            imgSrc.add(CheckClass(i.attr("src")))
                        }
                    } catch (e: NumberFormatException) {
                        Log.e("테스트e", e.toString())
                    }
                } else {
                    imgSrc.add(CheckClass(i.attr("src")))
                }
            }
        }
        return imgSrc
    }
}
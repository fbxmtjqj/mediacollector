package com.youngwon.mediacollector

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class MediaUrlCrawling(context: Context) {

    private val settings = PreferenceManager.getDefaultSharedPreferences(context)

    fun crawling(url: String?): ArrayList<CheckClass>? {
        var htmlSource: Document? = null
        var count = 0
        while(count < 5) {
            count += 1
            try {
                htmlSource = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
                    .get()
            } catch (e: Exception) {
            }
        }
        if(url == null || htmlSource == null) {
            return null
        }
        val imgElements = if(settings.getString("DownloadMethod", "1")!!.toInt() == 2) {
            htmlSource.select("img[src~=(?i)\\.(gif|png|jpe?g)]")
        } else {
            htmlSource.select("img")
        }
        val imgSrcUrl = arrayListOf<CheckClass>()
        if (imgElements != null) {
            for(i in imgElements) {
                if(settings.getString("DownloadMethod", "1")!!.toInt() == 3) {
                    try {
                        if (Integer.parseInt(i.attr("width")) > (settings.getString("DownloadSize", "100")!!.toInt()) &&
                            Integer.parseInt(i.attr("height")) > (settings.getString("DownloadSize", "100")!!.toInt())) {
                            imgSrcUrl.add(CheckClass(i.attr("src")))
                        }
                    } catch (e: NumberFormatException) {
                        Log.e("NumberFormatException", e.toString())
                    }
                } else {
                    imgSrcUrl.add(CheckClass(i.attr("src")))
                }
            }
        }
        return imgSrcUrl
    }

    fun getTitle(url: String?): String {
        var count = 0
        var title:String? = null
        while(count < 5) {
            count += 1
            try {
                title = Jsoup.connect(url).get().title().trim()
                    .replace("?","").replace(":","").replace("<","").replace(">","")
                    .replace("/","").replace("\\","").replace("|","")
            } catch (e: Exception) {
            }
        }
        return title.toString()
    }
}
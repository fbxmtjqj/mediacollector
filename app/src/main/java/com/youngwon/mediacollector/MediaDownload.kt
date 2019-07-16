package com.youngwon.mediacollector

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class MediaDownload {
    fun mediadownload(url: String?): ArrayList<CheckClass>? {
        val url1:Document
        try {
            url1 = Jsoup.connect(url).get()
        } catch (e:Exception) {
            return  null
        }
        val imgelements: Elements? =  url1.select("img")
        val imgsrc = arrayListOf<CheckClass>()
        if (imgelements != null) {
            for(i in imgelements) {
                imgsrc.add(CheckClass(i.attr("src")))
            }
        }
        return imgsrc
    }
}
package com.youngwon.mediacollector

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class MediaDownload {

    fun MediaDownload(url: String?): ArrayList<String>? {
        val url1:Document
        try {
            url1 = Jsoup.connect(url).get()
            url1.absUrl("src")
        } catch (e:Exception) {
            return  null
        }
        val imgelements: Elements? =  url1.select("img")
        val imgsrc = arrayListOf<String>()
        if (imgelements != null) {
            for(i in imgelements) {
                imgsrc.add(i.attr("src"))
            }
        }
        return imgsrc
    }
}
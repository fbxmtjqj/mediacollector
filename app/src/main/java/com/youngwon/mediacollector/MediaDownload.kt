package com.youngwon.mediacollector

import org.jsoup.Jsoup
import java.lang.Exception

class MediaDownload() {

    fun MediaDownload(url: Array<out String?>): Int {
        try {
            val url = Jsoup.connect(url[0]).get()
        } catch (e:Exception) {
            return  0
        }
        return 1
    }
}
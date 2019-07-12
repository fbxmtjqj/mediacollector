package com.youngwon.mediacollector

import android.widget.Toast

fun toastLong(message: String) {
    Toast.makeText(MainApplication.applicationContext(),message,Toast.LENGTH_LONG).show()
}
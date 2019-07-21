package com.youngwon.mediacollector

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class StartActivity : AppCompatActivity() {

    private val requestWriteExternalStorage = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                val builder = AlertDialog.Builder(this@StartActivity)
                builder.setTitle("저장소 접근권한 거부")
                builder.setMessage("사진을 저장할려면 저장소에 접근 권한이 있어야합니다.")
                builder.setPositiveButton("권한 요청") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this@StartActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestWriteExternalStorage
                    )
                }
                builder.setNegativeButton("취소") { _, _ ->
                    finish()
                }
                val dialog:AlertDialog = builder.create()
                dialog.show()
            }
            else {
                ActivityCompat.requestPermissions(
                    this@StartActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestWriteExternalStorage
                )
            }
        }
        else {
            startActivity(Intent(this@StartActivity,MainActivity::class.java))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            requestWriteExternalStorage -> {
                if((grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED)) {
                    startActivity(Intent(this@StartActivity, MainActivity::class.java))
                }
                else {
                    val builder = AlertDialog.Builder(this@StartActivity)
                    builder.setTitle("저장소 접근권한 거부")
                    builder.setMessage("사진을 저장할려면 저장소에 접근 권한이 있어야합니다.")
                    builder.setPositiveButton("확인") { _, _ ->
                        finish()
                    }
                    val dialog:AlertDialog = builder.create()
                    dialog.show()
                }
            }
        }
    }
}

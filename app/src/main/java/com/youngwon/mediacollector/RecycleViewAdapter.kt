package com.youngwon.mediacollector

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.download_recycleview.view.*
import kotlinx.android.synthetic.main.history_recycleview.view.*

class RecycleViewAdapter(private val index:Int, val context: Context, private val urlList: ArrayList<String>):
    RecyclerView.Adapter<RecycleViewAdapter.Holder>() {

    private lateinit var view:View
    private val te = arrayListOf<CheckClass?>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
         view = when(index) {
            2,4 -> LayoutInflater.from(context).inflate(R.layout.history_recycleview, parent, false)
            3 -> LayoutInflater.from(context).inflate(R.layout.download_recycleview, parent, false)
            else -> {
                LayoutInflater.from(context).inflate(R.layout.download_recycleview, parent, false)
            }
        }
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        when(index) {
            2,4 -> {
                urlList.reverse()
            }
            3 -> {
               val test = CheckClass(urlList[position],false)
                te.add(test)
            }
        }
        holder.bind(urlList[position],index, position)
    }

    override fun getItemCount(): Int {
        return urlList.size
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        fun bind(str: String, index: Int, toString: Int) {
            when(index) {
                2 -> {
                    itemView.main_history_recycleview.text = str
                }
                3 -> {
                    Glide.with(itemView.context).load(str)
                        .into(itemView.img)
                    itemView.mediacheck.setOnCheckedChangeListener(null)
                    itemView.mediacheck.isChecked = te[toString]!!.selected
                    itemView.mediacheck.setOnCheckedChangeListener { _, isChecked ->
                        te[toString] = CheckClass(urlList[toString], isChecked)
                    }
                    itemView.download_recycleview_text.text = str.split("/".toRegex()).last()
                    itemView.setOnClickListener {
                        Toast.makeText(itemView.context, "'$str'를 선택했습니다", Toast.LENGTH_LONG).show()
                    }
                }
                4 -> {
                    itemView.main_history_recycleview.text = str
                    itemView.setOnClickListener {
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("다운로드")
                        builder.setMessage("다시 다운로드 하시겠습니까?")
                        builder.setPositiveButton("다운받기") { _, _ ->
                            context.startActivity(Intent(context,DownloadActivity::class.java).putExtra("url",str))
                        }
                        builder.setNegativeButton("취소") { _, _ ->
                        }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                }
            }
        }
    }
}
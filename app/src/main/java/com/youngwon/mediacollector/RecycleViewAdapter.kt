package com.youngwon.mediacollector

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.download_recycleview.view.*
import kotlinx.android.synthetic.main.download_recycleview.view.img
import kotlinx.android.synthetic.main.history_recycleview.view.*
import kotlinx.android.synthetic.main.media_recycleview.view.*
import java.io.File

class RecycleViewAdapter(private val index:Int, private val urlList: ArrayList<CheckClass>, val context: Context, private val listener: RecycleViewClick):
    RecyclerView.Adapter<RecycleViewAdapter.Holder>() {

    private lateinit var view:View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
         view = when(index) {
             1,5 -> LayoutInflater.from(context).inflate(R.layout.media_recycleview,parent,false)
             2,6 -> LayoutInflater.from(context).inflate(R.layout.history_recycleview, parent, false)
             3 -> LayoutInflater.from(context).inflate(R.layout.download_recycleview, parent, false)
             4 -> LayoutInflater.from(context).inflate(R.layout.download2_recycleview, parent, false)
             else -> {
                 LayoutInflater.from(context).inflate(R.layout.download_recycleview, parent, false)
            }
        }
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(urlList[position].str,index, position)
    }

    override fun getItemCount(): Int {
        return urlList.size
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        fun bind(str: String, index: Int, position: Int) {
            when(index) {
                1 -> {
                    itemView.img.setImageURI(Uri.parse(str))
                    itemView.media_text.text = null
                }
                2 -> {
                    itemView.history_recycleview_text.text = str
                }
                3 -> {
                    Glide.with(itemView.context).load(str)
                        .into(itemView.img)
                    itemView.mediacheck.setOnCheckedChangeListener(null)
                    itemView.mediacheck.visibility = if(urlList[position].check) {
                        View.VISIBLE
                    } else {
                        View.INVISIBLE
                    }
                    itemView.mediacheck.setOnCheckedChangeListener { _, isChecked ->
                        urlList[position] = CheckClass(urlList[position].str, isChecked)
                    }
                    itemView.setOnClickListener {
                        urlList[position] = CheckClass(urlList[position].str, !urlList[position].check)
                        itemView.mediacheck.visibility = if(urlList[position].check) {
                            View.VISIBLE
                        } else {
                            View.INVISIBLE
                        }
                    }
                }
                4 -> {
                    itemView.img.setImageURI(Uri.parse(str))
                }
                5-> {
                    if(urlList[position].check) {
                        itemView.img.setImageResource(R.drawable.ic_folder)
                        itemView.media_text.text = urlList[position].str.split("/").last()
                    }else {
                        itemView.img.setImageURI(Uri.parse(str))
                        itemView.media_text.text = null
                    }
                    itemView.setOnClickListener {
                        if(File(urlList[position].str.substring(7)).isDirectory) {
                            listener.viewClick(urlList[position].str)
                        }
                    }
                }
                6 -> {
                    itemView.history_recycleview_text.text = str
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
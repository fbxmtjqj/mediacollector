package com.youngwon.mediacollector

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.download_recycleview.view.*
import kotlinx.android.synthetic.main.history_recycleview.view.*


class RecycleViewAdapter(private val index:Int, val context: Context, private val urlList: ArrayList<String>):
    RecyclerView.Adapter<RecycleViewAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view:View = when(index) {
            2 -> LayoutInflater.from(context).inflate(R.layout.history_recycleview, parent, false)
            3 -> LayoutInflater.from(context).inflate(R.layout.download_recycleview, parent, false)
            else -> {
                LayoutInflater.from(context).inflate(R.layout.download_recycleview, parent, false)
            }
        }
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(urlList[position],index)
    }

    override fun getItemCount(): Int {
        return urlList.size
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        fun bind(str: String, index: Int) {
            when(index) {
                2 -> {
                    itemView.main_history_recycleview.text = str
                }
                3 -> {
                    Glide.with(itemView.context).load(str)
                        .into(itemView.img)
                    itemView.download_recycleview_text.text = str.split("/".toRegex()).last()
                    itemView.setOnClickListener {
                        Toast.makeText(itemView.context, "'$str'를 선택했습니다", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
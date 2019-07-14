package com.youngwon.mediacollector

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MainHistoryRvAdapter(val context: Context, private val urlList: ArrayList<String>):
    RecyclerView.Adapter<MainHistoryRvAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.main_history_recycleview, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(urlList[position])
    }

    override fun getItemCount(): Int {
        return urlList.size
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        private val text = itemView?.findViewById<TextView>(R.id.main_history_text)
        fun bind (str:String) {
            text?.text = str
        }
    }
}
package com.youngwon.mediacollector

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class DownloadRvAdapter(val context: Context, val linklist: ArrayList<String>):
    RecyclerView.Adapter<DownloadRvAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.download_recycleview, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder?.bind(linklist[position],context)
    }

    override fun getItemCount(): Int {
        return linklist.size
    }


    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val text = itemView?.findViewById<TextView>(R.id.textView3)
        val img = itemView?.findViewById<ImageView>(R.id.img)
        fun bind (str:String,context:Context) {
            Glide.with(itemView.context).load(str)
                .into(img)
            text?.text = str
        }
    }
}
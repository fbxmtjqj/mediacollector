package com.youngwon.mediacollector

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class DownloadRvAdapter(val context: Context, private val urlList: ArrayList<String>):
    RecyclerView.Adapter<DownloadRvAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.download_recycleview, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(urlList[position])
    }

    override fun getItemCount(): Int {
        return urlList.size
    }


    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        private val text = itemView?.findViewById<TextView>(R.id.textView3)
        private val img = itemView?.findViewById<ImageView>(R.id.img)
        fun bind (str:String) {
            Glide.with(itemView.context).load(str)
                .into(img)
            val separate1 = str.split("/".toRegex())
            text?.text = separate1.last()
            itemView.setOnClickListener {
                Toast.makeText(itemView.context, "'$str'를 선택했습니다", Toast.LENGTH_LONG).show()
            }
        }
    }
}
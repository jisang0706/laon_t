package com.example.laont.fragment.board

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.laont.R
import com.example.laont.dto.AreaDto

class AreaListAdapter (val items: MutableList<AreaDto>) : BaseAdapter() {
    override fun getCount(): Int = items.size

    override fun getItem(position: Int): AreaDto = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val convertView = LayoutInflater.from(parent!!.context).inflate(R.layout.item_area, parent, false)

        val item = items[position]
        convertView.findViewById<TextView>(R.id.writer_text).text = item.writer_nickname
        convertView.findViewById<TextView>(R.id.created_text).text = item.created_at
        convertView.findViewById<TextView>(R.id.content_text).text = item.content
        if (item.like >= 0)     convertView.findViewById<TextView>(R.id.like_text).text = "좋아요 " + item.like.toString() + "개"
        if (item.comment >= 0)  convertView.findViewById<TextView>(R.id.comment_text).text = "댓글 " + item.comment.toString() + "개"
        return convertView!!
    }

    fun getItemPkId(position: Int): Int {
        return items[position].id
    }
}
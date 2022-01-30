package com.example.laont.fragment.board

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.laont.R
import com.example.laont.dto.NotiDetailDto

class NotiListAdapter (val items: MutableList<NotiDetailDto>) : BaseAdapter() {
    override fun getCount(): Int = items.size

    override fun getItem(position: Int): NotiDetailDto = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        val convertView = LayoutInflater.from(parent!!.context).inflate(R.layout.item_noti, parent, false)

        val item = items[position]
        convertView.findViewById<TextView>(R.id.noti_title).text = item.title
        convertView.findViewById<TextView>(R.id.noti_created).text = item.created_at
        convertView.findViewById<TextView>(R.id.noti_content).text = item.content
        return convertView!!
    }

    fun addContent(convertView: View, content: String) {
        val content_textview = convertView.findViewById<TextView>(R.id.noti_content)
        content_textview.text = content
        content_textview.visibility = View.VISIBLE
    }

    fun getItemPkId(position: Int): Int {
        return items[position].id
    }
}
package com.example.laont.fragment.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.laont.R
import com.example.laont.dto.Playground

class PgListAdapter (val items: MutableList<Playground>) : BaseAdapter() {
    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Playground = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val convertView = LayoutInflater.from(parent!!.context).inflate(R.layout.item_pginfo, parent, false)

        val item = items[position]
        convertView.findViewById<TextView>(R.id.pgname_text).text = item.name
        convertView.findViewById<TextView>(R.id.pgaddress_text).text = item.address
        convertView.findViewById<TextView>(R.id.pgboard_text).text = "한 문단"
        return convertView!!
    }
}
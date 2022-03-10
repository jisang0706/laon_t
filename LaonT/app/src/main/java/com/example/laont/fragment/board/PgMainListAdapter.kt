package com.example.laont.fragment.board

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.laont.R
import com.example.laont.dto.BoardDto
import com.example.laont.dto.Playground
import kotlin.math.min

class PgMainListAdapter (val items: MutableList<BoardDto>, val pg_list: MutableList<Playground>) : BaseAdapter() {
    override fun getCount(): Int = items.size

    override fun getItem(position: Int): BoardDto = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val convertView = LayoutInflater.from(parent!!.context).inflate(R.layout.item_pg, parent, false)

        val item = items[position]
        convertView.findViewById<TextView>(R.id.pg_name_text).text = pg_list[position].name.subSequence(0, min(12, pg_list[position].name.length))
        convertView.findViewById<TextView>(R.id.board_text).text = item.content.subSequence(0, min(10, item.content.length))
        return convertView!!
    }

}
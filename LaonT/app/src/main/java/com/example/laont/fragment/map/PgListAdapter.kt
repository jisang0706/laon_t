package com.example.laont.fragment.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.laont.R
import com.example.laont.SecretData
import com.example.laont.dto.BoardListDto
import com.example.laont.dto.Playground
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class PgListAdapter (val items: MutableList<Playground>) : BaseAdapter() {
    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Playground = items[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val convertView = LayoutInflater.from(parent!!.context).inflate(R.layout.item_pginfo, parent, false)

        val item = items[position]
        convertView.findViewById<TextView>(R.id.pgname_text).text = item.name
        convertView.findViewById<TextView>(R.id.pgaddress_text).text = item.address

        val retrofit: Retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        val service: RetrofitService = retrofit.create(RetrofitService::class.java)
        val call : Call<BoardListDto> = service.getPGList(item.name, 0, 1)

        call.enqueue(object: Callback<BoardListDto> {
            override fun onResponse(call: Call<BoardListDto>, response: Response<BoardListDto>) {
                if (response.isSuccessful) {
                    convertView.findViewById<TextView>(R.id.pgboard_text).text = response.body()!!.list[0].content
                }
            }

            override fun onFailure(call: Call<BoardListDto>, t: Throwable) { }

        })
        return convertView!!
    }
}
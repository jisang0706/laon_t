package com.example.laont.fragment.board

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.laont.MainActivity
import com.example.laont.SecretData
import com.example.laont.databinding.FragmentBoardBinding
import com.example.laont.dto.BoardDto
import com.example.laont.dto.BoardListDto
import com.example.laont.dto.NotiListDto
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class BoardFragment : Fragment() {

    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!
    lateinit var retrofit: Retrofit
    lateinit var service: RetrofitService
    lateinit var area_board_items: MutableList<BoardDto>

    private lateinit var noti_text: TextView
    lateinit var area_list: ListView
    lateinit var area_list_adapter: BoardListAdapter
    lateinit var area_more_text: TextView
    lateinit var pg_list: ListView
    lateinit var pg_list_adapter: PgMainListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)

        retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        service = retrofit.create(RetrofitService::class.java)

        noti_text = binding.notiText
        setNotiText()
        noti_text.setOnClickListener {
            val intent = Intent(binding.root.context, NotiListActivity::class.java)
            startActivity(intent)
        }
        area_list = binding.areaList
        pg_list = binding.pgList

        return binding.root
    }

    fun setNotiText() {
        val call : Call<NotiListDto> = service.getNotiList(0, 1)

        call.enqueue(object: Callback<NotiListDto> {
            override fun onResponse(call: Call<NotiListDto>, response: Response<NotiListDto>) {
                if (response.isSuccessful) {
                    if (response.body()?.list!!.size > 0) {
                        noti_text.setText("전체공지 : " + response.body()?.list!![0].title)
                    }
                }
            }

            override fun onFailure(call: Call<NotiListDto>, t: Throwable) { }

        })
    }

    fun initPgBoard() {
        val parent = activity as MainActivity?

        var pg_board_items = MutableList(10) { index -> BoardDto(0, "", "", 0, 0, "") }
        pg_list_adapter = PgMainListAdapter(pg_board_items, parent!!.PG_list)
        pg_list.adapter = pg_list_adapter
        setListViewHeightBasedOnChildren(pg_list)
        for (i in 0 until parent!!.PG_list.size) {
            val call : Call<BoardListDto> = service.getPGList(parent!!.PG_list[i].name, 0, 1)

            call.enqueue(object: Callback<BoardListDto> {
                override fun onResponse(
                    call: Call<BoardListDto>,
                    response: Response<BoardListDto>
                ) {
                    if (response.isSuccessful) {
                        pg_list_adapter.items[i] = response.body()!!.list[0]
                    }
                    pg_list.setOnItemClickListener { par, view, position, id ->
                        val intent = Intent(binding.root.context, PGListActivity::class.java)
                        intent.putExtra("pg_name", parent!!.PG_list[position].name)
                        startActivityForResult(intent, SecretData.RELOAD_PG)
                    }
                    pg_list_adapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<BoardListDto>, t: Throwable) {
                    pg_list.setOnItemClickListener { par, view, position, id ->
                        val intent = Intent(binding.root.context, PGListActivity::class.java)
                        intent.putExtra("pg_name", parent!!.PG_list[position].name)
                        startActivity(intent)
                    }
                    pg_list_adapter.notifyDataSetChanged()
                }

            })
        }
    }

    fun initAreaBoard() {
        val parent = activity as MainActivity?

        area_board_items = mutableListOf()
        val call : Call<BoardListDto> = service.getAreaList(parent!!.address, 0, 1)

        call.enqueue(object: Callback<BoardListDto> {
            override fun onResponse(call: Call<BoardListDto>, response: Response<BoardListDto>) {
                if (response.isSuccessful) {
                    for (item in response.body()?.list!!) {
                        area_board_items.add(item)
                    }

                    area_list_adapter = BoardListAdapter(area_board_items)
                    area_list.adapter = area_list_adapter
                    area_more_text = binding.areaMoreText
                    area_more_text.setOnClickListener {
                        val intent = Intent(binding.root.context, AreaListActivity::class.java)
                        intent.putExtra("address", parent!!.address)
                        startActivityForResult(intent, SecretData.RELOAD_AREA)
                    }
                    val town = parent!!.address.split(" ")

                    area_list.setOnItemClickListener { parent, view, position, id ->
                        val intent = Intent(binding.root.context, AreaDetailActivity::class.java)
                        intent.putExtra("board_id", area_list_adapter.getItemPkId(position))
                        intent.putExtra("town", town[town.size-1])
                        startActivity(intent)
                    }
                }
            }

            override fun onFailure(call: Call<BoardListDto>, t: Throwable) { }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == SecretData.RELOAD_AREA) {
            if (resultCode == SecretData.RESULT_OK) {
                initAreaBoard()
            }
        }
        else if (requestCode== SecretData.RELOAD_PG) {
            if (resultCode == SecretData.RESULT_OK) {
                initPgBoard()
            }
        }
    }

    fun setListViewHeightBasedOnChildren(listView: ListView) {
        val adapter = listView.adapter
        if (adapter == null)    return

        var totalHeight = 0
        val desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST)
        for (i in 0 until adapter.count) {
            val item = adapter.getView(i, null, listView)
            item.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
            totalHeight += item.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + (listView.dividerHeight * (adapter.count - 1))
        listView.requestLayout()
    }
}
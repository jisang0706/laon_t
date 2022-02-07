package com.example.laont.fragment.board

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.laont.MainActivity
import com.example.laont.R
import com.example.laont.SecretData
import com.example.laont.databinding.FragmentBoardBinding
import com.example.laont.dto.AreaDto
import com.example.laont.dto.AreaListDto
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
    lateinit var area_items: MutableList<AreaDto>

    private lateinit var noti_text: TextView
    lateinit var area_list: ListView
    lateinit var area_list_adapter: AreaListAdapter
    lateinit var area_more_text: TextView

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

    fun initAreaBoard() {
        val parent = activity as MainActivity?

        area_items = mutableListOf<AreaDto>()
        val call : Call<AreaListDto> = service.getAreaList(parent!!.address, 0, 1)

        call.enqueue(object: Callback<AreaListDto> {
            override fun onResponse(call: Call<AreaListDto>, response: Response<AreaListDto>) {
                if (response.isSuccessful) {
                    for (item in response.body()?.list!!) {
                        area_items.add(item)
                    }

                    area_list_adapter = AreaListAdapter(area_items)
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

            override fun onFailure(call: Call<AreaListDto>, t: Throwable) { }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == SecretData.RELOAD_AREA) {
            if (resultCode == SecretData.RESULT_OK) {
                initAreaBoard()
            }
        }
    }
}
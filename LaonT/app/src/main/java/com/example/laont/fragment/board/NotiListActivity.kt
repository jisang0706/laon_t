package com.example.laont.fragment.board

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import com.example.laont.SecretData
import com.example.laont.databinding.ActivityNotiListBinding
import com.example.laont.dto.NotiDetailDto
import com.example.laont.dto.NotiListDto
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class NotiListActivity : AppCompatActivity() {

    var pagenate = 0

    private lateinit var _binding: ActivityNotiListBinding
    private val binding get() = _binding!!
    lateinit var retrofit: Retrofit
    lateinit var service: RetrofitService
    lateinit var items: MutableList<NotiDetailDto>

    lateinit var noti_list: ListView
    lateinit var list_adapter: NotiListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNotiListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        service = retrofit.create(RetrofitService::class.java)
        items = mutableListOf<NotiDetailDto>()

        noti_list = binding.notiList
        getNotiList()

        noti_list.setOnItemClickListener { parent, view, position, id ->
            getNotiDetail(view, list_adapter.getItemPkId(position))
        }

        setTitle("전체공지")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = (0).toFloat()
    }

    fun getNotiList() {
        val call : Call<NotiListDto> = service.getNotiList(pagenate, 0)

        call.enqueue(object: Callback<NotiListDto> {
            override fun onResponse(call: Call<NotiListDto>, response: Response<NotiListDto>) {
                if (response.isSuccessful) {

                    for (i in 0 until response.body()?.list!!.size) {
                        items.add(
                            response.body()?.list!![i]
                        )
                        items[items!!.size-1].created_at = items[items!!.size-1].created_at.split(" ")[0]
                    }

                    list_adapter = NotiListAdapter(items)
                    noti_list.adapter = list_adapter
                }
            }

            override fun onFailure(call: Call<NotiListDto>, t: Throwable) { }

        })
    }

    fun getNotiDetail(view: View, id: Int) {
        val call : Call<NotiDetailDto> = service.getNotiDetail(id)

        call.enqueue(object: Callback<NotiDetailDto> {
            override fun onResponse(call: Call<NotiDetailDto>, response: Response<NotiDetailDto>) {
                if (response.isSuccessful) {
                    list_adapter.addContent(view, response.body()?.content!!)
                }
            }

            override fun onFailure(call: Call<NotiDetailDto>, t: Throwable) { }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            16908332 -> { // App Bar home button
                finish()
                return true
            }
        }
        return true
    }
}
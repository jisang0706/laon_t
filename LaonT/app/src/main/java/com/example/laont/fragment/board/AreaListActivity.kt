package com.example.laont.fragment.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import com.example.laont.R
import com.example.laont.SecretData
import com.example.laont.databinding.ActivityAreaListBinding
import com.example.laont.dto.AreaDto
import com.example.laont.dto.AreaListDto
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class AreaListActivity : AppCompatActivity(), AbsListView.OnScrollListener {

    val RELOAD_AREA = 1

    var paginate = 0
    var address = ""
    var preLast = 0

    private lateinit var _binding: ActivityAreaListBinding
    private val binding get() = _binding!!
    lateinit var retrofit: Retrofit
    lateinit var service: RetrofitService
    lateinit var items: MutableList<AreaDto>

    lateinit var area_list: ListView
    lateinit var list_adapter: AreaListAdapter
    lateinit var search_button: Button
    lateinit var upload_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAreaListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        address = intent.extras!!.getString("address").toString()

        retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        service = retrofit.create(RetrofitService::class.java)
        items = mutableListOf<AreaDto>()
        list_adapter = AreaListAdapter(items)

        area_list = binding.allList
        area_list.adapter = list_adapter
        getAreaList()
        area_list.setOnScrollListener(this)

        val town = address.split(" ")
        setTitle(town[town.size - 1])

        area_list.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(binding.root.context, AreaDetailActivity::class.java)
            intent.putExtra("board_id", list_adapter.getItemPkId(position))
            intent.putExtra("town", title)
            startActivity(intent)
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = (0).toFloat()

        upload_button = binding.writeButton
        upload_button.setOnClickListener {
            val intent = Intent(binding.root.context, AreaUploadActivity::class.java)
            intent.putExtra("town", title)
            intent.putExtra("address", address)
            startActivity(intent)
        }
    }

    fun getAreaList() {
        val call: Call<AreaListDto> = service.getAreaList(address, paginate, 0)

        call.enqueue(object: Callback<AreaListDto> {
            override fun onResponse(call: Call<AreaListDto>, response: Response<AreaListDto>) {
                if (response.isSuccessful) {
                    if (!list_adapter.isEmpty)  list_adapter.items.removeAt(list_adapter.count-1)
                    for (item in response.body()?.list!!) {
                        if (list_adapter.items.find { it.id == item.id } == null)
                        list_adapter.items.add(item)
                    }
                    list_adapter.items.add(AreaDto(0, "", "", -1, -1, ""))

                    list_adapter.notifyDataSetChanged()
                    paginate++
                }
            }

            override fun onFailure(call: Call<AreaListDto>, t: Throwable) { }

        })
    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

    }

    override fun onScroll(
        view: AbsListView?,
        firstVisibleItem: Int,
        visibleItemCount: Int,
        totalItemCount: Int
    ) {
        when(view!!.id) {
            R.id.all_list -> {
                val lastItem = firstVisibleItem + visibleItemCount

                if (lastItem == list_adapter.count && preLast != lastItem) {
                    preLast = lastItem
                    getAreaList()
                }
            }
        }
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

    override fun finish() {
        setResult(SecretData.RESULT_OK, intent)
        super.finish()
    }
}
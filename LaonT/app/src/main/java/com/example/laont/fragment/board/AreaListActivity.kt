package com.example.laont.fragment.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.laont.R
import com.example.laont.SecretData
import com.example.laont.databinding.ActivityAreaListBinding
import com.example.laont.dto.ActionDto
import com.example.laont.dto.BoardDto
import com.example.laont.dto.BoardListDto
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class AreaListActivity : AppCompatActivity(), AbsListView.OnScrollListener {

    var paginate = 0
    var address = ""
    var preLast = 0
    var is_user_list = 0

    private lateinit var _binding: ActivityAreaListBinding
    private val binding get() = _binding!!
    lateinit var retrofit: Retrofit
    lateinit var service: RetrofitService
    lateinit var items: MutableList<BoardDto>

    lateinit var area_list: ListView
    lateinit var list_adapter: BoardListAdapter
    lateinit var bottom_text_layout: LinearLayout
    lateinit var search_button: Button
    lateinit var upload_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAreaListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        address = intent.extras!!.getString("address", "").toString()
        is_user_list = intent.extras!!.getInt("is_user_list", 0)

        retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        service = retrofit.create(RetrofitService::class.java)
        items = mutableListOf()
        list_adapter = BoardListAdapter(items)

        area_list = binding.allList
        area_list.adapter = list_adapter
        getAreaList()
        area_list.setOnScrollListener(this)

        var town = intent.extras!!.getString("title", "").toString()
        if (town == "") {
            town = address.split(" ").last()
        }
        setTitle(town)

        area_list.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(binding.root.context, AreaDetailActivity::class.java)
            intent.putExtra("board_id", list_adapter.getItemPkId(position))
            if (is_user_list == 0) {
                intent.putExtra("town", title)
                startActivity(intent)
            } else {
                val call: Call<ActionDto> = service.getAreaBoardName(list_adapter.getItemPkId(position))

                call.enqueue(object: Callback<ActionDto> {
                    override fun onResponse(call: Call<ActionDto>, response: Response<ActionDto>) {
                        if (response.isSuccessful) {
                            intent.putExtra("town", response.body()!!.action)
                            startActivity(intent)
                        } else {
                            intent.putExtra("town", title)
                            startActivity(intent)
                        }
                    }

                    override fun onFailure(call: Call<ActionDto>, t: Throwable) {
                        intent.putExtra("town", title)
                        startActivity(intent)
                    }

                })
            }
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = (0).toFloat()

        bottom_text_layout = binding.bottomButtonLayout
        if (is_user_list != 0)  bottom_text_layout.visibility = View.GONE

        upload_button = binding.writeButton
        upload_button.setOnClickListener {
            val intent = Intent(binding.root.context, AreaUploadActivity::class.java)
            intent.putExtra("title", title)
            intent.putExtra("address", address)
            startActivity(intent)
        }

        search_button = binding.searchButton
        search_button.setOnClickListener {
            val intent = Intent(binding.root.context, BoardSearchActivity::class.java)
            intent.putExtra("isArea", true)
            intent.putExtra("address", address)
            intent.putExtra("title", title)
            startActivity(intent)
        }
    }

    fun getAreaList() {
        val prefs = binding.root.context.getSharedPreferences("user_info", 0)
        val google_token: String = prefs.getString("google_token", "").toString()
        val call: Call<BoardListDto>
        call = when (is_user_list) {
            0 -> service.getAreaList(address, paginate, 0)
            1 -> service.getWritedAreaList(paginate, google_token)
            2 -> service.getWritedAreaCommentList(paginate, google_token)
            else -> error("No such position")
        }

        call.enqueue(object: Callback<BoardListDto> {
            override fun onResponse(call: Call<BoardListDto>, response: Response<BoardListDto>) {
                if (response.isSuccessful) {
                    if (!list_adapter.isEmpty)  list_adapter.items.removeAt(list_adapter.count-1)
                    for (item in response.body()?.list!!) {
                        if (list_adapter.items.find { it.id == item.id } == null) {
                            list_adapter.items.add(item)
                        }
                    }
                    list_adapter.items.add(BoardDto(0, "", "", -1, -1, ""))

                    list_adapter.notifyDataSetChanged()
                    paginate++
                }
            }

            override fun onFailure(call: Call<BoardListDto>, t: Throwable) { }

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
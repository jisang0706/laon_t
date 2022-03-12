package com.example.laont.fragment.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.AbsListView
import android.widget.Button
import android.widget.ListView
import com.example.laont.R
import com.example.laont.SecretData
import com.example.laont.databinding.ActivityPglistBinding
import com.example.laont.dto.BoardDto
import com.example.laont.dto.BoardListDto
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class PGListActivity : AppCompatActivity(), AbsListView.OnScrollListener {

    var paginate = 0
    var pg_name = ""
    var preLast = 0

    private lateinit var _binding: ActivityPglistBinding
    private val binding get() = _binding!!
    lateinit var retrofit: Retrofit
    lateinit var service: RetrofitService
    lateinit var items: MutableList<BoardDto>

    lateinit var pg_list: ListView
    lateinit var list_adapter: BoardListAdapter
    lateinit var search_button: Button
    lateinit var upload_button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPglistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pg_name = intent.extras!!.getString("pg_name").toString()

        retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        service = retrofit.create(RetrofitService::class.java)
        items = mutableListOf()
        list_adapter = BoardListAdapter(items)

        pg_list = binding.allList
        pg_list.adapter = list_adapter
        getPgList()
        pg_list.setOnScrollListener(this)

        setTitle(pg_name)

        pg_list.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(binding.root.context, PGDetailActivity::class.java)
            intent.putExtra("board_id", list_adapter.getItemPkId(position))
            intent.putExtra("pg_name", pg_name)
            startActivity(intent)
        }

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = (0).toFloat()

        upload_button = binding.writeButton
        upload_button.setOnClickListener {
            val intent = Intent(binding.root.context, AreaUploadActivity::class.java)
            intent.putExtra("title", title)
            startActivity(intent)
        }

        search_button = binding.searchButton
        search_button.setOnClickListener {
            val intent = Intent(binding.root.context, BoardSearchActivity::class.java)
            intent.putExtra("isArea", false)
            intent.putExtra("title", title)
            startActivity(intent)
        }
    }

    fun getPgList() {
        val call: Call<BoardListDto> = service.getPGList(pg_name, paginate, 0)

        call.enqueue(object: Callback<BoardListDto> {
            override fun onResponse(call: Call<BoardListDto>, response: Response<BoardListDto>) {
                if (response.isSuccessful) {
                    if(!list_adapter.isEmpty) list_adapter.items.removeAt(list_adapter.count-1)
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
        when (view!!.id) {
            R.id.all_list -> {
                val lastItem = firstVisibleItem + visibleItemCount

                if (lastItem == list_adapter.count && preLast != lastItem) {
                    preLast = lastItem
                    getPgList()
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
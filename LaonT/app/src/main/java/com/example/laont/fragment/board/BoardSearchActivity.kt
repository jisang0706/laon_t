package com.example.laont.fragment.board

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.core.widget.addTextChangedListener
import com.example.laont.R
import com.example.laont.SecretData
import com.example.laont.databinding.ActivityBoardSearchBinding
import com.example.laont.dto.BoardDto
import com.example.laont.dto.BoardListDto
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class BoardSearchActivity : AppCompatActivity(), AbsListView.OnScrollListener {

    var paginate = 0
    var preLast = 0
    var isArea = true
    var search: String = ""
    var address: String = ""

    private lateinit var _binding: ActivityBoardSearchBinding
    private val binding get() = _binding!!
    lateinit var retrofit: Retrofit
    lateinit var service: RetrofitService
    lateinit var items: MutableList<BoardDto>

    lateinit var searchEdit: EditText
    lateinit var searchClearButton: ImageButton
    lateinit var boardList: ListView
    lateinit var listAdapter: BoardListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBoardSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isArea = intent.extras!!.getBoolean("isArea")
        if (isArea) address = intent.extras!!.getString("address").toString()
        setTitle(intent.extras!!.getString("title"))

        retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        service = retrofit.create(RetrofitService::class.java)

        searchClearButton = binding.searchClearButton
        searchClearButton.setOnClickListener {
            searchEdit.setText("")
            searchClearButton.visibility = View.GONE
            searchEdit.clearFocus()
            clearEditKeyboard()
        }

        searchEdit = binding.searchEdit
        searchEdit.setOnEditorActionListener { v, actionId, event ->
            search = searchEdit.text.toString()
            paginate = 0
            listAdapter.items.clear()
            listAdapter.notifyDataSetChanged()
            getSearchList()
            searchEdit.clearFocus()
            clearEditKeyboard()
            true
        }

        searchEdit.addTextChangedListener {
            if (searchEdit.text.length > 0) {
                searchClearButton.visibility = View.VISIBLE
            } else {
                searchClearButton.visibility = View.GONE
            }
        }

        items = mutableListOf()
        listAdapter = BoardListAdapter(items)
        boardList = binding.searchList
        boardList.adapter = listAdapter
        boardList.setOnScrollListener(this)
    }

    fun clearEditKeyboard() {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEdit.windowToken, 0)
    }

    fun getSearchList() {
        if (search != "") {
            val call: Call<BoardListDto>
            if (isArea) {
                call = service.getAreaSearch(address, paginate, search)
            } else {
                call = service.getPGSearch(title.toString(), paginate, search)
            }
            call.enqueue(object: Callback<BoardListDto> {
                override fun onResponse(
                    call: Call<BoardListDto>,
                    response: Response<BoardListDto>
                ) {
                    if (response.isSuccessful) {
                        if (!listAdapter.isEmpty)   listAdapter.items.removeAt(listAdapter.count-1)
                        for (item in response.body()?.list!!) {
                            if (listAdapter.items.find {it.id == item.id} == null) {
                                listAdapter.items.add(item)
                            }
                        }
                        listAdapter.items.add(BoardDto(0, "", "", -1, -1, ""))

                        listAdapter.notifyDataSetChanged()
                        paginate++
                    }
                }

                override fun onFailure(call: Call<BoardListDto>, t: Throwable) { }

            })
        }
    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) { }

    override fun onScroll(
        view: AbsListView?,
        firstVisibleItem: Int,
        visibleItemCount: Int,
        totalItemCount: Int
    ) {
        when(view!!.id) {
            R.id.board_list -> {
                val lastItem = firstVisibleItem + visibleItemCount

                if (lastItem == listAdapter.count && preLast != lastItem) {
                    preLast = lastItem
                    getSearchList()
                }
            }
        }
    }
}
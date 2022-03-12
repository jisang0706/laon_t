package com.example.laont.fragment.board

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.laont.R
import com.example.laont.SecretData
import com.example.laont.databinding.ActivityAreaDetailBinding
import com.example.laont.databinding.ActivityPgdetailBinding
import com.example.laont.dto.ActionDto
import com.example.laont.dto.BoardDto
import com.example.laont.dto.CommentListDto
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class PGDetailActivity : AppCompatActivity(), BoardDetail, AbsListView.OnScrollListener {

    var board_id: Int = 0
    var paginate = 0
    var preLast: Int = 0
    var group_id: Int = 0

    lateinit var _binding: ActivityPgdetailBinding
    private val binding get() = _binding!!
    lateinit var retrofit: Retrofit
    lateinit var service: RetrofitService
    lateinit var imm: InputMethodManager

    lateinit var board_list: ListView
    lateinit var list_adapter: DetailListAdapter
    lateinit var comment_edit: EditText
    lateinit var comment_send_button: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPgdetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        board_id = intent.extras!!.getInt("board_id")

        setTitle(intent.extras!!.getString("pg_name"))

        board_list = binding.boardList

        retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        service = retrofit.create(RetrofitService::class.java)

        initBoard()

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        comment_edit = binding.commentEdit
        comment_edit.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                group_id = 0
            }
        }

        comment_send_button = binding.commentSendButton
        comment_send_button.setOnClickListener {
            uploadComment()
            comment_edit.clearFocus()
        }
    }

    override fun onStart() {
        super.onStart()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = (0).toFloat()
    }

    override fun initBoard() {
        val call : Call<BoardDto> = service.getPGDetail(board_id)

        call.enqueue(object: Callback<BoardDto> {
            override fun onResponse(call: Call<BoardDto>, response: Response<BoardDto>) {
                if (response.isSuccessful) {
                    Log.e("WOW", response.body()!!.toString())
                    list_adapter = DetailListAdapter(response.body()!!, mutableListOf(), binding.root.context, this@PGDetailActivity)
                    board_list.adapter = list_adapter

                    initComment()
                }
            }

            override fun onFailure(call: Call<BoardDto>, t: Throwable) { }

        })
    }

    override fun initComment() {
        val call : Call<CommentListDto> = service.getPGComment(board_id, paginate++)

        call.enqueue(object: Callback<CommentListDto> {
            override fun onResponse(
                call: Call<CommentListDto>,
                response: Response<CommentListDto>
            ) {
                if (response.isSuccessful) {
                    for (item in response.body()!!.list) {
                        if (list_adapter.items.find { it.id == item.id } == null) {
                            list_adapter.items.add(item)
                        }
                    }
                    list_adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<CommentListDto>, t: Throwable) { }
        })
    }

    override fun writeReply(group_id: Int) {
        comment_edit.requestFocus()
        imm.showSoftInput(comment_edit, 0)
        this.group_id = group_id
    }

    override fun uploadComment() {
        imm.hideSoftInputFromWindow(comment_edit.windowToken, 0)
        val prefs = binding.root.context.getSharedPreferences("user_info", 0)
        val google_token: String = prefs.getString("google_token", "").toString()
        val call : Call<CommentListDto> = service.uploadPGComment(board_id, google_token, group_id, comment_edit.text.toString())
        comment_edit.setText("")

        call.enqueue(object: Callback<CommentListDto> {
            override fun onResponse(
                call: Call<CommentListDto>,
                response: Response<CommentListDto>
            ) {
                if (response.isSuccessful) {
                    for (i in 0 until list_adapter.items.size) {
                        if (list_adapter.items[i].id == response.body()!!.list[0].id) {
                            list_adapter.items[i] = response.body()!!.list[0]
                            list_adapter.notifyDataSetChanged()
                            return
                        }
                    }
                    list_adapter.items.add(response.body()!!.list[0])
                    list_adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<CommentListDto>, t: Throwable) { }

        })
    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) { }

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
                    initComment()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_board, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_delete -> {
                delete()
                return true
            }
            16908332 -> { // App Bar home button
                finish()
                return true
            }
        }
        return true
    }

    override fun delete() {
        val prefs = getSharedPreferences("user_info", 0)
        val google_token: String = prefs.getString("google_token", "").toString()
        val builder: AlertDialog.Builder = AlertDialog.Builder(binding.root.context, R.style.MyDialogTheme)
        builder.setTitle("삭제하시겠습니까?")
        builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
            val call = service.deletePG(board_id, google_token)

            call.enqueue(object: Callback<ActionDto> {
                override fun onResponse(call: Call<ActionDto>, response: Response<ActionDto>) {
                    if (response.isSuccessful) {
                        if(response.body()!!.action == "1") {
                            Toast.makeText(binding.root.context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(binding.root.context, "작성자가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ActionDto>, t: Throwable) { }

            })
        })
        builder.setNegativeButton("취소", null)
        builder.create().show()
    }
}
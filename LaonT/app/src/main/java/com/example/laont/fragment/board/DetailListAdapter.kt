package com.example.laont.fragment.board

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.laont.R
import com.example.laont.SecretData
import com.example.laont.dto.*
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class DetailListAdapter (val board: BoardDto, val items: MutableList<CommentDto>,
                         val context: Context, val parentClass: BoardDetail) : BaseAdapter() {

    var retrofit: Retrofit
    var service: RetrofitService

    init {
        retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        service = retrofit.create(RetrofitService::class.java)
    }

    override fun getCount(): Int {
        var cnt = 1
        for(item in items) {
            cnt += item.reply.size + 1
        }
        return cnt
    }

    override fun getItem(position: Int): Any {
        var position = position
        if (position == 0) {
            return board
        }

        position--
        var cnt = 0
        for (item in items) {
            if (position == cnt) {
                return item
            }
            if (position - cnt <= item.reply.size) {
                return item.reply[position - cnt - 1]
            }
            cnt += item.reply.size + 1
        }
        return board
    }

    fun getParent(position: Int): CommentDto {
        var position = position

        position--
        var cnt = 0
        for (item in items) {
            if (position == cnt || position - cnt <= item.reply.size) {
                return item
            }
            cnt += item.reply.size + 1
        }
        return items[0]
    }

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = getItem(position)
        if (item is BoardDto) {
            val convertView = LayoutInflater.from(parent!!.context).inflate(R.layout.item_board_detail, parent, false)

            val writer_text = convertView.findViewById<TextView>(R.id.writer_text)
            writer_text.text = board.writer_nickname
            val created_text = convertView.findViewById<TextView>(R.id.created_text)
            created_text.text = board.created_at
            val content_text = convertView.findViewById<TextView>(R.id.content_text)
            content_text.text = board.content
            val comment_cnt_text = convertView.findViewById<TextView>(R.id.comment_text)
            comment_cnt_text.text = "?????? " + board.comment + "???"
            val like_button = convertView.findViewById<Button>(R.id.like_button)
            like_button.text = board.like.toString()
            like_button.setOnClickListener {
                likeBoard(like_button)
            }
            return convertView
        } else if (item is CommentDto) {
            val convertView = LayoutInflater.from(parent!!.context).inflate(R.layout.item_comment, parent, false)

            val writer_text = convertView.findViewById<TextView>(R.id.writer_text)
            writer_text.text = item.writer_nickname
            val content_text = convertView.findViewById<TextView>(R.id.content_text)
            content_text.text = item.content
            val created_text = convertView.findViewById<TextView>(R.id.created_text)
            created_text.text = item.created_at
            val reply_button = convertView.findViewById<ImageButton>(R.id.reply_button)
            reply_button.setOnClickListener {
                parentClass.writeReply(getParent(position).id)
            }
            val menu_button = convertView.findViewById<ImageButton>(R.id.menu_button)
            menu_button.setOnClickListener {
                showDialog(position)
            }
            return convertView
        } else if (item is ReplyDto) {
            val convertView = LayoutInflater.from(parent!!.context).inflate(R.layout.item_reply, parent, false)

            val writer_text = convertView.findViewById<TextView>(R.id.writer_text)
            writer_text.text = item.writer_nickname
            val content_text = convertView.findViewById<TextView>(R.id.content_text)
            content_text.text = item.content
            val created_text = convertView.findViewById<TextView>(R.id.created_text)
            val menu_button = convertView.findViewById<ImageButton>(R.id.menu_button)
            menu_button.setOnClickListener {
                showDialog(position)
            }
            created_text.text = item.created_at
            return convertView
        }
        return LayoutInflater.from(parent!!.context).inflate(R.layout.item_comment, parent, false)
    }

    fun likeBoard(like_button: Button) {
        val prefs = context.getSharedPreferences("user_info", 0)
        val google_token: String = prefs.getString("google_token", "").toString()
        if (google_token != "") {
            var call: Call<CountDto>
            if (parentClass is AreaDetailActivity) {
                call = service.likeArea(board.id, google_token)
            }
            else {
                call = service.likePG(board.id, google_token)
            }

            call.enqueue(object: Callback<CountDto> {
                override fun onResponse(call: Call<CountDto>, response: Response<CountDto>) {
                    if (response.isSuccessful) {
                        like_button.text = response.body()?.count.toString()
                    }
                }

                override fun onFailure(call: Call<CountDto>, t: Throwable) { }

            })
        }
    }

    fun showDialog(position: Int) {
        val item_list: Array<String> = arrayOf("????????????")
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setItems(item_list) { dialog, pos ->
            when (pos) {
                0 -> {
                    delete(position)
                }
            }
        }
        builder.show()
    }

    fun delete(position: Int) {
        val item = getItem(position)
        val prefs = context.getSharedPreferences("user_info", 0)
        val google_token: String = prefs.getString("google_token", "").toString()
        val builder: AlertDialog.Builder = AlertDialog.Builder(context, R.style.MyDialogTheme)
        builder.setTitle("?????????????????????????")
        builder.setPositiveButton("??????", DialogInterface.OnClickListener { dialog, which ->
            if (item is CommentDto) {
                var call : Call<ActionDto>
                if (parentClass is AreaDetailActivity) {
                    call = service.deleteAreaComment((item as CommentDto).id, google_token)
                } else {
                    call = service.deletePGComment((item as CommentDto).id, google_token)
                }
                call.enqueue(object: Callback<ActionDto> {
                    override fun onResponse(call: Call<ActionDto>, response: Response<ActionDto>) {
                        if (response.isSuccessful) {
                            if(response.body()!!.action == "1") {
                                Toast.makeText(context, "?????????????????????.", Toast.LENGTH_SHORT).show()

                            items.remove(item)
                            notifyDataSetChanged()
                            } else {
                                Toast.makeText(context, "???????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ActionDto>, t: Throwable) { }

                })
            }
            else {
                var call : Call<ActionDto>
                if (parentClass is AreaDetailActivity) {
                    call = service.deleteAreaComment((item as ReplyDto).id, google_token)
                } else {
                    call = service.deletePGComment((item as ReplyDto).id, google_token)
                }
                call.enqueue(object: Callback<ActionDto> {
                    override fun onResponse(call: Call<ActionDto>, response: Response<ActionDto>) {
                        if (response.isSuccessful) {
                            if(response.body()!!.action == "1") {
                                Toast.makeText(context, "?????????????????????.", Toast.LENGTH_SHORT).show()
                                getParent(position).reply.remove(item)
                                notifyDataSetChanged()
                            } else {
                                Toast.makeText(context, "???????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ActionDto>, t: Throwable) { }

                })
            }
        })
        builder.setNegativeButton("??????", null)
        builder.create().show()
    }
}
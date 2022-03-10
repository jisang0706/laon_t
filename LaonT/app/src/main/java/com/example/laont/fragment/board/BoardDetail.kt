package com.example.laont.fragment.board

import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import com.example.laont.databinding.ActivityAreaDetailBinding
import com.example.laont.dto.BoardDto
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Retrofit

interface BoardDetail {

    fun initBoard()

    fun initComment()

    fun writeReply(group_id: Int)

    fun uploadComment()

    fun delete()


}
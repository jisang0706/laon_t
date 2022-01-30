package com.example.laont.fragment.board

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.laont.SecretData
import com.example.laont.databinding.FragmentBoardBinding
import com.example.laont.dto.NotiListDto
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardFragment : Fragment() {
    private var _binding: FragmentBoardBinding? = null
    private val binding get() = _binding!!

    private lateinit var noti_text: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoardBinding.inflate(inflater, container, false)

        noti_text = binding.notiText
        setNotiText()
        noti_text.setOnClickListener {
            val intent = Intent(binding.root.context, NotiListActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    fun setNotiText() {
        val retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        val service = retrofit.create(RetrofitService::class.java)
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
}
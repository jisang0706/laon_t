package com.example.laont.fragment.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.example.laont.SecretData
import com.example.laont.databinding.ActivityAreaUploadBinding
import com.example.laont.dto.IdDto
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class AreaUploadActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityAreaUploadBinding
    private val binding get() = _binding!!
    lateinit var retrofit: Retrofit
    lateinit var service: RetrofitService

    lateinit var area_town_text: TextView
    lateinit var back_button: ImageButton
    lateinit var allow_button: Button
    lateinit var content_edit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAreaUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        service = retrofit.create(RetrofitService::class.java)

        area_town_text = binding.townText
        area_town_text.text = intent.extras!!.getString("town").toString()
        back_button = binding.backButton
        back_button.setOnClickListener { finish() }
        allow_button = binding.allowButton
        allow_button.setOnClickListener { upload() }
        content_edit = binding.contentEdit
    }

    fun upload() {
        val prefs = getSharedPreferences("user_info", 0)
        val google_token: String = prefs.getString("google_token", "").toString()
        val address = intent.extras!!.getString("address").toString()
        if (google_token != "") {
            val call: Call<IdDto> = service.uploadArea(
                google_token,
                address,
                area_town_text.text.toString(),
                content_edit.text.toString())

            call.enqueue(object: Callback<IdDto> {
                override fun onResponse(call: Call<IdDto>, response: Response<IdDto>) {
                    if (response.isSuccessful) {
                        val intent = Intent(binding.root.context, AreaDetailActivity::class.java)
                        intent.putExtra("board_id", response.body()!!.id)
                        intent.putExtra("town", area_town_text.text)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onFailure(call: Call<IdDto>, t: Throwable) { }

            })
        }
    }
}
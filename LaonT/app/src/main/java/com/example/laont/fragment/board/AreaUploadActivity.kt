package com.example.laont.fragment.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.example.laont.SecretData
import com.example.laont.databinding.ActivityBoardUploadBinding
import com.example.laont.dto.IdDto
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class AreaUploadActivity : AppCompatActivity() {

    var name: String = ""

    private lateinit var _binding: ActivityBoardUploadBinding
    private val binding get() = _binding!!
    lateinit var retrofit: Retrofit
    lateinit var service: RetrofitService

    lateinit var title_text: TextView
    lateinit var back_button: ImageButton
    lateinit var allow_button: Button
    lateinit var content_edit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBoardUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        service = retrofit.create(RetrofitService::class.java)

        title_text = binding.titleText
        name =  intent.extras!!.getString("title").toString()
        if (name.length > 12)   title_text.text = name.substring(0, 12) + ".."
        else                    title_text.text = name
        back_button = binding.backButton
        back_button.setOnClickListener { finish() }
        allow_button = binding.allowButton
        allow_button.setOnClickListener { upload() }
        content_edit = binding.contentEdit
    }

    fun upload() {
        val prefs = getSharedPreferences("user_info", 0)
        val google_token: String = prefs.getString("google_token", "").toString()
        val address: String = intent.extras!!.getString("address", "")
        if (google_token != "") {
            if (address != "") {
                val call: Call<IdDto> = service.uploadArea(
                    google_token,
                    address,
                    name,
                    content_edit.text.toString()
                )

                call.enqueue(object : Callback<IdDto> {
                    override fun onResponse(call: Call<IdDto>, response: Response<IdDto>) {
                        if (response.isSuccessful) {
                            val intent =
                                Intent(binding.root.context, AreaDetailActivity::class.java)
                            intent.putExtra("board_id", response.body()!!.id)
                            intent.putExtra("town", name)
                            startActivity(intent)
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<IdDto>, t: Throwable) {}

                })
            } else {
                val call: Call<IdDto> = service.uploadPG(
                    google_token,
                    name,
                    content_edit.text.toString()
                )

                call.enqueue(object : Callback<IdDto> {
                    override fun onResponse(call: Call<IdDto>, response: Response<IdDto>) {
                        if (response.isSuccessful) {
                            val intent = Intent(binding.root.context, PGDetailActivity::class.java)
                            intent.putExtra("board_id", response.body()!!.id)
                            intent.putExtra("pg_name", name)
                            startActivity(intent)
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<IdDto>, t: Throwable) { }

                })
            }
        }
    }
}
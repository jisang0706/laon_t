package com.example.laont.intro

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.preference.Preference
import com.example.laont.MainActivity
import com.example.laont.SecretData
import com.example.laont.databinding.ActivityNicknameJoinBinding
import com.example.laont.dto.ActionDto
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class NicknameJoin : AppCompatActivity() {

    private lateinit var mBinding: ActivityNicknameJoinBinding
    private val binding get() = mBinding!!
    private lateinit var nicknameEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityNicknameJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val commentText: TextView = binding.nicknameCommentTextview

        nicknameEdit = binding.nicknameEdittext
        nicknameEdit.setFilters(arrayOf<InputFilter>(LengthFilter(10)))
        nicknameEdit.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            val ps = Pattern.compile("[a-z0-9ㄱ-ㅎ가-힣]+")
            if (source == "" || ps.matcher(source).matches()) {
                return@InputFilter source
            }
            ""
        }, InputFilter.LengthFilter(10))
        nicknameEdit.addTextChangedListener {
            if (nicknameEdit.text.length >= 2) {
                commentText.visibility = View.INVISIBLE
            } else {
                commentText.visibility = View.VISIBLE
            }
        }

        val allowButton: Button = mBinding.nicknameAllowButton
        allowButton.setOnClickListener {
            if (!commentText.isVisible) { joinUser() }
        }
    }

    fun joinUser() {
        val prefs = getSharedPreferences("user_info", 0)
        Log.e("WOW", prefs.getString("google_token", "").toString() + "ASAD")
        val retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        val service = retrofit.create(RetrofitService::class.java)
        val call : Call<ActionDto> = service.userJoin(
            prefs.getString("google_token", "").toString(),
            nicknameEdit.text.toString(),
            prefs.getString("email", "").toString()
        )

        call.enqueue(object: Callback<ActionDto> {
            override fun onResponse(call: Call<ActionDto>, response: Response<ActionDto>) {
                if (response.isSuccessful) {
                    Log.e("WOW", response.body()?.action.toString())
                    if (response.body()?.action.toString() == "1") {
                        val prefsEditor = prefs.edit()
                        prefsEditor.putString("nickname", nicknameEdit.text.toString())
                        prefsEditor.apply()
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        Toast.makeText(applicationContext, "이미 존재하는 닉네임입니다.", Toast.LENGTH_LONG).show()
                        Log.e("WOW", prefs.getString("google_token", "").toString() + nicknameEdit.text.toString() + prefs.getString("email", "").toString())
                    }
                }
            }

            override fun onFailure(call: Call<ActionDto>, t: Throwable) {
                Toast.makeText(applicationContext, "인터넷 연결 상태를 확인해주세요.", Toast.LENGTH_LONG).show()
            }

        })
    }
}
package com.example.laont.fragment.userpage

import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.example.laont.R
import com.example.laont.SecretData
import com.example.laont.databinding.ActivityNicknameJoinBinding
import com.example.laont.dto.ActionDto
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.regex.Pattern

class NicknameActivity : AppCompatActivity() {
    private var _binding: ActivityNicknameJoinBinding? = null
    private val binding get() = _binding!!
    lateinit var retrofit: Retrofit
    lateinit var service: RetrofitService
    lateinit var prefs: SharedPreferences

    var google_token: String = ""

    private lateinit var nicknameEdit: EditText
    private lateinit var commentText: TextView
    private lateinit var allowButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNicknameJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        service = retrofit.create(RetrofitService::class.java)

        prefs = getSharedPreferences("user_info", 0)
        google_token = prefs.getString("google_token", "").toString()

        commentText = binding.nicknameCommentTextview

        nicknameEdit = binding.nicknameEdittext
        nicknameEdit.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(10)))
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

        allowButton = binding.nicknameAllowButton
        allowButton.setOnClickListener {
            if (!commentText.isVisible) { setNickname(nicknameEdit.text.toString()) }
        }
    }

    fun setNickname(nickname: String) {
        Log.e("WOW", google_token)
        val call : Call<ActionDto> = service.setNickname(
            google_token,
            nickname
        )

        call.enqueue(object: Callback<ActionDto> {
            override fun onResponse(call: Call<ActionDto>, response: Response<ActionDto>) {
                if (response.isSuccessful) {
                    if (response.body()?.action.toString() == "1") {
                        val prefsEditor = prefs.edit()
                        prefsEditor.putString("nickname", nickname)
                        prefsEditor.apply()
                        nicknameChangeSuccess()
                    } else {
                        Toast.makeText(applicationContext, "이미 존재하는 닉네임입니다.", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call<ActionDto>, t: Throwable) { }

        })
    }

    fun nicknameChangeSuccess() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(binding.root.context, R.style.MyDialogTheme)
        builder.setTitle("닉네임을 변경했습니다.")
        builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
            setResult(SecretData.RESULT_OK, intent)
            finish()
        })
        builder.create().show()
    }
}
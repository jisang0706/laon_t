package com.example.laont.fragment.userpage

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.laont.SecretData
import com.example.laont.databinding.FragmentUserBinding
import com.example.laont.fragment.board.AreaListActivity
import com.example.laont.fragment.board.PGListActivity
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import retrofit2.Retrofit

class UserFragment : Fragment() {
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!
    lateinit var retrofit: Retrofit
    lateinit var service: RetrofitService

    lateinit var nicknameText: TextView
    lateinit var nicknameButton: ImageButton
    lateinit var writedAreaText: TextView
    lateinit var writedAreaCommentText: TextView
    lateinit var writedPGText: TextView
    lateinit var writedPGCommentText: TextView

    lateinit var prefs: SharedPreferences
    var nickname: String = ""
    var google_token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
        service = retrofit.create(RetrofitService::class.java)

        prefs = this.requireActivity().getSharedPreferences("user_info", 0)
        google_token = prefs.getString("google_token", "").toString()

        nicknameText = binding.nicknameText
        setNickname()

        nicknameButton = binding.changeNicknameButton
        nicknameButton.setOnClickListener {
            val intent = Intent(binding.root.context, NicknameActivity::class.java)
            startActivityForResult(intent, SecretData.RELOAD_NICKNAME)
        }

        writedAreaText = binding.writedAreaText
        writedAreaText.setOnClickListener {
            val intent = Intent(binding.root.context, AreaListActivity::class.java)
            intent.putExtra("title", "작성한 동네 글")
            intent.putExtra("is_user_list", 1)
            startActivity(intent)
        }

        writedAreaCommentText = binding.writedAreaCommentText
        writedAreaCommentText.setOnClickListener {
            val intent = Intent(binding.root.context, AreaListActivity::class.java)
            intent.putExtra("title", "작성한 동네 댓글")
            intent.putExtra("is_user_list", 2)
            startActivity(intent)
        }

        writedPGText = binding.writedPgText
        writedPGText.setOnClickListener {
            val intent = Intent(binding.root.context, PGListActivity::class.java)
            intent.putExtra("pg_name", "작성한 놀이터 글")
            intent.putExtra("is_user_list", 1)
            startActivity(intent)
        }

        writedPGCommentText = binding.writedPgCommentText
        writedPGCommentText.setOnClickListener {
            val intent = Intent(binding.root.context, PGListActivity::class.java)
            intent.putExtra("pg_name", "작성한 놀이터 댓글")
            intent.putExtra("is_user_list", 2)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SecretData.RELOAD_NICKNAME) {
            if (resultCode == SecretData.RESULT_OK) {
                setNickname()
            }
        }
    }

    fun setNickname() {
        nickname = prefs.getString("nickname", "").toString()
        nicknameText.text = nickname
    }
}
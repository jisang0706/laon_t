package com.example.laont.intro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import com.example.laont.MainActivity
import com.example.laont.R
import com.example.laont.SecretData
import com.example.laont.databinding.ActivityIntroBinding
import com.example.laont.dto.ActionDto
import com.example.laont.dto.UserInfoDto
import com.example.laont.retrofit.RetrofitCreator
import com.example.laont.retrofit.RetrofitService
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IntroActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityIntroBinding
    private val binding get() = mBinding!!
    private lateinit var loginResultLauncher: ActivityResultLauncher<Intent>
    private val firebaseAuth= FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            loginResultLauncher.launch(googleSignInIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        loginResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK) {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(it.data)
                result.let {
                    if (it!!.isSuccess) {
                        firebaseLogin(result!!.signInAccount)
                    }
                }
            }
        }
    }

    private val googleSignInIntent by lazy { // 구글 로그인 인텐트
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        GoogleSignIn.getClient(this, gso).signInIntent
    }

    private fun firebaseLogin(googleAcount: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(googleAcount.idToken, null)

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val retrofit = RetrofitCreator.defaultRetrofit(SecretData.SERVER_URI)
                val service = retrofit.create(RetrofitService::class.java)
                val call : Call<UserInfoDto> = service.userLogin(it.result?.user?.uid.toString())

                call.enqueue(object : Callback<UserInfoDto> {
                    override fun onResponse(call: Call<UserInfoDto>, response: Response<UserInfoDto>) {
                        if (response.isSuccessful) {
                            val prefs = getSharedPreferences("user_info", 0)
                            val prefsEditor = prefs.edit()
                            prefsEditor.clear()
                            prefsEditor.putString("google_token", it.result?.user?.uid)
                            prefsEditor.putString("email", it.result?.user?.email)
                            prefsEditor.apply()
                            if (response.body()?.action.toString() == "1"){
                                prefsEditor.putString("nickname", response.body()?.nickname)
                                prefsEditor.apply()

                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else {
                                val intent = Intent(applicationContext, NicknameJoin::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                    override fun onFailure(call: Call<UserInfoDto>, t: Throwable) {
                        Toast.makeText(applicationContext, "인터넷 연결 상태를 확인해주세요.", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }.addOnFailureListener {

            Toast.makeText(applicationContext, "인터넷 연결 상태를 확인해주세요.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseAuth.signOut()
    }
}
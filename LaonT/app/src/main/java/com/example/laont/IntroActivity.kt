package com.example.laont

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.laont.databinding.ActivityIntroBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

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
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
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
                Toast.makeText(this, it.result?.user?.displayName, Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {

        }
    }
}
package com.example.marketplacefirebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = Firebase.auth.currentUser

        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            setContentView(R.layout.activity_login)

            findViewById<Button>(R.id.login)?.setOnClickListener {
                val userEmail = findViewById<EditText>(R.id.email)?.text.toString()
                val password = findViewById<EditText>(R.id.password)?.text.toString()
                doLogin(userEmail, password)
            }

            findViewById<Button>(R.id.signup)?.setOnClickListener {
                startActivity(Intent(this, SignUpActivity::class.java))
            }
        }
    }

    private fun doLogin(userEmail: String, password: String) {
        if (userEmail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
            return
        }

        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    startActivity(
                        Intent(this, FirestoreActivity::class.java))
                    finish()
                } else {
                    Log.w("LoginActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "인증 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
}


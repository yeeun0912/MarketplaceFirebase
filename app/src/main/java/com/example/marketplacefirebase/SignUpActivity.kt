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

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        findViewById<Button>(R.id.createAccount)?.setOnClickListener {
            val userName = findViewById<EditText>(R.id.userName)?.text.toString()
            val birthdate = findViewById<EditText>(R.id.birthdate)?.text.toString()
            val userEmail = findViewById<EditText>(R.id.signupEmail)?.text.toString()
            val password = findViewById<EditText>(R.id.signupPassword)?.text.toString()

            createAccount(userName, birthdate, userEmail, password)
        }
    }

    private fun createAccount(userName: String, birthdate: String, userEmail: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    loginUser(userEmail, password)
                } else {
                    Log.w("SignUpActivity", "createUserWithEmail", it.exception)
                    Toast.makeText(this, "계정 생성 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginUser(userEmail: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    startActivity(Intent(this, FirestoreActivity::class.java))
                    finish()
                } else {
                    Log.w("SignUpActivity", "signInWithEmail", it.exception)
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
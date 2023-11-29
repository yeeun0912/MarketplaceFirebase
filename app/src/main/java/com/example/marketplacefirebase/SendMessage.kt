package com.example.marketplacefirebase

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SendMessage : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

//        val sharedUserEmail : SharedPreferences = getSharedPreferences("sellerEmail", MODE_PRIVATE)
//        val sellerEmail = sharedUserEmail.getString("sellerEmail", "알 수 없는 판매자").toString()
//
        val sellerEmail = intent.getSerializableExtra("sellerEmail").toString()

        val auth = Firebase.auth
        val user = auth.currentUser
        val currentUserEmail = user?.email

        val db : FirebaseFirestore = Firebase.firestore
        val usersCollectionRef = db.collection("users")

        val sendButton = findViewById<Button>(R.id.sendButton)

        sendButton.setOnClickListener {
            val editMessage = findViewById<EditText>(R.id.editMessage)
            val usersMap = hashMapOf(
                "currentUserEmail" to currentUserEmail,
                "message" to editMessage.text.toString(),
                "sellerEmail" to sellerEmail
            )
            usersCollectionRef.add(usersMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "메세지가 성공적으로 보내졌습니다.", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener{
                    Toast.makeText(this, "메세지 보내기가 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }

            val intent = Intent(this, FirestoreActivity::class.java)
            startActivity(intent)
        }
    }
}
package com.example.marketplacefirebase

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SendMessage : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val sharedUserEmail : SharedPreferences = getSharedPreferences("userEmail", MODE_PRIVATE)
        val sellerEmail = sharedUserEmail.getString("userEmail", "").toString()

        val auth = Firebase.auth
        val user = auth.currentUser
        val currentUserEmail = user?.email

        val db : FirebaseFirestore = Firebase.firestore
        val usersCollectionRef = db.collection("users")

        val sendButton = findViewById<Button>(R.id.sendButton)

        sendButton.setOnClickListener {
            val editMessage = findViewById<EditText>(R.id.editMessage)
            val usersMap = hashMapOf(
                "userEmail" to currentUserEmail,
                "message" to editMessage
            )
            usersCollectionRef.document(sellerEmail).set(usersMap)
                .addOnSuccessListener { }.addOnFailureListener{ }
            val intent = Intent(this, FirestoreActivity::class.java)
            startActivity(intent)
        }
    }
}
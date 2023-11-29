package com.example.marketplacefirebase

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ItemDetailActivity : AppCompatActivity() {

    private lateinit var item: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        val item = intent.getSerializableExtra("item") as Item

        if (item == null) {
            Toast.makeText(this, "아이템 데이터를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val auth = Firebase.auth
        val user = auth.currentUser
        //val currentUserId = user?.uid
        val currentUserEmail = user?.email

        val textTitle: TextView = findViewById(R.id.textTitle)
        val textContent: TextView = findViewById(R.id.textContent)
        val textPrice: TextView = findViewById(R.id.textPrice)
        val textStatus: TextView = findViewById(R.id.textStatus)
        val textSeller: TextView = findViewById(R.id.textSeller)
        val buttonSendMessage: Button = findViewById(R.id.buttonSendMessage)

        textTitle.text = item.title
        textPrice.text = item.price.toString()
        textStatus.text = if (item.status) "판매 중" else "판매 완료"
        textContent.text = item.description

        textSeller.text = item.sellerEmail ?: "알 수 없는 판매자"

        buttonSendMessage.setOnClickListener {
            val intent = Intent(this, SendMessage::class.java)
            intent.putExtra("sellerEmail", textSeller.text.toString())
            startActivity(intent)
        }
    }
}

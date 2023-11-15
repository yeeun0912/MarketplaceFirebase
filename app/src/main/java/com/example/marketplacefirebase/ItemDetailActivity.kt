package com.example.marketplacefirebase

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ItemDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        // Get item information from the intent
        val item = intent.getSerializableExtra("item") as Item

        // Set up views
        val textTitle: TextView = findViewById(R.id.textTitle)
        val textContent: TextView = findViewById(R.id.textContent)
        val textPrice: TextView = findViewById(R.id.textPrice)
        val textStatus: TextView = findViewById(R.id.textStatus)
        val textSeller: TextView = findViewById(R.id.textSeller)
        val buttonSendMessage: Button = findViewById(R.id.buttonSendMessage)


        textTitle.text = item.title
        textContent.text = item.content
        textPrice.text = item.price.toString()
        textStatus.text = if (item.status) "판매 중" else "판매 완료"
        textSeller.text = item.seller


        buttonSendMessage.setOnClickListener {

        }
    }
}
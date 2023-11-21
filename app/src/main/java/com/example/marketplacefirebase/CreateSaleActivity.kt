package com.example.marketplacefirebase

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateSaleActivity : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editPrice: EditText
    private lateinit var editDescription: EditText
    private lateinit var buttonSubmit: Button
    private lateinit var textSeller: TextView
    private lateinit var spinnerStatus: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createsale)

        val auth = Firebase.auth
        val user = auth.currentUser
        //val currentUserId = user?.uid
        val currentUserEmail = user?.email

        editName = findViewById(R.id.editName)
        editPrice = findViewById(R.id.editPrice)
        editDescription = findViewById(R.id.editDescription)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        textSeller = findViewById(R.id.textSeller)

        spinnerStatus = findViewById(R.id.spinnerStatus)
        val statusOptions = listOf("판매 중", "판매 완료")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter

        textSeller.text = currentUserEmail ?: "Unknown User"

        buttonSubmit.setOnClickListener{
            val itemName = editName.text.toString()
            val itemPrice = editPrice.text.toString().toIntOrNull()
            val itemDescription = editDescription.text.toString()
            val selectedStatus = spinnerStatus.selectedItem.toString()

            if (itemName.isNotEmpty() && itemPrice != null){
                addDataToFirestore(itemName, itemPrice, itemDescription, currentUserEmail, selectedStatus)
                val intent = Intent(this, FirestoreActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "모든 칸을 채워주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addDataToFirestore(name: String, price: Int, description: String, sellerEmail: String?, status: String){
        val db = Firebase.firestore
        val itemsCollection = db.collection("items")

        val user = Firebase.auth.currentUser
        val userId = user?.uid

        val newItem = hashMapOf(
            "name" to name,
            "price" to price,
            "status" to (status == "판매 중"),
            "description" to description,
            "userId" to userId,
            "sellerEmail" to sellerEmail
        )

        textSeller.text = sellerEmail ?: "Unknown User"

        itemsCollection.add(newItem)
            .addOnSuccessListener{ documentReference ->
                Log.d("CreateSaleActivity", "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(this, "상품이 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("CreateSaleActivity", "Error adding document", e)
                Toast.makeText(this, "상품 추가 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }

    }
}
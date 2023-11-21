package com.example.marketplacefirebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditSaleActivity : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editPrice: EditText
    private lateinit var editDescription: EditText
    private lateinit var textSeller: TextView
    private lateinit var buttonUpdate: Button
    private lateinit var spinnerStatus: Spinner

    private lateinit var documentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editsale)

        val auth = Firebase.auth
        val user = auth.currentUser
        //val currentUserId = user?.uid
        val currentUserEmail = user?.email

        editName = findViewById(R.id.editName)
        editPrice = findViewById(R.id.editPrice)
        editDescription = findViewById(R.id.editDescription)
        buttonUpdate = findViewById(R.id.buttonUpdate)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        textSeller = findViewById(R.id.textSeller)

        spinnerStatus = findViewById(R.id.spinnerStatus)
        val statusOptions = listOf("판매 중", "판매 완료")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter

        textSeller.text = currentUserEmail

        documentId = intent.getStringExtra("ITEM_ID") ?: ""

        if (documentId.isEmpty()) {
            Toast.makeText(this, "상품 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            loadDataFromFirestore()
        }

        buttonUpdate.setOnClickListener {
            val itemName = editName.text.toString()
            val itemPrice = editPrice.text.toString().toIntOrNull()
            val itemDescription = editDescription.text.toString()
            val selectedPosition = spinnerStatus.selectedItemPosition
            val itemStatus = when (selectedPosition) {
                0 -> true
                1 -> false
                else -> true
            }

            if (itemPrice != null) {
                updateDataInFirestore(itemName, itemPrice, itemDescription, itemStatus)
                val intent = Intent(this, FirestoreActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "모든 칸을 채워주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadDataFromFirestore() {
        val db = Firebase.firestore
        val itemDocument = db.collection("items").document(documentId)

        itemDocument.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val itemUserId = document.getString("userId")

                    val currentUser = Firebase.auth.currentUser
                    val currentUserId = currentUser?.uid

                    if (itemUserId == currentUserId) {
                        val itemName = document.getString("name")
                        val itemPrice = document.getLong("price")?.toInt()
                        val itemDescription = document.getString("description")
                        val itemStatus = document.getBoolean("status")

                        editName.setText(itemName)
                        editPrice.setText(itemPrice.toString())
                        editDescription.setText(itemDescription)
                    } else {
                        Toast.makeText(this, "수정 권한이 없습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    Log.d("EditSaleActivity", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("EditSaleActivity", "Error getting document", exception)
            }
    }

    private fun updateDataInFirestore(name: String, price: Int, description: String, status: Boolean) {
        val db = Firebase.firestore
        val itemDocument = db.collection("items").document(documentId)

        val updatedItem = hashMapOf<String, Any>(
            "name" to name,
            "price" to price,
            "description" to description,
            "status" to status
        )

        itemDocument.update(updatedItem)
            .addOnSuccessListener {
                Log.d("EditSaleActivity", "DocumentSnapshot updated successfully")
                Toast.makeText(this, "상품이 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show()
                finish() // Close the activity after successful update
            }
            .addOnFailureListener { e ->
                Log.e("EditSaleActivity", "Error updating document", e)
                Toast.makeText(this, "상품 수정 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
    }
}
package com.example.marketplacefirebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class FirestoreActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var textSnapshotListener: TextView
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore)

        findViewById<Button>(R.id.logout)?.setOnClickListener {
            Firebase.auth.signOut()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerViewItems)
        progressBar = findViewById(R.id.progressWait)
        textSnapshotListener = findViewById(R.id.textSnapshotListener)

        itemAdapter = ItemAdapter(Firebase.auth.currentUser?.uid ?: "")
        recyclerView.adapter = itemAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        val spinnerFilter: Spinner = findViewById(R.id.spinnerFilter)

        ArrayAdapter.createFromResource(
            this,
            R.array.filter_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerFilter.adapter = adapter
        }

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Log.d("FilterDebug", "Selected Filter: ${parent.getItemAtPosition(position)}")
                fetchDataFromFirestore(parent.getItemAtPosition(position).toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        spinnerFilter.setSelection(resources.getStringArray(R.array.filter_options).indexOf("전체 보기"), false)

        fetchDataFromFirestore("전체 보기")

        val buttonCreate = findViewById<Button>(R.id.buttonCreate)

        buttonCreate.setOnClickListener{
            val intent = Intent(this@FirestoreActivity, CreateSaleActivity::class.java)
            startActivity(intent)
        }

        val buttonViewMessages = findViewById<Button>(R.id.buttonViewMessages)

        buttonViewMessages.setOnClickListener {
            val intent = Intent(this, ViewMessages::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchDataFromFirestore(filter: String) {
        val db = Firebase.firestore
        val itemsCollection = db.collection("items")

        val query = when (filter) {
            "판매 중" -> itemsCollection.whereEqualTo("status", true)
            "판매 완료" -> itemsCollection.whereEqualTo("status", false)
            else -> itemsCollection
        }

        query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val itemList = mutableListOf<Item>()

                for (document in snapshot.documents) {
                    val documentId = document.id
                    val name = document.getString("name")
                    val price = document.getLong("price")?.toInt()
                    val status = document.getBoolean("status")
                    val description = document.getString("description")
                    val sellerEmail = document.getString("sellerEmail")
                    val userId = document.getString("userId") ?: ""

                    if (name != null && price != null && status != null) {
                        val item = Item(title = name, content = "", documentId = documentId,
                            name = name, price = price, status = status, sellerEmail = sellerEmail ?:"",
                            description = description, userId = userId)
                        itemList.add(item)
                    }
                }

                itemAdapter.setItems(itemList)
                progressBar.visibility = ProgressBar.GONE
                textSnapshotListener.text = "Data received successfully."
            } else {
                textSnapshotListener.text = "No data available."
            }
        }
    }
}

data class Item(val title: String, val content: String, val documentId: String, val name: String,
                val price: Int, val status: Boolean, val sellerEmail: String,
                val userId: String, val description: String?) : Serializable

class ItemAdapter(private val userId: String) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private var itemList: List<Item> = emptyList()

    fun setItems(items: List<Item>) {
        itemList = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.textName.text = currentItem.name
        holder.textStatus.text = if (currentItem.status) "판매 중" else "판매 완료"
        holder.textPrice.text = currentItem.price.toString()
        holder.itemView.setOnClickListener {
            val intent = if(Firebase.auth.currentUser?.uid == currentItem.userId) {
                Intent(holder.itemView.context, EditSaleActivity::class.java)
            } else {
                Intent(holder.itemView.context, ItemDetailActivity::class.java)
            }
            intent.putExtra("item", currentItem)
            intent.putExtra("ITEM_ID", currentItem.documentId)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.textName)
        val textStatus: TextView = itemView.findViewById(R.id.textStatus)
        val textPrice: TextView = itemView.findViewById(R.id.textPrice)
    }
}
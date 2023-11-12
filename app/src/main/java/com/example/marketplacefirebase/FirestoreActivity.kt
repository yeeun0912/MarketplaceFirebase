package com.example.marketplacefirebase

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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

        itemAdapter = ItemAdapter()
        recyclerView.adapter = itemAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchDataFromFirestore()
    }

    private fun fetchDataFromFirestore() {
        val db = Firebase.firestore
        val itemsCollection = db.collection("items")

        itemsCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                // Error handling
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val itemList = mutableListOf<Item>()

                for (document in snapshot.documents) {
                    val documentId = document.id
                    val name = document.getString("name")
                    val price = document.getLong("price")?.toInt()
                    val status = document.getBoolean("status")

                    if (name != null && price != null && status != null) {
                        val item = Item(documentId, name, price, status)
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

data class Item(val documentId: String, val name: String, val price: Int, val status: Boolean)

class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

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
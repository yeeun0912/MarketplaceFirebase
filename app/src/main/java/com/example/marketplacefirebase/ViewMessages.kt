package com.example.marketplacefirebase

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable

class ViewMessages : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var buttonBack : Button
    private var firestore : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewmessages)

        firestore = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerViewMessages)

        messageAdapter = MessageAdapter(Firebase.auth.currentUser?.uid ?: "")
        recyclerView.adapter = messageAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        buttonBack = findViewById(R.id.buttonBack)

        buttonBack.setOnClickListener {
            val intent = Intent(this, FirestoreActivity::class.java)
            startActivity(intent)
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    inner class MessageAdapter(private val userId: String) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
        private var messageList: ArrayList<Message> = arrayListOf()

        init {
            firestore?.collection("users")?.addSnapshotListener{ querySnapshot, firebaseFirestoreException ->
                messageList.clear()

                if (querySnapshot != null) {
                    for(snapshot in querySnapshot.documents) {
                        var item = snapshot.toObject(Message::class.java)
                        messageList.add(item!!)
                    }
                }
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.message, parent, false)
            return MessageViewHolder(view)
        }

        override fun getItemCount(): Int {
            return messageList.size
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            val currentUser = messageList[position]

            if(Firebase.auth.currentUser?.email == currentUser.sellerEmail){
                holder.textMessage.text = currentUser.message
            } else {
                holder.textMessage.text = "확인하지 못하는 메세지"
            }
        }

        inner class MessageViewHolder(messageView: View) : RecyclerView.ViewHolder(messageView) {
            val textMessage: TextView = messageView.findViewById(R.id.textMessage)
        }
    }
}
data class Message(var currentUserEmail: String? = null, var sellerEmail: String? = null, var message: String? = null) : Serializable
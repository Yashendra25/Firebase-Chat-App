package com.yashendra.firebasechatapp.adapter

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.yashendra.firebasechatapp.ChatActivity
import com.yashendra.firebasechatapp.Model.ChatMessageModel
import com.yashendra.firebasechatapp.R
import com.yashendra.firebasechatapp.utills.AndroidUtill
import com.yashendra.firebasechatapp.utills.FirebaseUtill

class ChatRecyclerAdpater(options: FirestoreRecyclerOptions<ChatMessageModel>) :
    FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdpater.ChatModelViewHolder>(options) {

    inner class ChatModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderlayout=itemView.findViewById<LinearLayout>(R.id.sender_layout)
        val receiverlayout=itemView.findViewById<LinearLayout>(R.id.receiver_layout)
        val sendermessage=itemView.findViewById<TextView>(R.id.sender_message)
        val receivermessage=itemView.findViewById<TextView>(R.id.receiver_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatModelViewHolder {
        val view = View.inflate(parent.context, R.layout.chat_message_recycler_row, null)
        return ChatModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatModelViewHolder, position: Int, model: ChatMessageModel) {
            if (model.senderId.equals(FirebaseUtill().currentUserId())) {
                holder.senderlayout.visibility = View.GONE
                holder.receiverlayout.visibility = View.VISIBLE
                holder.receivermessage.text = model.message
            } else {
                holder.senderlayout.visibility = View.VISIBLE
                holder.receiverlayout.visibility = View.GONE
                holder.sendermessage.text = model.message
            }
    }

}
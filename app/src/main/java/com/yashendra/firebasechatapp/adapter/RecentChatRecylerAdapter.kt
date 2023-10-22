package com.yashendra.firebasechatapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.yashendra.firebasechatapp.ChatActivity
import com.yashendra.firebasechatapp.Model.ChatRoomModel
import com.yashendra.firebasechatapp.Model.UserModel
import com.yashendra.firebasechatapp.R
import com.yashendra.firebasechatapp.utills.AndroidUtill
import com.yashendra.firebasechatapp.utills.FirebaseUtill
import java.util.*

class RecentChatRecylerAdapter(options: FirestoreRecyclerOptions<ChatRoomModel>, private val context: Context) :
    FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecylerAdapter.ChatRoomModelViewHolder>(options) {

    inner class ChatRoomModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user_name = itemView.findViewById(R.id.tv_user_name) as TextView
        val lastmessage = itemView.findViewById(R.id.lastmessage) as TextView
        val imageView = itemView.findViewById(R.id.profile_pic_image_view) as ImageView
        val time = itemView.findViewById(R.id.time) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomModelViewHolder {
        val view = View.inflate(parent.context, R.layout.recent_chat_recycler_row, null)
        return ChatRoomModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatRoomModelViewHolder, position: Int, model: ChatRoomModel) {
            FirebaseUtill().getOtherUserFromChatroom(model.UserIds).get().addOnCompleteListener {
                if (it.isSuccessful){

                    val lastmessagesendbyme:Boolean = model.lastMessageSenderId.equals(FirebaseUtill().currentUserId())
                    val user = it.result!!.toObject(UserModel::class.java)

                    if (user != null) {
                        FirebaseUtill().getotherprofileRefrence(user.uid).downloadUrl.addOnSuccessListener {
                            AndroidUtill().setimageinview(context ,it,holder.imageView)
                        }
                    }
                    holder.user_name.text = user!!.username
                    if (lastmessagesendbyme){
                        holder.lastmessage.text = "You: ${model.lastMessage}"
                    }
                    else
                    {
                        holder.lastmessage.text = model.lastMessage
                    }

                    // Convert the string timestamp to a Long value and then to a Date object.
                    val timestampString = model.lastmessagetimestamp
                    val timestampLong = timestampString.toLong()
                    val date = Date(timestampLong)

                    // Convert the Date object to a formatted string.
                    val formattedTime =AndroidUtill().getFormattedTime(date)
                    Log.d("TAG", "onBindViewHolder: $formattedTime")
                    holder.time.text = formattedTime
                    holder.itemView.setOnClickListener {
                        val intent = Intent(holder.itemView.context, ChatActivity::class.java)
                        AndroidUtill().passUserModelAsIntent(intent,user)
                        holder.itemView.context.startActivity(intent)
                    }
                }
            }
    }

}
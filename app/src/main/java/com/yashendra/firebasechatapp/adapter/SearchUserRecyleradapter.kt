package com.yashendra.firebasechatapp.adapter

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.yashendra.firebasechatapp.ChatActivity
import com.yashendra.firebasechatapp.Model.UserModel
import com.yashendra.firebasechatapp.R
import com.yashendra.firebasechatapp.utills.AndroidUtill
import com.yashendra.firebasechatapp.utills.FirebaseUtill

class SearchUserRecyclerAdapter(options: FirestoreRecyclerOptions<UserModel>, private val context: Context) :
    FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder>(options) {

    inner class UserModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val user_name = itemView.findViewById(R.id.tv_user_name) as TextView
        val user_phone = itemView.findViewById(R.id.tv_user_phone) as TextView
        val imageView = itemView.findViewById(R.id.profile_pic_image_view) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserModelViewHolder {
        val view = View.inflate(parent.context, R.layout.search_user_recylerview_item, null)
        return UserModelViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserModelViewHolder, position: Int, model: UserModel) {
        // Check the view holder's adapter position before setting the data.
        if (holder.adapterPosition != position) {
            return
        }

        holder.user_name.text = model.username
        holder.user_phone.text = model.mobilenumber
        // Compare UIDs as strings
        if (model.uid == FirebaseUtill().currentUserId()) {
            holder.user_name.text = "(Me)"
        }
        FirebaseUtill().getotherprofileRefrence(model.uid).downloadUrl.addOnSuccessListener {
            AndroidUtill().setimageinview(context ,it,holder.imageView)
        }
        holder.itemView.setOnClickListener {
            //navigate to chat activity
            val intent = Intent(holder.itemView.context, ChatActivity::class.java)
//        intent.putExtra("name", model.username)
//        intent.putExtra("uid", model.uid)
//        intent.putExtra("image", model.profileimage)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        Toast.makeText(holder.itemView.context, "Clicked on ${model.username}", Toast.LENGTH_SHORT).show()
            AndroidUtill().passUserModelAsIntent(intent, model)
            holder.itemView.context.startActivity(intent)
        }
    }

}
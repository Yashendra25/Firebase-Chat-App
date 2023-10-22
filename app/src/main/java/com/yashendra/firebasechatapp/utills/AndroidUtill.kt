package com.yashendra.firebasechatapp.utills

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yashendra.firebasechatapp.Model.UserModel
import java.text.SimpleDateFormat
import java.util.*

class AndroidUtill {

   fun passUserModelAsIntent(Intent: Intent, UserModel: UserModel?) {
       Intent.putExtra("name", UserModel!!.username)
       Intent.putExtra("phone", UserModel.mobilenumber)
//       Intent.putExtra("image", UserModel.profileimage)
       Intent.putExtra("Uid",UserModel.uid)
       Intent.putExtra("timestamp",UserModel.timestamp)
       Intent.putExtra("FCMToken",UserModel.FCMToken)
   }
    fun getUserModelAsIntenet(Intent: Intent): UserModel {
        val name = Intent.getStringExtra("name")
        val phone = Intent.getStringExtra("phone")
//        val image = Intent.getStringExtra("image")
        val Uid = Intent.getStringExtra("Uid")
        val timestamp = Intent.getStringExtra("timestamp")
        val FCMToken = Intent.getStringExtra("FCMToken")
        return UserModel(Uid!!,name!!,phone!!,timestamp!!,FCMToken!!)
    }
    fun getFormattedTime(date: Date): String {
        val formatter = SimpleDateFormat("HH:mm")
        return formatter.format(date)
    }
    fun setimageinview(Context: Context, imageuri: Uri, imageview: android.widget.ImageView) {
        Glide.with(Context).load(imageuri).apply(RequestOptions.circleCropTransform()).into(imageview)
    }
}
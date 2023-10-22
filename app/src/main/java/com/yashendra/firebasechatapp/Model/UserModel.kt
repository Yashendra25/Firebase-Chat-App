package com.yashendra.firebasechatapp.Model

data class UserModel(
    val uid:String="",
    var username:String="",
    val mobilenumber:String="",
    val timestamp:String="",
    val FCMToken:String="",
)

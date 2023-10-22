package com.yashendra.firebasechatapp

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.yashendra.firebasechatapp.utills.FirebaseUtill

class MainActivity : AppCompatActivity() {
    lateinit var BottomNavigationView: BottomNavigationView
    lateinit var ChatFragment: ChatFragment
    lateinit var ProfileFragment: ProfileFragment
    lateinit var SearchButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BottomNavigationView = findViewById(R.id.bottom_nav)
        ChatFragment = ChatFragment()
        ProfileFragment = ProfileFragment()
        SearchButton = findViewById(R.id.btn_search)

        BottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_chat -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ChatFragment).commit()
                }
                R.id.menu_user_profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ProfileFragment).commit()
                }
            }
            true
        }
        BottomNavigationView.selectedItemId = R.id.menu_chat

        SearchButton.setOnClickListener {
           val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)

        }
        getFCMToken()

    }
    fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result
                FirebaseUtill().currentUserDeatils().update("FCMToken", token)
                println("Token: $token")
            }
        }
    }

}
package com.yashendra.firebasechatapp

import android.content.ClipData.newIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.yashendra.firebasechatapp.Model.UserModel
import com.yashendra.firebasechatapp.utills.AndroidUtill
import com.yashendra.firebasechatapp.utills.FirebaseUtill
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (intent.extras != null && FirebaseUtill().isloggedin()) {
            val userid = intent.getStringExtra("userid")

            if (userid != null) {
                FirebaseUtill().allUserCollectionRefrence().document(userid).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val mainintent = Intent(this, MainActivity::class.java)
                        mainintent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                        startActivity(mainintent)
                        val user = task.result.toObject(UserModel::class.java)
                        if (user != null) {
                            val chatIntent = Intent(this, ChatActivity::class.java)
                            AndroidUtill().passUserModelAsIntent(chatIntent, user)
                            startActivity(chatIntent)
                            finish()
                        }
                    }
                }
            }
        } else {
            val intent: Intent = if (FirebaseUtill().isloggedin()) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, LoginPhoneNumberActivity::class.java)
            }
            Handler().postDelayed({
                startActivity(intent)
                finish()
            }, 3000)
        }
    }
}

package com.yashendra.firebasechatapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import com.yashendra.firebasechatapp.Model.UserModel
import com.yashendra.firebasechatapp.databinding.ActivityLoginUsernameBinding
import com.yashendra.firebasechatapp.utills.FirebaseUtill
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Instant

class LoginUsernameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginUsernameBinding
    private lateinit var latemein:Button
    private lateinit var progressBar:ProgressBar
    private lateinit var username:EditText
    lateinit var phonenumber:String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginUsernameBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        latemein=binding.letMeIn
        progressBar=binding.progressBar
        username=binding.etUsername
        setInProgress(false)
        phonenumber=intent.getStringExtra("mobile").toString()

        latemein.setOnClickListener {
            if (username.text.toString().isEmpty()){
                username.error="Please enter username"
                return@setOnClickListener
            }
            saveUserToFirestore()
        }




    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveUserToFirestore() {
        setInProgress(true)

        GlobalScope.launch(Dispatchers.IO) {
            val user = UserModel(FirebaseUtill().currentUserId(), username.text.toString(), phonenumber, Instant.now().toEpochMilli().toString())
            FirebaseUtill().savetodatabase(user)

            // Update UI on the main thread
            launch(Dispatchers.Main) {
                setInProgress(false)

                // Start the new activity here
                val intent = Intent(this@LoginUsernameActivity, MainActivity::class.java)
                startActivity(intent)

                // Finish the current activity if needed
                finish()
            }
        }
    }

    private fun setInProgress(inProgress:Boolean){
        if (inProgress){
            progressBar.visibility= ProgressBar.VISIBLE
            latemein.visibility= Button.INVISIBLE
        }else{
            progressBar.visibility= ProgressBar.INVISIBLE
           latemein.visibility= Button.VISIBLE
        }

    }
}
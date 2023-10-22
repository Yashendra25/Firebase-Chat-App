package com.yashendra.firebasechatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import com.yashendra.firebasechatapp.databinding.ActivityLoginPhoneNumberBinding

class LoginPhoneNumberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPhoneNumberBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginPhoneNumberBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        binding.loginProgressBar.visibility=GONE


        binding.countrycodepicker.registerCarrierNumberEditText(binding.loginEtMobile)
        binding.loginBtnSendOtp.setOnClickListener {
            if (!binding.countrycodepicker.isValidFullNumber){
                binding.loginEtMobile.error="Please enter valid mobile number"
                return@setOnClickListener
            }
            binding.loginProgressBar.visibility= View.VISIBLE
            val intent=Intent(this,LoginOtpActivity::class.java)
            intent.putExtra("mobile",binding.countrycodepicker.fullNumberWithPlus)
            startActivity(intent)
            binding.loginProgressBar.visibility=GONE
        }
    }
}
package com.yashendra.firebasechatapp

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.*
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*

import com.yashendra.firebasechatapp.databinding.ActivityLoginOtpBinding
import java.util.concurrent.TimeUnit

class LoginOtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginOtpBinding
    private lateinit var nextbtn:Button
    private lateinit var progressBar:ProgressBar;
    private lateinit var resendotptv:TextView;
    private lateinit var otpinput:EditText;
    private lateinit var firebaseAuth: FirebaseAuth
    var verificationcode:String?=null
    var resendToken: PhoneAuthProvider.ForceResendingToken?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginOtpBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        firebaseAuth =FirebaseAuth.getInstance()

        Log.d("firebase auth", "onCreate: $firebaseAuth")
        otpinput=binding.loginOtpEt
        nextbtn=binding.loginOtpNextBtn
        progressBar=binding.loginOtpProgressBar
        resendotptv=binding.loginOtpResendTv


        val mobile=intent.getStringExtra("mobile").toString()
        sendOtp(mobile,false)

        nextbtn.setOnClickListener {
            if (otpinput.text.toString().isEmpty()){
                Toast.makeText(this, "Please enter otp", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            verificationcode?.let {
                val credential=PhoneAuthProvider.getCredential(it,otpinput.text.toString())
                signInWithPhoneAuthCredential(credential)
                setInProgress(true)
            }

        }

    }

    private fun sendOtp(mobile: String, isResend: Boolean) {
        resendTimer()
        setInProgress(true)
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(mobile) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // This method will be called when verification is completed automatically
                    // (e.g., when the device receives an SMS with the verification code).
                    // You can use the credential to sign in the user.
                    signInWithPhoneAuthCredential(credential)
                    setInProgress(false)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    // This method will be called if verification fails.
                    // Handle the error here.
                    Toast.makeText(this@LoginOtpActivity, "otp verification failed", Toast.LENGTH_SHORT).show()
                    setInProgress(false)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // This method will be called when the verification code is successfully sent.
                    // You can use the verificationId and token to verify the code later.
                    verificationcode = verificationId
                    resendToken = token
                    Toast.makeText(this@LoginOtpActivity, "Otp Sent", Toast.LENGTH_SHORT).show()
                    setInProgress(false)
                }

                override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                    // This method will be called when the auto-retrieval timeout is reached.
                    // You can handle this event if needed.
                }
            }) // OnVerificationStateChangedCallbacks

        if (isResend) {
            resendToken?.let {
                options
                    .setForceResendingToken(it)
                    .build()
            }?.let { PhoneAuthProvider.verifyPhoneNumber(it) }
        } else {
            PhoneAuthProvider.verifyPhoneNumber(options.build())
        }
    }

    private fun resendTimer() {
        resendotptv.isEnabled=false
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                resendotptv.text = "Resend OTP in: " + millisUntilFinished / 1000
            }

            override fun onFinish() {
                resendotptv.text = "Resend OTP"
                resendotptv.isEnabled=true
            }
        }.start()
        resendotptv.setOnClickListener {
            val mobile=intent.getStringExtra("mobile").toString()
            sendOtp(mobile,true)
        }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        setInProgress(true)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = task.result?.user

                    val intent=Intent(this,LoginUsernameActivity::class.java)
                    intent.putExtra("mobile",user?.phoneNumber)
                    startActivity(intent)
                    setInProgress(false)
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Otp verification failed", Toast.LENGTH_SHORT).show()
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }

    private fun setInProgress(inProgress:Boolean){
        if (inProgress){
            progressBar.visibility= ProgressBar.VISIBLE
            nextbtn.visibility=Button.INVISIBLE
        }else{
            progressBar.visibility= ProgressBar.INVISIBLE
            nextbtn.visibility=Button.VISIBLE
        }

    }

}
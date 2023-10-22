package com.yashendra.firebasechatapp

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.messaging.FirebaseMessaging
import com.yashendra.firebasechatapp.Model.UserModel
import com.yashendra.firebasechatapp.utills.AndroidUtill
import com.yashendra.firebasechatapp.utills.FirebaseUtill

class ProfileFragment : Fragment() {
    lateinit var profilepic:ImageView
    lateinit var username:EditText
    lateinit var userphone:EditText
    lateinit var logoutbtn:TextView
    lateinit var updatebtn:Button
    lateinit var progressBar: ProgressBar
    lateinit var userModel: UserModel
    lateinit var imagepicklauncher:ActivityResultLauncher<Intent>
    var imageuri: Uri? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagepicklauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val selectedImageUri = result.data?.data
                    imageuri = selectedImageUri!!
                    AndroidUtill().setimageinview(requireContext(), imageuri!!, profilepic)
                }
            }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_profile, container, false)
        profilepic=view.findViewById(R.id.iv_profile)
        username=view.findViewById(R.id.et_username)
        userphone=view.findViewById(R.id.et_phone)
        logoutbtn=view.findViewById(R.id.tv_logout)
        updatebtn=view.findViewById(R.id.btn_update_profile)
        progressBar=view.findViewById(R.id.progress_bar)

        getUserdara()
        logoutbtn.setOnClickListener {

            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(
                requireActivity()
            ) { task ->
                if (!task.isSuccessful) {
                    println("Token not deleted")
                }
                println("Token deleted")
            }


            FirebaseUtill().logout()
            val intent= Intent(context,SplashActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        updatebtn.setOnClickListener {
            updatebtnclick()
        }
        profilepic.setOnClickListener {
//            val intent=Intent(Intent.ACTION_PICK)
//            intent.type="image/*"
//            imagepicklauncher.launch(intent)
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(512,512)
                .createIntent {
                    imagepicklauncher.launch(it)
                }
        }
        return view
    }

    private fun updatebtnclick() {
        val newUsername=username.text.toString()
        if (newUsername.isEmpty() && newUsername.length<3){
            username.error="Please enter username of length greater than 3"
            return
        }
        userModel.username=newUsername

       if(imageuri!=null){
           FirebaseUtill().getcurrentprofileRefrence().putFile(imageuri!!).addOnCompleteListener {
               if (it.isSuccessful){

                   updateToFirebase()
               }
               else{
                   updateToFirebase()
               }
           }
       }
//        updateToFirebase()
    }

    private fun updateToFirebase() {
        setInProgress(true)
        FirebaseUtill().currentUserDeatils().set(userModel).addOnCompleteListener {
            setInProgress(false)
            if (it.isSuccessful){
                Toast.makeText(context,"Profile Updated",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(context,"Error in updating profile",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserdara() {
        setInProgress(true)
        FirebaseUtill().getcurrentprofileRefrence().downloadUrl.addOnSuccessListener {
            AndroidUtill().setimageinview(requireContext(),it,profilepic)
        }

        FirebaseUtill().currentUserDeatils().get().addOnSuccessListener {
            if(it.exists()){
                userModel=it.toObject(UserModel::class.java)!!
                username.setText(userModel.username)
                userphone.setText(userModel.mobilenumber)
                setInProgress(false)
            }
        }
    }
    private fun setInProgress(inProgress:Boolean){
        if (inProgress){
            progressBar.visibility= ProgressBar.VISIBLE
            updatebtn.visibility=Button.INVISIBLE
        }else{
            progressBar.visibility= ProgressBar.INVISIBLE
           updatebtn.visibility=Button.VISIBLE
        }

    }

}
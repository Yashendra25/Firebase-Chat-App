package com.yashendra.firebasechatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import okhttp3.MediaType.Companion.toMediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.yashendra.firebasechatapp.Model.ChatMessageModel
import com.yashendra.firebasechatapp.Model.ChatRoomModel
import com.yashendra.firebasechatapp.Model.UserModel
import com.yashendra.firebasechatapp.adapter.ChatRecyclerAdpater
import com.yashendra.firebasechatapp.adapter.SearchUserRecyclerAdapter
import com.yashendra.firebasechatapp.databinding.ActivityChatBinding
import com.yashendra.firebasechatapp.utills.AndroidUtill
import com.yashendra.firebasechatapp.utills.FirebaseUtill
import okhttp3.Call
import okhttp3.Callback
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.security.Timestamp

class ChatActivity : AppCompatActivity() {
    lateinit var otherUser: UserModel
    lateinit var binding: ActivityChatBinding
    lateinit var MesssageInput: EditText
    lateinit var SendButton: ImageButton
    lateinit var backbutton: ImageButton
    lateinit var chatRecylerView: RecyclerView
    lateinit var username:TextView
    lateinit var chatRoomId:String
    lateinit var chatRecyclerAdpater: ChatRecyclerAdpater
    lateinit var profilepic:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        //get Usermodel from intent
        otherUser = AndroidUtill().getUserModelAsIntenet(intent)

        chatRoomId = FirebaseUtill().getChatroomId(FirebaseUtill().currentUserId(), otherUser.uid)

        MesssageInput = binding.messageEditText
        SendButton = binding.sendMessageBtn
        backbutton = binding.btnBack
        chatRecylerView = binding.chatRecyclerView
        username = binding.userName
        profilepic = findViewById(R.id.profile_pic_image_view)
        FirebaseUtill().getotherprofileRefrence(otherUser.uid).downloadUrl.addOnSuccessListener {
            AndroidUtill().setimageinview(this ,it,profilepic)
        }

        backbutton.setOnClickListener {
            onBackPressed()
        }
        SendButton.setOnClickListener {
            val message = MesssageInput.text.toString()
            if (message.isEmpty()) {
                MesssageInput.error = "Please write something to send"
                return@setOnClickListener
            } else {
                SendMessageToUser(message)

            }
        }
        username.text = otherUser.username

        getOrcreateChatRoomModel()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        val Query = FirebaseUtill().getMessageRefrence(chatRoomId).orderBy("timestamp",Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<ChatMessageModel>().setQuery(Query, ChatMessageModel::class.java).build()
        Log.d("chatactivty", "setRecyclerView: ${options.snapshots}")

        Log.d("chatactivty", "query result: ${Query.get()}")
        chatRecyclerAdpater= ChatRecyclerAdpater(options)
       chatRecylerView.adapter = chatRecyclerAdpater
        val manager=LinearLayoutManager(this)
        manager.reverseLayout=true
        chatRecylerView.layoutManager= manager
        chatRecyclerAdpater.startListening()

        chatRecyclerAdpater.registerAdapterDataObserver(object :RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                chatRecylerView.smoothScrollToPosition(0)
            }
        })
    }

    private fun SendMessageToUser(message: String) {
        val chatMessage = ChatMessageModel(
            message,
            FirebaseUtill().currentUserId(),
            System.currentTimeMillis().toString()
        )
        FirebaseUtill().getChatRoomReference(chatRoomId).update("lastMessageSenderId",FirebaseUtill().currentUserId())
        FirebaseUtill().getChatRoomReference(chatRoomId).update("lastmessagetimestamp",System.currentTimeMillis().toString())
        FirebaseUtill().getChatRoomReference(chatRoomId).update("lastMessage",message)
        FirebaseUtill().getChatRoomReference(chatRoomId).collection("messages").add(chatMessage).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(this,"Message Sent",Toast.LENGTH_SHORT).show()
                MesssageInput.setText("")
                SendNotification(message)
            }
            else{
                //message not sent
                Toast.makeText(this,"Message not Sent",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun SendNotification(message: String) {
        //current username ,message,other user token
        FirebaseUtill().currentUserDeatils().get().addOnSuccessListener {
            val currentuser = it.toObject(UserModel::class.java)
            try {
                val jsonObject = JSONObject()
                val jsonObjectNotification = JSONObject()
                jsonObjectNotification.put("title", currentuser!!.username)
                jsonObjectNotification.put("body", message)

                val dataobject = JSONObject()
                dataobject.put("userid", currentuser.uid)

                jsonObject.put("notification", jsonObjectNotification)
                jsonObject.put("data", dataobject)
                jsonObject.put("to", otherUser.FCMToken)
                Log.d("chatactivity", "SendNotification: $jsonObject")
                callApi(jsonObject)
            } catch (e: Exception) {
                Log.d("chatactivity", "SendNotification: ${e.message}")
            }
        }
    }
    private fun callApi(jsonObject: JSONObject) {
        val JSON = "application/json; charset=utf-8".toMediaType()
        val client = okhttp3.OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"
        val requestBody = RequestBody.create(JSON, jsonObject.toString())
        val request = okhttp3.Request.Builder()
            .url(url)
            .post(requestBody)
            .header("Authorization", "Bearer " + "AAAAB9vkGgk:APA91bENKd5q-jsE_wTH1pL-jySCsHhzQGI8YPAZfq2rZFH_q_0lDcQv83TbPlYTjFxabehojBpR9bZlOp6LDesZOYRRQBipjFZgufUSIDr5PdpkVw3F9zJ3YrLdCGTwg0n8C8NiVmSt")
            .build()
        client.newCall(request).enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("chatactivity", "onFailure: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d("chatactivity", "onResponse: ${response.body!!.string()}")
                }
            }
        )
    }


    private fun getOrcreateChatRoomModel() {
            FirebaseUtill().getChatRoomReference(chatRoomId).get().addOnCompleteListener {
                if (it.isSuccessful){
                    val chatRoomModel=it.getResult().toObject(ChatRoomModel::class.java)
                    if(chatRoomModel==null){
                        //first time chat
                        val newChatRoomModel=ChatRoomModel(
                            chatroomid = chatRoomId,
                            UserIds = listOf(FirebaseUtill().currentUserId(),otherUser.uid),
                            System.currentTimeMillis().toString(),
                            lastMessageSenderId = ""
                        )
                        FirebaseUtill().getChatRoomReference(chatRoomId).set(newChatRoomModel)

                    }
                    else{
//                        setUpChatRecylerView()
                    }
                }
            }
    }
}
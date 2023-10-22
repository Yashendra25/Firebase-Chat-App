package com.yashendra.firebasechatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.database.Query
import com.google.firebase.firestore.FirebaseFirestore
import com.yashendra.firebasechatapp.Model.UserModel
import com.yashendra.firebasechatapp.adapter.SearchUserRecyclerAdapter

class SearchActivity : AppCompatActivity() {
    lateinit var search_edit_text:EditText
    lateinit var search_button:ImageButton
    lateinit var search_list: RecyclerView
    lateinit var backbutton:ImageButton
    lateinit var adpter:SearchUserRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        search_edit_text = findViewById(R.id.et_search)
        search_button = findViewById(R.id.searchbtn)
        search_list = findViewById(R.id.searchuserRecylerview)
        backbutton = findViewById(R.id.btn_back)

        search_edit_text.requestFocus()

        backbutton.setOnClickListener {
            onBackPressed()
        }

        search_button.setOnClickListener {
            val searchtext = search_edit_text.text.toString()
            if(searchtext == ""){
                search_edit_text.error = "Please write something to search"
                return@setOnClickListener
            }
            else{
                setUpSearchRecylerView(searchtext)

            }
        }

    }

    private fun setUpSearchRecylerView(searchtext: String) {
//       val  Query = FirebaseFirestore.getInstance().collection("users").
//        whereGreaterThanOrEqualTo("username",searchtext).whereLessThanOrEqualTo("username",searchtext)
        val Query = FirebaseFirestore.getInstance().collection("users")
            .orderBy("username")
            .startAt(searchtext)
            .endAt(searchtext + "\uf8ff")
        val options = FirestoreRecyclerOptions.Builder<UserModel>().setQuery(Query, UserModel::class.java).build()
        Log.d("TAG", "setUpSearchRecylerView: ${options.snapshots}")
        adpter= SearchUserRecyclerAdapter(options, context = this)
        search_list.adapter = adpter
        search_list.layoutManager= LinearLayoutManager(this)
        adpter.startListening()
    }
    override fun onStart() {
        super.onStart()
//        if (::adpter.isInitialized) { // Check if adpter is initialized
//            adpter.startListening()
//        }
    }

    override fun onStop() {
        super.onStop()
//        if (::adpter.isInitialized) { // Check if adpter is initialized
//            adpter.stopListening()
//        }
    }


}
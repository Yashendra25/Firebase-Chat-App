package com.yashendra.firebasechatapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import com.yashendra.firebasechatapp.Model.ChatRoomModel
import com.yashendra.firebasechatapp.adapter.RecentChatRecylerAdapter
import com.yashendra.firebasechatapp.utills.FirebaseUtill

class ChatFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var adpter: RecentChatRecylerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView=view.findViewById(R.id.chat_recycler_view)

        setUprecylerView()
        return view
    }

    private fun setUprecylerView() {
        val Query = FirebaseUtill().allchatRoomRefrence()
            .whereArrayContains("userIds",FirebaseUtill().currentUserId()).orderBy("lastmessagetimestamp",
                Query.Direction.DESCENDING)
        val options = FirestoreRecyclerOptions.Builder<ChatRoomModel>().setQuery(Query, ChatRoomModel::class.java).build()
        Log.d("TAG", "setUpchatRecylerView: ${options.snapshots}")
        adpter= RecentChatRecylerAdapter(options,requireContext())
       recyclerView.adapter = adpter
        recyclerView.layoutManager= LinearLayoutManager(context)
        adpter.startListening()
    }


    override fun onStop() {
        super.onStop()
//        if (adpter!= null)
//        {
//            adpter.stopListening()
//        }


    }

    override fun onResume() {
        super.onResume()
        if (adpter!= null)
            adpter.notifyDataSetChanged()
    }


}

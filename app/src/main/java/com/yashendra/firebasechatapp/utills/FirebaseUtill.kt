package com.yashendra.firebasechatapp.utills

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yashendra.firebasechatapp.Model.UserModel

class FirebaseUtill {
    fun isloggedin():Boolean{
        return FirebaseAuth.getInstance().currentUser!=null
    }

    fun currentUserId():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }


    fun currentUserDeatils():DocumentReference{
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId())
    }
    fun savetodatabase(UserModel: UserModel) {

        FirebaseFirestore.getInstance().collection("users").document(currentUserId()).set(UserModel)

    }
    fun getChatRoomReference(ChatRoomId:String):DocumentReference{
        return FirebaseFirestore.getInstance().collection("chatrooms").document(ChatRoomId)
    }

    fun getMessageRefrence(ChatRoomId:String):CollectionReference{
        return getChatRoomReference(ChatRoomId).collection("messages")
    }
    fun getChatroomId(Userid1:String,Userid2:String):String{
        if (Userid1.hashCode()<Userid2.hashCode()){
            return Userid1+"_"+Userid2
        }
        else{
            return Userid2+"_"+Userid1
        }
    }
    fun allUserCollectionRefrence():CollectionReference{
        return FirebaseFirestore.getInstance().collection("users")
    }
    fun  allchatRoomRefrence():CollectionReference{
        return FirebaseFirestore.getInstance().collection("chatrooms")
    }

    fun getOtherUserFromChatroom(uids:List<String>):DocumentReference{
        if (uids[0]==currentUserId()){
            return allUserCollectionRefrence().document(uids[1])
        }
        else{
            return allUserCollectionRefrence().document(uids[0])
        }
    }

    fun logout(){
        FirebaseAuth.getInstance().signOut()
    }
    fun getcurrentprofileRefrence(): StorageReference {
        return FirebaseStorage.getInstance().reference.child("profileimages").child(currentUserId())
    }

    fun getotherprofileRefrence(otheruserId:String): StorageReference {
        return FirebaseStorage.getInstance().reference.child("profileimages").child(otheruserId)
    }
}
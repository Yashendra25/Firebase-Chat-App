package com.yashendra.firebasechatapp.Model

data class ChatRoomModel(
    var chatroomid: String = "",
    var UserIds: List<String> = emptyList(),
    var lastmessagetimestamp: String = "",
    var lastMessageSenderId: String = "",
    var lastMessage: String = ""
) {
    // Add a default no-argument constructor
    constructor() : this("", emptyList(), "", "")
}

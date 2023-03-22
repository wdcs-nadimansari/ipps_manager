package com.webclues.IPPSManager.Modelclass

class ChatResponseItem {

    var ChatProfile: Int? = null
    var Chatonoff: Int? = null
    var UserName: String? = null
    var Description: String? = null
    var ChatTime: String? = null


    constructor(
        ChatProfile: Int,
        Chatonoff: Int,
        UserName: String,
        Description: String,
        ChatTime: String
    ) {
        this.ChatProfile = ChatProfile
        this.Chatonoff = Chatonoff
        this.UserName = UserName
        this.Description = Description
        this.ChatTime = ChatTime

    }
}
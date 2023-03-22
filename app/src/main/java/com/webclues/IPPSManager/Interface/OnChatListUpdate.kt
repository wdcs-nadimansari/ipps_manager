package com.webclues.IPPSManager.Interface

import com.webclues.IPPSManager.Modelclass.GroupListModel


interface OnChatListUpdate {
    fun UpdateList(chatList : ArrayList<GroupListModel>)
}
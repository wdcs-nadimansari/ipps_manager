package com.webclues.IPPSManager.Modelclass

import android.graphics.Bitmap
import androidx.core.app.NotificationCompat

class NotificationModel{
    var id = 0
    var message: String? = ""
    var title:String? = ""
    var type:String? = ""
    var notificationCount = 0
    val job_id: Int = 0
    val content: String? = ""
    val date_time: Long? = 0
    var chatListModel: GroupListModel? = null
    var imageBitmap: Bitmap? = null
    var inboxStyle = NotificationCompat.InboxStyle()
}
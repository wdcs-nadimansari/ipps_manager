package com.webclues.IPPSManager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.webclues.IPPSManager.Modelclass.NotificationListModel
import com.webclues.IPPSManager.R
import kotlinx.android.synthetic.main.adp_notifications.view.*

class NotificationAdapter(
    private val context: Context,
    private val notificationsarraylist: ArrayList<NotificationListModel>
) : RecyclerView.Adapter<NotificationAdapter.Myviewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Myviewholder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.adp_notifications, parent, false)
        return Myviewholder(view)
    }

    override fun getItemCount(): Int {
        return notificationsarraylist.size
    }

    override fun onBindViewHolder(holder: Myviewholder, position: Int) {
        val notificationResponseItem = notificationsarraylist.get(position)
//        holder.txtTime.setText(notificationResponseItem.NotificationTime)
//        holder.txtDescription.setText(notificationResponseItem.NotificationDesc)

        if (position == 0 || position == 1) {
            holder.txtDescription.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.fontColorBlack
                )
            )
        } else {
            holder.txtDescription.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.fontColorBlack50
                )
            )
        }
    }

    class Myviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var txtTime = itemView.txtTime
        var txtDescription = itemView.txtDescription
    }
}
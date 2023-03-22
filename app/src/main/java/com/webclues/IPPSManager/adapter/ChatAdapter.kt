package com.webclues.IPPSManager.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.webclues.IPPSManager.Modelclass.GroupListModel
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.ChatActivity
import com.webclues.IPPSManager.utility.Utility

class ChatAdapter(
    private val context: Context,
    private val chatarraylist: ArrayList<GroupListModel>
) : RecyclerView.Adapter<ChatAdapter.Myviewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.Myviewholder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_chats, parent, false)
        return Myviewholder(view)

    }

    override fun getItemCount(): Int {

        return chatarraylist.size
    }

    override fun onBindViewHolder(holder: ChatAdapter.Myviewholder, position: Int) {

        val chatresponseitem: GroupListModel = chatarraylist.get(position)
        /*  if(chatresponseitem.profile_pic.isNotBlank()){
              Picasso.get().load(chatresponseitem.profile_pic).placeholder(R.drawable.ic_placeholder_profile).into(holder.ivChatprofile)
          }*/
   /*     if (chatresponseitem.has_message) {
            holder.ivChatOnoff.visibility = View.VISIBLE
        } else {
            holder.ivChatOnoff.visibility = View.GONE
        }*/

        holder.txtUserName.setText(chatresponseitem.group_name)
        holder.txtMessage.setText(chatresponseitem.lastMessage)
        holder.txtTime.setText(Utility.getTime(chatresponseitem.lastUpdate))

        if (position == itemCount - 1) {
            holder.view.visibility = View.GONE
        }

/*
        if (position == 0) {
            holder.ivRead.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_sent))
        } else if (position == 2) {
            holder.ivRead.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_delivered
                )
            )
        } else {
            holder.ivRead.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_seen))
        }
*/

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, ChatActivity::class.java).putExtra("chat_group", Gson().toJson(chatresponseitem)))
        }
    }

    class Myviewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        var ivChatprofile = itemview.findViewById(R.id.ivChatprofile) as ImageView
        var ivChatOnoff = itemview.findViewById(R.id.ivChatOnline) as ImageView
        var ivRead = itemview.findViewById(R.id.ivRead) as ImageView
        var txtUserName = itemview.findViewById(R.id.txtUserName) as TextView
        var txtMessage = itemview.findViewById(R.id.txtMessage) as TextView
        var txtTime = itemview.findViewById(R.id.txtTime) as TextView
        var view = itemView.findViewById(R.id.view) as View

    }
}
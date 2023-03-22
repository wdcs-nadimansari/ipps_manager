package com.webclues.IPPSManager.adapter

import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.webclues.IPPSManager.Modelclass.ChatMessageItem
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.ImageViewActivity
import com.webclues.IPPSManager.application.mApplicationClass
import com.webclues.IPPSManager.utility.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adp_message_layout.view.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import com.webclues.IPPSManager.utility.ChatConstants.*
import com.webclues.IPPSManager.utility.CircleTransform
import java.text.SimpleDateFormat

class MessageListAdapter(
    private val mContext: Context,
    private val messageList: ArrayList<DataSnapshot>
) : RecyclerView.Adapter<MessageListAdapter.Myviewholder>() {
    private var date: Date? = null
    private var date1: Date? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageListAdapter.Myviewholder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_message_layout, parent, false)
        return Myviewholder(view)

    }

    override fun getItemCount(): Int {

        return messageList.size
    }

    override fun onBindViewHolder(holder: MessageListAdapter.Myviewholder, position: Int) {
        val model = messageList[position]
        val format2 = SimpleDateFormat("yyyyMMdd")
        val format3 = SimpleDateFormat("dd MMMM yyyy, hh:mm a")
        val format4: SimpleDateFormat
        format4 = if (DateFormat.is24HourFormat(mContext)) {
            SimpleDateFormat("HH:mm")
        } else {
            SimpleDateFormat("hh:mm a")
        }
        val c = Calendar.getInstance()
        val currentDate = c.time
        if (position != messageList.size - 1) {
            val calendar = Calendar.getInstance()
            val calendar1 = Calendar.getInstance()
            calendar.timeInMillis = messageList[position].child(messageTime).value.toString().toLong()
            calendar1.timeInMillis = messageList[position + 1].child(messageTime).value.toString().toLong()
            try {
                date = calendar.time
                date1 = calendar1.time
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val diff = getSignedDiffInDays(currentDate, date)
            if (format2.format(date1) == format2.format(date)) {
                holder.txtHeaderKey.visibility = View.GONE
            } else {
                holder.txtHeaderKey.visibility = View.VISIBLE
                if (format2.format(currentDate) == format2.format(date)) {
                    holder.txtHeaderKey.text = mContext.getString(R.string.lbl_today)/*+", "+format4.format(date)*/
                } else if (diff == 1) {
                    holder.txtHeaderKey.text = mContext.getString(R.string.lbl_yesterday)/*+" , "+format4.format(date)*/
                } else {
                    holder.txtHeaderKey.text = format3.format(date)
                }
            }
        } else if (position == messageList.size - 1) {
            try {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = messageList[position].child(messageTime).value.toString().toLong()
                date = calendar.time
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val diff = getSignedDiffInDays(currentDate, date)
            holder.txtHeaderKey.visibility = View.VISIBLE
            if (format2.format(currentDate) == format2.format(date)) {
                holder.txtHeaderKey.text =mContext.getString(R.string.lbl_today)/*+", "+format4.format(date)*/
            } else if (diff == 1) {
                holder.txtHeaderKey.text = mContext.getString(R.string.lbl_yesterday)/*+" , "+format4.format(date)*/
            } else {
                holder.txtHeaderKey.text = format3.format(date)
            }
        }

        if (model.child(messageType).value == Content.CHAT_TEXT_TYPE) {
            if (model.child(sender_id).value == mApplicationClass.getPreference()?.getString(Content.USER_ID)) {
                holder.lnSender.visibility = View.VISIBLE
                holder.lnReceiver.visibility = View.GONE
                holder.txtSenderMessage.visibility=View.VISIBLE
                holder.ivSenderImagePath.visibility = View.GONE
                holder.txtSenderName.text = model.child(sender_first_name).value.toString() +" "+model.child(sender_last_namee).value.toString()
                holder.txtSenderTime.text = Utility.getTime(model.child(messageTime).value.toString().toLong())
                holder.txtSenderMessage.text = model.child(message).value.toString()
                Picasso.get().load(model.child(sender_profile_pic).value.toString())
                    .placeholder(R.drawable.ic_placeholder_profile).transform(CircleTransform())
                    .into(holder.ivSenderImage)
            } else {
                holder.lnReceiver.visibility = View.VISIBLE
                holder.lnSender.visibility = View.GONE
                holder.txtReceiverMessage.visibility=View.VISIBLE
                holder.ivReceiverImagePath.visibility = View.GONE
                holder.txtRecieverName.text = model.child(sender_first_name).value.toString() +" "+model.child(sender_last_namee).value.toString()
                holder.txtReceiverTime.text = Utility.getTime(model.child(messageTime).value.toString().toLong())
                holder.txtReceiverMessage.text =model.child(message).value.toString()
                Picasso.get().load(model.child(sender_profile_pic).value.toString())
                    .placeholder(R.drawable.ic_placeholder_profile).transform(CircleTransform())
                    .into(holder.ivReceiverImage)
            }
        } else if (model.child(messageType).value == Content.CHAT_IMAGE_TYPE) {

            if (model.child(sender_id).value == mApplicationClass.getPreference()?.getString(Content.USER_ID)) {
                holder.lnSender.visibility = View.VISIBLE
                holder.lnReceiver.visibility = View.GONE
                holder.ivSenderImagePath.visibility = View.VISIBLE
                holder.txtSenderMessage.visibility = View.GONE
                holder.txtSenderName.text = model.child(sender_first_name).value.toString() +" "+model.child(sender_last_namee).value.toString()
                holder.txtSenderTime.text = Utility.getTime(model.child(messageTime).value.toString().toLong())
                Picasso.get().load(model.child(messageImage).value.toString())
                    .placeholder(R.drawable.ic_placeholder_job)
                    .into(holder.ivSenderImagePath)
                Picasso.get().load(model.child(sender_profile_pic).value.toString())
                    .placeholder(R.drawable.ic_placeholder_profile).transform(CircleTransform())
                    .into(holder.ivSenderImage)

                holder.ivSenderImagePath.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        nextScreen(model.child(messageImage).value.toString(), holder.txtSenderName.text.toString() )
                    }
                })

            } else {
                holder.lnReceiver.visibility = View.VISIBLE
                holder.lnSender.visibility = View.GONE
                holder.ivReceiverImagePath.visibility = View.VISIBLE
                holder.txtReceiverMessage.visibility = View.GONE
                holder.txtRecieverName.text = model.child(sender_first_name).value.toString() +" "+model.child(sender_last_namee).value.toString()
                holder.txtReceiverTime.text = Utility.getTime(model.child(messageTime).value.toString().toLong())
                Picasso.get().load(model.child(messageImage).value.toString())
                    .placeholder(R.drawable.ic_placeholder_job)
                    .into(holder.ivReceiverImagePath)
                Picasso.get().load(model.child(sender_profile_pic).value.toString())
                    .placeholder(R.drawable.ic_placeholder_profile).transform(CircleTransform())
                    .into(holder.ivReceiverImage)

                holder.ivReceiverImagePath.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        nextScreen(model.child(messageImage).value.toString(), holder.txtRecieverName.text.toString())
                    }
                })
            }
        }
    }

    fun nextScreen(url: String,name: String){
        val message =  url
        val intent = Intent(mContext, ImageViewActivity::class.java).apply {
            putExtra("imageUrl", message)
            putExtra("name", name)
        }
        mContext.startActivity(intent)
    }

    class Myviewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        var lnSender = itemview.lnSender
        var lnReceiver = itemview.lnReceiver
        var txtSenderName = itemview.txtSenderName
        var txtRecieverName = itemview.txtRecieverName
        var txtSenderTime = itemview.txtSenderTime
        var txtReceiverTime = itemview.txtReceiverTime
        var txtSenderMessage = itemview.txtSenderMessage
        var txtReceiverMessage = itemview.txtReceiverMessage
        var ivSenderImagePath = itemview.ivSenderImagePath
        var ivReceiverImagePath = itemview.ivReceiverImagepath
        var ivSenderImage = itemview.ivSenderImage
        var ivReceiverImage = itemview.ivReceiverImage
        var txtHeaderKey = itemview.txtHeaderKey


    }

    private fun getSignedDiffInDays(beginDate: Date, endDate: Date?): Int {
        val beginMS = getDateToLong(beginDate)
        val endMS = getDateToLong(endDate!!)
        val diff = (beginMS - endMS) / (24 * 60 * 60 * 1000)
//        val diff =beginDate.time-endDate.time
        return diff.toInt()

    }
    private fun getDateToLong(date: Date): Long {
        return Date.UTC(date.year, date.month, date.date, 0, 0, 0)
    }
}
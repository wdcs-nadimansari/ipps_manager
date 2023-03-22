package com.webclues.IPPSManager.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.webclues.IPPSManager.Modelclass.NotificationListModel
import com.webclues.IPPSManager.Modelclass.NotificationModel
import com.webclues.IPPSManager.Modelclass.SensorResponseItem
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.JobDetailActivity
import com.webclues.IPPSManager.activity.SensorDetailHistoryActivity
import com.webclues.IPPSManager.utility.Content
import com.webclues.IPPSManager.utility.Timeago

class NewNotificationsAdapter(
    var context: Activity

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var ITEM: Int = 0
    private var LOADING = 1
    var isloadingadded = false
    var notificationarraylist: ArrayList<NotificationListModel>? = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {


        var viewholder: RecyclerView.ViewHolder? = null

        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> {
                val viewItem = inflater.inflate(R.layout.adp_notifications, parent, false)
                viewholder = MyViewholder(viewItem)

            }

            LOADING -> {
                val viewLoading = inflater.inflate(R.layout.pagination_progressbar, parent, false)
                viewholder = LoadingViewholder(viewLoading)
            }


        }


        return viewholder!!
    }

    override fun getItemCount(): Int {
        return if (notificationarraylist == null) 0 else notificationarraylist!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val notificationModel = notificationarraylist!!.get(position)
        when (getItemViewType(position)) {
            ITEM -> {
                val holder = holder as MyViewholder
                holder.txtTime.text = Timeago(context).convertedtimeago(notificationModel.date_time!!)
                holder.txtDesc.text = notificationModel.content
            }
            LOADING -> {
                showloadingview(holder as LoadingViewholder, position)
            }
        }
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if(notificationModel.type == Content.LIMIT_ALARM){
                    val sensor = SensorResponseItem()
                    sensor.sensor_name =notificationModel.sensor_name
                    sensor._id =notificationModel.sensor_id.toString()
                    val intent = Intent(context, SensorDetailHistoryActivity::class.java)
                    intent.putExtra("sensorModel", Gson().toJson(sensor))
                    intent.putExtra("from","notification")
                    intent.putExtra("filter",notificationModel.filter_type)
                    context.startActivity(intent)
                }else{
                    val intent = Intent(context, JobDetailActivity::class.java)
                    intent.putExtra(Content.JOB_ID, notificationModel.job_id)
                    intent.putExtra(Content.JOB, Content.JOBTYPE)
                    context.startActivityForResult(intent, 100)
                }

            }

        })
    }


    fun add(notificationModel: NotificationListModel) {
        notificationarraylist!!.add(notificationModel)
        notifyItemInserted(notificationarraylist!!.size - 1)
    }



    fun addall(list: ArrayList<NotificationListModel>) {
        for (notificationmodel in list) {
            add(notificationmodel)
        }
    }

    fun remove(notificationModel: NotificationListModel) {
        var position: Int = notificationarraylist!!.indexOf(notificationModel)
        if (position > -1) {
            notificationarraylist!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun clear() {
        isloadingadded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    fun isEmpty(): Boolean {
        return itemCount == 0
    }


    fun addloadingfooter() {
        isloadingadded = true
        add(NotificationListModel())

    }

    fun removeloadingfooter() {
        isloadingadded = false

        val position = notificationarraylist!!.size - 1
        val result = getItem(position)

        if (result != null) {
            notificationarraylist!!.removeAt(position)
            notifyItemRemoved(position)
        }

    }

    override fun getItemViewType(position: Int): Int {


        if (position == notificationarraylist!!.size - 1 && isloadingadded) {
            return LOADING

        } else {
            return ITEM
        }
    }


    fun getItem(position: Int): NotificationListModel {
        return notificationarraylist!!.get(position)
    }

    fun showloadingview(viewholder: LoadingViewholder, position: Int) {

    }

    class MyViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtTime = itemView.findViewById(R.id.txtTime) as TextView
        var txtDesc = itemView.findViewById(R.id.txtDescription) as TextView
    }

    class LoadingViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var progressBar = itemView.findViewById(R.id.progressBar) as ProgressBar
    }
}
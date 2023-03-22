package com.webclues.IPPSManager.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.webclues.IPPSManager.Modelclass.SensorResponseItem
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.SensorDetailHistoryActivity
import kotlinx.android.synthetic.main.adp_sensor_history.view.*


class SensorAdapter(
    var context: Context,
    var SensorHistoryList: ArrayList<SensorResponseItem>
) :
    RecyclerView.Adapter<SensorAdapter.Myviewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorAdapter.Myviewholder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.adp_sensor_history, null)
        return Myviewholder(view)
    }

    override fun getItemCount(): Int {
        return SensorHistoryList.size
    }

    override fun onBindViewHolder(holder: SensorAdapter.Myviewholder, position: Int) {

        val SensorItem = SensorHistoryList[position]
        holder.txtSensor.text=SensorItem.sensor_name

        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                context.startActivity(Intent(context, SensorDetailHistoryActivity::class.java)
                    .putExtra("sensorModel",Gson().toJson(SensorItem)))
            }

        })

    }

    class Myviewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        var txtSensor = itemview.txtSensor
        var txtHistory = itemview.txtHistory
    }
}
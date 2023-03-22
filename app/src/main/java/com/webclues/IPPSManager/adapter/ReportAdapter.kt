package com.webclues.IPPSManager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webclues.IPPSManager.Modelclass.ReportResponseItem
import com.webclues.IPPSManager.R

class ReportAdapter(var context: Context, var ReportArraylist: ArrayList<ReportResponseItem>) :
    RecyclerView.Adapter<ReportAdapter.MyViewholder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportAdapter.MyViewholder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.adp_reports, parent, false)
        return MyViewholder(view)
    }

    override fun getItemCount(): Int {
        return ReportArraylist.size
    }

    override fun onBindViewHolder(holder: ReportAdapter.MyViewholder, position: Int) {
        var reportresponseitem: ReportResponseItem = ReportArraylist.get(position)
        holder.txtMachineName.setText(reportresponseitem.MachineName)
        holder.txtMachineOperator.setText(reportresponseitem.Machineoperator)
    }

    class MyViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {

        var txtMachineName = itemview.findViewById(R.id.txtMachineName) as TextView
        var txtMachineOperator = itemview.findViewById(R.id.txtmachineOperator) as TextView
        var txtDetails = itemview.findViewById(R.id.txtDetails) as TextView

    }
}
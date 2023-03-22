package com.webclues.IPPSManager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.webclues.IPPSManager.Interface.ItemClickListner
import com.webclues.IPPSManager.Modelclass.EngineerModel
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.JobDetailActivity

class EngineerNameListAdapter(
    var context: JobDetailActivity,
    var engineerlist: ArrayList<EngineerModel>,
    var itemclicklistner: ItemClickListner
) :
    RecyclerView.Adapter<EngineerNameListAdapter.MyViewholder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewholder {

        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.adp_engineername, parent, false)
        return MyViewholder(view)
    }

    override fun getItemCount(): Int {
        return engineerlist.size
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        val engineermodel = engineerlist.get(position)
        holder.txtEngineer.text=engineermodel.engineer_name
        if (position == itemCount - 1) {
            holder.view.visibility = View.GONE
        }
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                itemclicklistner.onclick(v!!, position)
            }

        })

    }

    fun SetItemclicklistner(itemClickListner: ItemClickListner) {
        this.itemclicklistner = itemClickListner
    }


    class MyViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtEngineer: TextView = itemView.findViewById(R.id.txtEngineerName)
        val view: View = itemView.findViewById(R.id.View)
    }

}
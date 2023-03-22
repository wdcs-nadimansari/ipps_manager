package com.webclues.IPPSManager.adapter

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.webclues.IPPSManager.Modelclass.EngineerModel
import com.webclues.IPPSManager.Modelclass.EngineerResponseItem
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.EngineerDetailsActivity
import com.webclues.IPPSManager.activity.EngineerListActivity
import com.webclues.IPPSManager.utility.Content
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.adp_engineerlist.view.*
import androidx.core.content.ContextCompat.startActivity
import android.os.Bundle
import com.webclues.IPPSManager.activity.MainActivity
import java.io.Serializable


class EngineerAdapter(
    var context: Context,
    var engineerlist: ArrayList<EngineerModel>
) :
    RecyclerView.Adapter<EngineerAdapter.MyViewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewholder {

        var view = LayoutInflater.from(parent.context).inflate(R.layout.adp_engineerlist, null)
        return MyViewholder(view)

    }


    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        val engineerModel: EngineerModel = engineerlist.get(position)
        holder.txtEngineerName.text = engineerModel.engineer_name
        holder.txtEnginnerPosition.text = engineerModel.position.position_name
        Picasso.get().load(engineerModel.profile_pic).placeholder(R.drawable.ic_placeholder_profile)
            .error(R.drawable.ic_placeholder_profile).into(holder.ivEngineerimage)

        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                context.startActivity(
                    Intent(context, EngineerDetailsActivity::class.java)
                        .putExtra(Content.ENGINEER_ID, engineerModel.engineer_id)
                        .putExtra(Content.ENGINEER_NAME, engineerModel.engineer_name)
                        .putExtra(Content.ENGINEER_POSITION, engineerModel.position.position_name)
                        .putExtra(Content.ENGINEER_EMAIL, engineerModel.email)
                        .putExtra(Content.ENGINEER_COMPANY, engineerModel.company.company_name)
                        .putExtra(Content.ENGINEER_NUMBER, engineerModel.phone)
                        .putExtra(Content.ENGINEER_PROFILE_PIC, engineerModel.profile_pic)

                )
            }

        })


    }

    override fun getItemCount(): Int {

        return engineerlist.size
    }

    class MyViewholder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        var ivEngineerimage = itemview.ivEngineerProfile
        var txtEngineerName = itemview.txtEngineerName
        var txtEnginnerPosition = itemview.txtEnginnerPosition
        var view = itemView.view
    }

}
package com.webclues.IPPSManager.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.webclues.IPPSManager.Modelclass.JobStatusModel
import com.webclues.IPPSManager.Modelclass.MetalResponseItem
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.EngineerDetailsActivity
import com.webclues.IPPSManager.activity.JobDetailActivity
import com.webclues.IPPSManager.utility.Content
import com.webclues.IPPSManager.utility.RoundedTransformationPicasso
import com.webclues.IPPSManager.utility.Utility
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_job_detail.*

class JobOrderAdapter(
    private val context: Activity

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var ITEM: Int = 0
    private var LOADING = 1
    var isloadingadded = false
    val jobstatuslist: ArrayList<JobStatusModel>? = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        var viewholder: RecyclerView.ViewHolder? = null

        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> {
                val viewItem = inflater.inflate(R.layout.adp_joborder, parent, false)
                viewholder = Myviewholder(viewItem)

            }

            LOADING -> {
                val viewLoading = inflater.inflate(R.layout.pagination_progressbar, parent, false)
                viewholder = LoadingViewholder(viewLoading)
            }


        }


        return viewholder!!
    }


    override fun getItemCount(): Int {
        return if (jobstatuslist == null) 0 else jobstatuslist.size

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var JobStatusModel: JobStatusModel = jobstatuslist!!.get(position)



        when (getItemViewType(position)) {
            ITEM -> {
                val holder = holder as Myviewholder
                holder.txtMachineName.text = JobStatusModel.machine_name
                holder.txtProblemName.text = JobStatusModel.problem
                holder.txtLocationName.text = JobStatusModel.location
                holder.txtEngineerName.text = JobStatusModel.engineer_name

                holder.txtDate.text = Utility.getDatewithTimestamp(JobStatusModel.created_at!!)
                Picasso.get().load(JobStatusModel.images!!.image_url)
                    .transform(RoundedTransformationPicasso(20, 0))
                    .placeholder(R.drawable.ic_placeholder_job).error(R.drawable.ic_placeholder_job)
                    .into(holder.ivJobImage, object : Callback {
                        override fun onError(e: Exception?) {
                            Toast.makeText(context, e?.message.toString(), Toast.LENGTH_LONG).show()
                        }

                        override fun onSuccess() {
                            holder.progress.visibility = View.GONE
                            holder.ivJobImage.visibility = View.VISIBLE
                        }

                    })


                if (JobStatusModel.priority == Content.ALL) {
                    holder.txtPriority.text = context.resources.getString(R.string.all)

                } else if (JobStatusModel.priority == Content.PENDING) {
                    holder.txtPriority.text = context.resources.getString(R.string.pending)
                } else if (JobStatusModel.priority == Content.LOW) {
                    holder.txtPriority.text = context.resources.getString(R.string.low)

                } else if (JobStatusModel.priority == Content.MEDIUM) {
                    holder.txtPriority.text = context.resources.getString(R.string.medium)

                } else if (JobStatusModel.priority == Content.HIGH) {
                    holder.txtPriority.text = context.resources.getString(R.string.high)
                }


                if (JobStatusModel.job_status == Content.JOB_REQUEST) {
                    holder.txtEngineerName.visibility = View.GONE
                    holder.txtStatus.text = context.resources.getString(R.string.job_request)
                    holder.txtStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.fontColorBlack50
                        )
                    )

                } else if (JobStatusModel.job_status == Content.ASSIGNED) {
                    holder.txtEngineerName.visibility = View.VISIBLE
                    holder.txtStatus.text = context.resources.getString(R.string.assigned)
                    holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.yellow))

                } else if (JobStatusModel.job_status == Content.WORKORDER) {
                    holder.txtEngineerName.visibility = View.VISIBLE
                    holder.txtStatus.text = context.resources.getString(R.string.workorder)
                    holder.txtStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimary
                        )
                    )


                } else if (JobStatusModel.job_status == Content.COMPLETED) {
                    holder.txtEngineerName.visibility = View.VISIBLE
                    holder.txtStatus.text = context.resources.getString(R.string.completed)
                    holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.green))


                } else if (JobStatusModel.job_status == Content.DECLINE) {
                    holder.txtEngineerName.visibility = View.GONE
                    holder.txtStatus.text = context.resources.getString(R.string.decline)
                    holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.red))


                } else if (JobStatusModel.job_status == Content.INCOMPLETE) {
                    holder.txtEngineerName.visibility = View.VISIBLE
                    holder.txtStatus.text = context.resources.getString(R.string.incomplete)
                    holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.orange))


                } else if (JobStatusModel.job_status == Content.KIV) {
                    holder.txtEngineerName.visibility = View.GONE
                    holder.txtStatus.text = context.resources.getString(R.string.kiv)
                    holder.txtStatus.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.bluelight
                        )
                    )
                }

                holder.itemView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        val intent = Intent(context, JobDetailActivity::class.java)
                        intent.putExtra(Content.JOB_ID, JobStatusModel.job_id)
                        intent.putExtra(Content.JOB, Content.JOBTYPE)
                        context.startActivityForResult(intent, 100)
                    }


                })

            }

            LOADING -> {
                showloadingview(holder as LoadingViewholder, position)


            }


        }


    }

    fun add(jobStatusModel: JobStatusModel?) {
        jobstatuslist!!.add(jobStatusModel!!)
        notifyItemInserted(jobstatuslist.size - 1)


    }


    fun addall(joblist: ArrayList<JobStatusModel>?) {
        for (jobstatusmodel in joblist!!) {
            add(jobstatusmodel)
        }

    }

    fun remove(jobStatusModel: JobStatusModel) {
        var position: Int = jobstatuslist!!.indexOf(jobStatusModel)
        if (position > -1) {
            jobstatuslist.removeAt(position)
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
        add(JobStatusModel())

    }

    fun removeloadingfooter() {
        isloadingadded = false

        val position = jobstatuslist!!.size - 1
        val result = getItem(position)

        if (result != null) {
            jobstatuslist.removeAt(position)
            notifyItemRemoved(position)
        }

    }

    override fun getItemViewType(position: Int): Int {


        if (position == jobstatuslist!!.size - 1 && isloadingadded) {
            return LOADING

        } else {
            return ITEM
        }


    }


    fun getItem(position: Int): JobStatusModel {
        return jobstatuslist!!.get(position)
    }

    fun showloadingview(viewholder: LoadingViewholder, position: Int) {

    }


    class Myviewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var ivJobImage = itemView.findViewById(R.id.ivJobImage) as ImageView
        var txtPriority = itemView.findViewById(R.id.txtPriority) as TextView
        var txtMachineName = itemView.findViewById(R.id.txtMachineName) as TextView
        var txtLocationName = itemView.findViewById(R.id.txtLocationName) as TextView
        var txtProblemName = itemView.findViewById(R.id.txtProblemName) as TextView
        var txtDate = itemView.findViewById(R.id.txtDate) as TextView
        var txtStatus = itemView.findViewById(R.id.txtStatus) as TextView
        var txtEngineerName = itemView.findViewById(R.id.txtEngineerName) as TextView
        var progress = itemView.findViewById(R.id.progress) as ProgressBar
    }

    class LoadingViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var progressBar = itemView.findViewById(R.id.progressBar) as ProgressBar
    }

}
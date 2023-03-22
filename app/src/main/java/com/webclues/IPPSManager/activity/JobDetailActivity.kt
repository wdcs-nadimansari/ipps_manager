package com.webclues.IPPSManager.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSManager.Interface.ItemClickListner
import com.webclues.IPPSManager.Modelclass.EngineerModel
import com.webclues.IPPSManager.Modelclass.JobDetailModel
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.adapter.EngineerNameListAdapter
import com.webclues.IPPSManager.adapter.JobImageViewpagerAdapter
import com.webclues.IPPSManager.service.APIClient
import com.webclues.IPPSManager.service.checkNetworkState
import com.webclues.IPPSManager.utility.Content
import com.webclues.IPPSManager.utility.CustomProgress
import com.webclues.IPPSManager.utility.Log
import com.webclues.IPPSManager.utility.Utility
import com.webclues.IPPSManager.view.MaterialAutoCompleteTextView
import com.webclues.IPPSManager.service.ApiInterface
import kotlinx.android.synthetic.main.activity_job_detail.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class JobDetailActivity : AppCompatActivity(), View.OnClickListener {


    private var dotscount: Int = 0
    var dots: Array<ImageView?> = arrayOf()
    lateinit var viewpagerAdapter: JobImageViewpagerAdapter
    lateinit var JobStatus: String
    lateinit var JobPriority: String
    var declinecomment: String = ""
    var engineerlist: ArrayList<EngineerModel>? = arrayListOf()
    var stringengineerlist: ArrayList<String> = arrayListOf()
    var engineer_id: Int = 0
    lateinit var customProgress: CustomProgress
    lateinit var apiInterface: ApiInterface
    var Jobid: Int = 0
    var type: Int = 0
    var status: Int = 0
    var priority: Int = 0
    var changeengineerpriority: Int = 0
    lateinit var context: Context
    lateinit var jobDetailModel: JobDetailModel

    lateinit var AssignPriority: String
    private var radioselected: String? = null
    var changedStatus = false
    var jobtype: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_detail)
        context = this
        initview()
    }

    private fun initview() {
        AssignPriority = ""

        customProgress = CustomProgress.instance
        apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)

        if (intent.extras != null) {
            Jobid = intent.getIntExtra(Content.JOB_ID, 0)
            status = intent.getIntExtra(Content.JOB_STATUS, 0)
            priority = intent.getIntExtra(Content.JOB_PRIORITY, 0)

        }

        if (getIntent().hasExtra(Content.JOB)) {
            jobtype = getIntent().getStringExtra(Content.JOBTYPE)
        }


        SetListner()

        if (checkNetworkState(context)) {

            jobdetail(Jobid)
        } else {
            Utility().showOkDialog(
                context,
                resources.getString(R.string.app_name),
                resources.getString(R.string.error_internet_ln)
            )
        }


    }

    private fun SetListner() {

        ivBack.setOnClickListener(this)
        btnDecline.setOnClickListener(this)
        btnAccept.setOnClickListener(this)
        txtAssignPriority.setOnClickListener(this)
        btnChangeEngneerName.setOnClickListener(this)
        btnUpdte.setOnClickListener(this)
        btnEndJob.setOnClickListener(this)
        btnMoveJobOrder.setOnClickListener(this)

    }


    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
            R.id.btnDecline -> {
                ShowDecline()
            }
            R.id.btnAccept -> {
                ShowAccept()
            }

            R.id.txtAssignPriority -> {
                SetAssignPriority()
            }

            R.id.btnChangeEngneerName -> {
                ChangeEngineerName()
            }

            R.id.btnUpdte -> {
                /*  if (jobDetailModel.job_status == Content.DECLINE) {
                      ShowUpdateEngineer()
                  } else{
  //                    updateengineername(Jobid, priority, 2, engineer_id,)
                  }*/
                ShowUpdateEngineer()
            }

            R.id.btnEndJob -> {
                finish()

            }
            R.id.btnMoveJobOrder -> {
                movetojobrequest(Jobid)
            }
        }
    }


    private fun jobdetail(jobid: Int) {
        customProgress.showProgress(this, getString(R.string.please_wait), false)
        val call: Call<JsonObject> = apiInterface.jobdetails(jobid)
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                customProgress.hideProgress()

                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        if (data != null) {
                            changedStatus = true

                            jobDetailModel =
                                Gson().fromJson(data.toString(), JobDetailModel::class.java)
                            SetJobDetailData(jobDetailModel)
//                            setimageviewpageradapter(jobDetailModel)

                        }
                    } else {
                        Utility().showOkDialog(
                            context,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }
                } else if (statuscode == 403) {
                    Utility().showInactiveDialog(
                        context,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                } else {
                    Utility().showOkDialog(
                        context,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                customProgress.hideProgress()
                Utility().showOkDialog(
                    context,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }

        })

    }

    private fun Setstatusandpriority(jobDetailModel: JobDetailModel) {


        if (jobDetailModel.priority == Content.ALL) {
            JobPriority = getString(R.string.all)

        } else if (jobDetailModel.priority == Content.PENDING) {
            JobPriority = getString(R.string.pending)
        } else if (jobDetailModel.priority == Content.LOW) {
            JobPriority = getString(R.string.low)

        } else if (jobDetailModel.priority == Content.MEDIUM) {
            JobPriority = getString(R.string.medium)

        } else if (jobDetailModel.priority == Content.HIGH) {
            JobPriority = getString(R.string.high)
        }

    }


    private fun SetJobDetailData(jobDetailModel: JobDetailModel) {       //Set JobDetail data

        Setstatusandpriority(jobDetailModel)


        if (jobDetailModel.job_status == Content.JOB_REQUEST) {
            JobStatus = getString(R.string.job_request)

            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.fontColorRegularGrey))
            txtStatus.setBackgroundResource(R.drawable.rounded_lightgrey)
            txtStatusPriority.setBackgroundResource(R.drawable.round_lightblue_bg)
            txtOperatorName.text =
                jobDetailModel.created_user_name + " | " + Utility.getDateTime(jobDetailModel.created_time)

            txtMachinename.setText(jobDetailModel.machine_name)
            txtLocation.setText(jobDetailModel.location_name)
            txtProblem.setText(jobDetailModel.problem_name)

            if (!jobDetailModel.comment.isNullOrEmpty()) {
                llDescriptions.visibility = View.VISIBLE
                txtCommentDesc.setText(jobDetailModel.comment)

            }
            if (!jobDetailModel.engineer_id.isEmpty()) {
                engineer_id = jobDetailModel.engineer_id.toInt()

            }
            llRequest.visibility = View.VISIBLE
            txtPriority.visibility = View.VISIBLE
            llEngineerName.visibility = View.GONE
            txtJobStartTime.visibility = View.GONE
            llJobDuration.visibility = View.GONE

        } else if (jobDetailModel.job_status == Content.WORKORDER) {
            JobStatus = getString(R.string.workorder)

            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            txtStatus.setBackgroundResource(R.drawable.round_lightblue_bg)
            txtStatusPriority.setBackgroundResource(R.drawable.round_lightblue_bg)
            txtOperatorName.text =
                jobDetailModel.created_user_name + " | " + Utility.getDateTime(jobDetailModel.created_time)
            if (!jobDetailModel.job_start_time.equals("")) {

                txtJobStartTime.text = Utility.getDateTime(jobDetailModel.job_start_time.toLong())
            }
            engineer_id = jobDetailModel.engineer_id.toInt()
            txtEngineerName.text = jobDetailModel.engineer_name
            txtMachinename.text = jobDetailModel.machine_name
            txtLocation.text = jobDetailModel.location_name
            txtProblem.text = jobDetailModel.problem_name
            if (!jobDetailModel.comment.isNullOrEmpty()) {
                llDescriptions.visibility = View.VISIBLE
                txtCommentDesc.setText(jobDetailModel.comment)

            }

            llJobStartTime.visibility = View.VISIBLE
            rrJobDuration.visibility = View.GONE
            edtJobDuration.visibility = View.GONE
            llJobDuration.visibility = View.GONE
            llEngineerName.visibility = View.VISIBLE
            edtJobDuration.visibility = View.GONE
            btnEndJob.visibility = View.GONE

        } else if (jobDetailModel.job_status == Content.ASSIGNED) {
            JobStatus = getString(R.string.assigned)

            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.yellow))
            txtStatus.setBackgroundResource(R.drawable.rounded_lightyellow)

            txtAssignPriority.text = JobPriority
            txtEngineerName.setText(jobDetailModel.engineer_name)
            llMetalDesc.setBackgroundColor(ContextCompat.getColor(this, R.color.extraLightGrey))
            txtOperatorName.text =
                jobDetailModel.created_user_name + " | " + Utility.getDateTime(jobDetailModel.created_time)

            txtMachinename.text = jobDetailModel.machine_name
            txtLocation.text = jobDetailModel.location_name
            txtProblem.text = jobDetailModel.problem_name

            engineer_id = jobDetailModel.engineer_id.toInt()
            changeengineerpriority = jobDetailModel.priority

            if (!jobDetailModel.comment.isNullOrEmpty()) {
                llDescriptions.visibility = View.VISIBLE
                txtCommentDesc.setText(jobDetailModel.comment)

            }


            txtStatusPriority.visibility = View.GONE
            llJobDuration.visibility = View.GONE
            txtJobStartTime.visibility = View.GONE
            btnUpdte.visibility = View.VISIBLE
            btnChangeEngneerName.visibility = View.VISIBLE
            txtAssignPriority.visibility = View.VISIBLE

        } else if (jobDetailModel.job_status == Content.KIV) {
            JobStatus = getString(R.string.kiv)

            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.bluelight))
            txtStatus.setBackgroundResource(R.drawable.round_lightblue_bg)
            txtStatusPriority.setBackgroundResource(R.drawable.round_lightblue_bg)
            txtOperatorName.text =
                jobDetailModel.created_user_name + " | " + Utility.getDateTime(jobDetailModel.created_time)

            txtMachinename.setText(jobDetailModel.machine_name)
            txtLocation.setText(jobDetailModel.location_name)
            txtProblem.setText(jobDetailModel.problem_name)

            if (!jobDetailModel.comment.isNullOrEmpty()) {
                llDescriptions.visibility = View.VISIBLE
                txtCommentDesc.setText(jobDetailModel.comment)

            }

            txtPriority.visibility = View.VISIBLE
            llEngineerName.visibility = View.GONE
            txtJobStartTime.visibility = View.GONE
            llJobDuration.visibility = View.GONE
            btnMoveJobOrder.visibility = View.VISIBLE


        } else if (jobDetailModel.job_status == Content.COMPLETED) {
            JobStatus = getString(R.string.completed)

            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
            txtStatus.setBackgroundResource(R.drawable.round_lightgreen_bg)

            txtStatusPriority.setBackgroundResource(R.drawable.round_lightblue_bg)
            llMetalDesc.setBackgroundColor(ContextCompat.getColor(this, R.color.extraLightGrey))
            txtOperatorName.text =
                jobDetailModel.created_user_name + " | " + Utility.getDateTime(jobDetailModel.created_time)

            txtEngineerName.text = jobDetailModel.engineer_name
            txtMachinename.text = jobDetailModel.machine_name
            txtLocation.text = jobDetailModel.location_name
            txtProblem.text = jobDetailModel.problem_name
            txtJobDuration.text = jobDetailModel.job_duration

            txtJobStartTime.text = Utility.getDateTime(jobDetailModel.job_start_time.toLong())

            if (!jobDetailModel.comment.isNullOrEmpty()) {
                llDescriptions.visibility = View.VISIBLE
                txtCommentDesc.setText(jobDetailModel.comment)

            }


            llJobStartTime.visibility = View.VISIBLE

        } else if (jobDetailModel.job_status == Content.DECLINE) {

            JobStatus = getString(R.string.decline)


            txtStatus.setBackgroundResource(R.drawable.round_lightred_bg)
            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.red))

            txtStatusPriority.setBackgroundResource(R.drawable.round_lightblue_bg)
            llMetalDesc.setBackgroundColor(ContextCompat.getColor(this, R.color.extraLightGrey))
            txtOperatorName.text =
                jobDetailModel.created_user_name + " | " + Utility.getDateTime(jobDetailModel.created_time)
            if (jobDetailModel.engineer_name.isNotBlank()) {
                txtEngineerName.setText(jobDetailModel.engineer_name)
            } else {
                txtEngineerName.setText("Not Assigned")
            }

//            txtDeclineComment.setText(jobDetailModel.decline_reason)
            txtMachinename.setText(jobDetailModel.machine_name)
            txtLocation.setText(jobDetailModel.location_name)
            txtProblem.setText(jobDetailModel.problem_name)
            txtDeclineBy.setText(context.resources.getString(R.string.decline_by) + " " + jobDetailModel.declined_by + ":")
            txtDeclineByUser.setText(jobDetailModel.declined_by_user)
            txtDeclineReason.setText(jobDetailModel.decline_reason)

            if (!jobDetailModel.comment.isNullOrEmpty()) {
                llDescriptions.visibility = View.VISIBLE
                txtCommentDesc.setText(jobDetailModel.comment)

            }
            engineer_id = jobDetailModel.engineer_id.toInt()
            btnUpdte.visibility = View.VISIBLE
            btnChangeEngneerName.visibility = View.VISIBLE
            llComments.visibility = View.GONE
            llJobDuration.visibility = View.GONE
            llEngineerName.visibility = View.VISIBLE
            txtJobStartTime.visibility = View.GONE
            llDeclineBy.visibility = View.VISIBLE
            view.visibility = View.VISIBLE
//            btnUpdateJob.visibility=View.VISIBLE


        } else if (jobDetailModel.job_status == Content.INCOMPLETE) {
            JobStatus = getString(R.string.incomplete)


            txtStatus.setBackgroundResource(R.drawable.orange_bg)
            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.orange))
            txtStatusPriority.setBackgroundResource(R.drawable.round_lightblue_bg)
            llMetalDesc.setBackgroundColor(ContextCompat.getColor(this, R.color.extraLightGrey))
            txtOperatorName.text =
                jobDetailModel.created_user_name + " | " + Utility.getDateTime(jobDetailModel.created_time)

            txtMachinename.text = jobDetailModel.machine_name
            txtLocation.text = jobDetailModel.location_name
            txtProblem.text = jobDetailModel.problem_name
            txtEngineerComment.text = jobDetailModel.incomplete_reason
            txtEngineerName.text = jobDetailModel.engineer_name

            txtIncompleteReason.text = jobDetailModel.incomplete_reason
            txtJobDuration.text = jobDetailModel.job_duration


            if (!jobDetailModel.job_start_time.equals("")) {

                txtJobStartTime.text = Utility.getDateTime(jobDetailModel.job_start_time.toLong())
            }

            if (!jobDetailModel.comment.isNullOrEmpty()) {
                llDescriptions.visibility = View.VISIBLE
                txtCommentDesc.setText(jobDetailModel.comment)

            }


            llEngineerComment.visibility = View.GONE
            llJobDuration.visibility = View.VISIBLE
            llEngineerName.visibility = View.VISIBLE
            llJobStartTime.visibility = View.VISIBLE
            llIncompleteReason.visibility = View.VISIBLE
            llRequest.visibility = View.VISIBLE
            view.visibility = View.VISIBLE

        }
        txtStatus.setText(JobStatus)
        txtStatusPriority.setText(JobPriority)



        viewpagerAdapter = JobImageViewpagerAdapter(this, jobDetailModel.images)
        ivViewPager.adapter = viewpagerAdapter

        SetImagecount()

    }


    private fun SetImagecount() {
        dotscount = viewpagerAdapter.count
        dots = arrayOfNulls<ImageView>(dotscount)
        for (i in 0..dotscount - 1) {
            dots[i] = ImageView(this)
            dots[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.non_active_dot
                )
            )

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(8, 0, 8, 0)

            Indicator.addView(dots[i], params)
        }


        dots[0]!!.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.non_active_dot
            )
        );
        ivViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {


                for (i in 0 until dotscount) {
                    dots[i]!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.non_active_dot
                        )
                    )
                }

                dots[position]!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.active_dots
                    )
                )
            }
        })
    }


    private fun SetAssignPriority() {
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(
            R.layout.dialog_priority
        )
        dialog.window?.setBackgroundDrawableResource(R.drawable.rectangle_background);


        val txtLow: TextView = dialog.findViewById(R.id.txtLow)
        val txtHigh: TextView = dialog.findViewById(R.id.txtHigh)
        val txtMedium: TextView = dialog.findViewById(R.id.txtMedium)

        if (JobPriority.equals(resources.getString(R.string.low))) {
            txtLow.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            txtLow.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else if (JobPriority.equals(resources.getString(R.string.high))) {
            txtHigh.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            txtHigh.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else if (JobPriority.equals(resources.getString(R.string.medium))) {
            txtMedium.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            txtMedium.setTextColor(ContextCompat.getColor(this, R.color.white))
        }

        txtLow.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                changeengineerpriority = Content.LOW
                JobPriority = resources.getString(R.string.low)
                txtAssignPriority.setText(JobPriority)
                dialog.dismiss()
            }

        })
        txtHigh.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                changeengineerpriority = Content.HIGH
                JobPriority = resources.getString(R.string.high)
                txtAssignPriority.setText(JobPriority)
                dialog.dismiss()

            }

        })

        txtMedium.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                changeengineerpriority = Content.MEDIUM
                JobPriority = resources.getString(R.string.medium)
                txtAssignPriority.setText(JobPriority)
                dialog.dismiss()

            }
        })
        dialog.show()
    }

    private fun ChangeEngineerName() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.getWindow()!!.setBackgroundDrawableResource(R.drawable.rectangle_background);
        dialog.setContentView(R.layout.popup_engineer_list)
        changeengineerdata(dialog)
//        SetEngineerData(dialog)
        dialog.show()

    }

    private fun SetEngineerData(dialog: Dialog) {
        val engineerlist: ArrayList<String>? = arrayListOf()
        engineerlist!!.add("Engineer Name")
        engineerlist.add("Engineer Name")
        engineerlist.add("Engineer Name")
        engineerlist.add("Engineer Name")
        engineerlist.add("Engineer Name")
        engineerlist.add("Engineer Name")
        engineerlist.add("Engineer Name")
        engineerlist.add("Engineer Name")

        val recyclerView: RecyclerView = dialog.findViewById(R.id.rvEngineerName)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun ShowDecline() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.getWindow()!!.setBackgroundDrawableResource(R.drawable.rectangle_background);
        dialog.setContentView(
            R.layout.dialog_decline
        )
        val edtComment: EditText = dialog.findViewById(R.id.edtAddComment)
        val btnSend: Button = dialog.findViewById(R.id.btnSend)
        btnSend.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (edtComment.text.toString().trim().isEmpty()) {
                    Toast.makeText(
                        context,
                        getString(R.string.error_msg_reason_blank),
                        Toast.LENGTH_SHORT
                    ).show();
                    return
                }
                declinejob(Jobid, edtComment.text.toString().trim(), dialog)

            }

        })

        dialog.show()
    }


    private fun declinejob(jobid: Int, comment: String, dialog: Dialog) {
        customProgress.showProgress(context, getString(R.string.please_wait), false)
        val call: Call<JsonObject> = apiInterface.declinejob(jobid, comment)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    customProgress.hideProgress()
                    if (jsonObject.optBoolean("status")) {
                        Toast.makeText(context, jsonObject.optString("message"), Toast.LENGTH_LONG)
                            .show()
                        dialog.dismiss()
                        startActivity(Intent(context, MainActivity::class.java))
                        finishAffinity()
                    } else {
                        customProgress.hideProgress()
                        Utility().showOkDialog(
                            context,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }
                } else if (statuscode == 403) {
                    customProgress.hideProgress()
                    Utility().showInactiveDialog(
                        context,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                } else {
                    customProgress.hideProgress()
                    Utility().showOkDialog(
                        context,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                customProgress.hideProgress()
                Utility().showOkDialog(
                    context,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }

        })
    }

    private fun endjob(
        jobid: Int,
        timeduration: String,
        type: Int,
        comment: String,
        dialog: Dialog
    ) {
        customProgress.showProgress(this, getString(R.string.please_wait), false)
        val call: Call<JsonObject> = apiInterface.endjob(jobid, timeduration, type, comment)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                customProgress.hideProgress()
                if (response.code() == 200) {
                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        Toast.makeText(context, jsonObject.optString("message"), Toast.LENGTH_LONG)
                            .show()
                        dialog.dismiss()
//                        Navigate()

                    } else {
                        customProgress.hideProgress()
                        Utility().showOkDialog(
                            context,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                customProgress.hideProgress()
                customProgress.hideProgress()
                Utility().showOkDialog(
                    context,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }

        })
    }

    private fun movetojobrequest(jobid: Int) {
        customProgress.showProgress(this, getString(R.string.please_wait), false)
        val call: Call<JsonObject> = apiInterface.movetojobrequest(jobid)
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                customProgress.hideProgress()
                val jsonObject = JSONObject(response.body().toString())
                if (jsonObject.optInt("status_code") == 200) {
                    if (jsonObject.optBoolean("status")) {
                        Toast.makeText(context, jsonObject.optString("message"), Toast.LENGTH_LONG)
                            .show()
                        startActivity(Intent(context, MainActivity::class.java))
                        finishAffinity()
                    } else {
                        Toast.makeText(context, jsonObject.optString("message"), Toast.LENGTH_LONG)
                            .show()

                    }
                } else {
                    Utility().showOkDialog(
                        context,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                customProgress.hideProgress()
                Utility().showOkDialog(
                    context,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }


        })
    }


    private fun ShowAccept() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.getWindow()!!.setBackgroundDrawableResource(R.drawable.rectangle_background);
        dialog.setContentView(R.layout.dialog_accept)
        val txtLow: TextView = dialog.findViewById(R.id.txtLow)
        val txtMedium: TextView = dialog.findViewById(R.id.txtMedium)
        val txtHigh: TextView = dialog.findViewById(R.id.txtHigh)
        val llView: LinearLayout = dialog.findViewById(R.id.llView)
        val edtEngineerList: EditText = dialog.findViewById(R.id.edtEngineerList)
        val RadioGroup: RadioGroup = dialog.findViewById(R.id.RadioGroup)
        val rbkiv: RadioButton = dialog.findViewById(R.id.rbKiv)
        val rbAssignEngineer: RadioButton = dialog.findViewById(R.id.rbAssignEnginner)

        if (jobDetailModel.priority == Content.LOW) {
            priority = Content.LOW
            txtLow.setTextColor(ContextCompat.getColor(context, R.color.white))
            txtLow.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        } else if (jobDetailModel.priority == Content.MEDIUM) {
            priority = Content.MEDIUM
            txtMedium.setTextColor(ContextCompat.getColor(context, R.color.white))
            txtMedium.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        } else if (jobDetailModel.priority == Content.HIGH) {
            priority = Content.HIGH
            txtHigh.setTextColor(ContextCompat.getColor(context, R.color.white))
            txtHigh.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }


        rbkiv.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    rbkiv.isChecked = true
                    rbAssignEngineer.isChecked = false
                }
                type = Content.ASSIGN_KIV
                radioselected = "Kiv"
                disable(llView)
//                enableDisableView(dialog, llView, false)

            }

        })
        rbAssignEngineer.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    rbAssignEngineer.isChecked = true
                    rbkiv.isChecked = false
                }
                type = Content.ASSIGN_FITER_OPERATOR
                radioselected = "AssignEngineer"
                if (jobDetailModel.job_status == Content.INCOMPLETE) {
                    engineer_id = jobDetailModel.engineer_id.toInt()
                }
//
                enable(llView)
//                enableDisableView(dialog, llView, true)

            }

        })

        if (jobDetailModel.engineer_id.isNotBlank()) {
            rbAssignEngineer.isChecked = true
            rbkiv.isChecked = false
            edtEngineerList.setText(jobDetailModel.engineer_name)
        }

        txtLow.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                priority = Content.LOW
                txtLow.setTextColor(ContextCompat.getColor(context, R.color.white))
                txtLow.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))

                txtHigh.setTextColor(ContextCompat.getColor(context, R.color.colorlightgray))
                txtHigh.setBackgroundColor(ContextCompat.getColor(context, R.color.extraLightGrey))

                txtMedium.setTextColor(ContextCompat.getColor(context, R.color.colorlightgray))
                txtMedium.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.extraLightGrey
                    )
                )

            }
        })

        txtMedium.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                priority = Content.MEDIUM
                txtMedium.setTextColor(ContextCompat.getColor(context, R.color.white))
                txtMedium.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))

                txtLow.setTextColor(ContextCompat.getColor(context, R.color.colorlightgray))
                txtLow.setBackgroundColor(ContextCompat.getColor(context, R.color.extraLightGrey))

                txtHigh.setTextColor(ContextCompat.getColor(context, R.color.colorlightgray))
                txtHigh.setBackgroundColor(ContextCompat.getColor(context, R.color.extraLightGrey))
            }
        })

        txtHigh.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                priority = Content.HIGH
                txtHigh.setTextColor(ContextCompat.getColor(context, R.color.white))
                txtHigh.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))

                txtLow.setTextColor(ContextCompat.getColor(context, R.color.colorlightgray))
                txtLow.setBackgroundColor(ContextCompat.getColor(context, R.color.extraLightGrey))

                txtMedium.setTextColor(ContextCompat.getColor(context, R.color.colorlightgray))
                txtMedium.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.extraLightGrey
                    )
                )

            }
        })

        val btnsave: Button = dialog.findViewById(R.id.btnSave)
        btnsave.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (!radioselected.equals("Kiv")) {
                    if (RadioGroup.checkedRadioButtonId == -1) {
                        Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.error_radiooption_blank),
                            Toast.LENGTH_SHORT
                        ).show();
                        return
                    }
                    if (edtEngineerList.text.toString().trim().isEmpty()) {
                        Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.error_assignfitter_blank),
                            Toast.LENGTH_SHORT
                        ).show();
                        return
                    }

                    if (priority == 0) {
                        Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.error_priority_blank),
                            Toast.LENGTH_SHORT
                        ).show();
                        return
                    }
                }
                acceptjob(Jobid, priority, type, engineer_id, dialog)

//                startActivity(Intent(activity!!, LoginActivity::class.java))
//                startActivity(Intent(this@JobDetailActivity, MainActivity::class.java))
            }

        })

        dialog.show()
        getengineerlist(dialog)
//        ShowEngineerdata(dialog)
    }


    private fun ShowUpdateEngineer() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.getWindow()!!.setBackgroundDrawableResource(R.drawable.rectangle_background);
        dialog.setContentView(R.layout.dialog_accept)
        val txtLow: TextView = dialog.findViewById(R.id.txtLow)
        val txtMedium: TextView = dialog.findViewById(R.id.txtMedium)
        val txtHigh: TextView = dialog.findViewById(R.id.txtHigh)
        val llView: LinearLayout = dialog.findViewById(R.id.llView)
        val edtEngineerList: EditText = dialog.findViewById(R.id.edtEngineerList)
        val RadioGroup: RadioGroup = dialog.findViewById(R.id.RadioGroup)
        val rbkiv: RadioButton = dialog.findViewById(R.id.rbKiv)
        val rbAssignEngineer: RadioButton = dialog.findViewById(R.id.rbAssignEnginner)

        if (jobDetailModel.priority == Content.LOW) {
            priority = Content.LOW
            txtLow.setTextColor(ContextCompat.getColor(context, R.color.white))
            txtLow.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        } else if (jobDetailModel.priority == Content.MEDIUM) {
            priority = Content.MEDIUM
            txtMedium.setTextColor(ContextCompat.getColor(context, R.color.white))
            txtMedium.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        } else if (jobDetailModel.priority == Content.HIGH) {
            priority = Content.HIGH
            txtHigh.setTextColor(ContextCompat.getColor(context, R.color.white))
            txtHigh.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }


        rbkiv.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    rbkiv.isChecked = true
                    rbAssignEngineer.isChecked = false
                }
                type = Content.ASSIGN_KIV
                radioselected = "Kiv"
                disable(llView)
//                enableDisableView(dialog, llView, false)

            }

        })
        rbAssignEngineer.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    rbAssignEngineer.isChecked = true
                    rbkiv.isChecked = false
                }
                type = Content.ASSIGN_FITER_OPERATOR
                radioselected = "AssignEngineer"
                if (jobDetailModel.job_status == Content.INCOMPLETE) {
                    engineer_id = jobDetailModel.engineer_id.toInt()
                }
//
                enable(llView)
//                enableDisableView(dialog, llView, true)

            }

        })

        if (jobDetailModel.engineer_id.isNotBlank()) {
            rbAssignEngineer.isChecked = true
            rbkiv.isChecked = false
            edtEngineerList.setText(jobDetailModel.engineer_name)
        }

        txtLow.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                priority = Content.LOW
                txtLow.setTextColor(ContextCompat.getColor(context, R.color.white))
                txtLow.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))

                txtHigh.setTextColor(ContextCompat.getColor(context, R.color.colorlightgray))
                txtHigh.setBackgroundColor(ContextCompat.getColor(context, R.color.extraLightGrey))

                txtMedium.setTextColor(ContextCompat.getColor(context, R.color.colorlightgray))
                txtMedium.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.extraLightGrey
                    )
                )

            }
        })

        txtMedium.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                priority = Content.MEDIUM
                txtMedium.setTextColor(ContextCompat.getColor(context, R.color.white))
                txtMedium.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))

                txtLow.setTextColor(ContextCompat.getColor(context, R.color.colorlightgray))
                txtLow.setBackgroundColor(ContextCompat.getColor(context, R.color.extraLightGrey))

                txtHigh.setTextColor(ContextCompat.getColor(context, R.color.colorlightgray))
                txtHigh.setBackgroundColor(ContextCompat.getColor(context, R.color.extraLightGrey))
            }
        })

        txtHigh.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                priority = Content.HIGH
                txtHigh.setTextColor(ContextCompat.getColor(context, R.color.white))
                txtHigh.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))

                txtLow.setTextColor(ContextCompat.getColor(context, R.color.colorlightgray))
                txtLow.setBackgroundColor(ContextCompat.getColor(context, R.color.extraLightGrey))

                txtMedium.setTextColor(ContextCompat.getColor(context, R.color.colorlightgray))
                txtMedium.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.extraLightGrey
                    )
                )

            }
        })

        val btnsave: Button = dialog.findViewById(R.id.btnSave)
        btnsave.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (!radioselected.equals("Kiv")) {
                    if (RadioGroup.checkedRadioButtonId == -1) {
                        Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.error_radiooption_blank),
                            Toast.LENGTH_SHORT
                        ).show();
                        return
                    }
                    if (edtEngineerList.text.toString().trim().isEmpty()) {
                        Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.error_assignfitter_blank),
                            Toast.LENGTH_SHORT
                        ).show();
                        return
                    }

                    if (priority == 0) {
                        Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.error_priority_blank),
                            Toast.LENGTH_SHORT
                        ).show();
                        return
                    }
                }
                acceptjob(Jobid, priority, type, engineer_id, dialog)

//                startActivity(Intent(activity!!, LoginActivity::class.java))
//                startActivity(Intent(this@JobDetailActivity, MainActivity::class.java))
            }

        })

        dialog.show()
        getengineerlist(dialog)
//        ShowEngineerdata(dialog)
    }


    private fun changeengineerdata(dialog: Dialog) {
        val call: Call<JsonObject> = apiInterface.getengineerlist()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("Response_code", "=" + response.code());
                if (response.code() == 200) {
                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val list = data.optJSONArray("engineers_list")
                        if (list != null && list.length() > 0) {
                            engineerlist!!.clear()

                            engineerlist!!.addAll(
                                Gson().fromJson(
                                    list.toString(),
                                    Array<EngineerModel>::class.java
                                ).toList()
                            )

                            setengineername(dialog, engineerlist!!)

                        }
                    }
                } else if (response.code() == 403) {
                    if (response.errorBody() != null) {
                        val `object` = JSONObject(response.errorBody()!!.string())

                        Utility().showOkDialog(
                            context,
                            resources.getString(R.string.app_name),
                            `object`.optString("message")
                        )
                    }

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utility().showOkDialog(
                    context,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )

            }
        })
    }

    fun setengineername(
        dialog: Dialog,
        engineerlist: ArrayList<EngineerModel>
    ) {


        val recyclerView: RecyclerView = dialog.findViewById(R.id.rvEngineerName)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter =
            EngineerNameListAdapter(this, engineerlist, object : ItemClickListner {
                override fun onclick(view: View, position: Int) {
                    txtEngineerName.text = engineerlist.get(position).engineer_name
                    engineer_id = engineerlist.get(position).engineer_id
                    dialog.dismiss()
                }

            })
    }

    fun updateengineername(
        jobid: Int,
        priority: Int,
        type: Int,
        engineer_id: Int,
        dialog: Dialog
    ) {
        customProgress.showProgress(context, getString(R.string.please_wait), false)
        val call: Call<JsonObject> = apiInterface.update_job(jobid, priority, type, engineer_id)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    customProgress.hideProgress()
                    if (jsonObject.optBoolean("status")) {
                        Toast.makeText(context, jsonObject.optString("message"), Toast.LENGTH_LONG)
                            .show()
                        dialog.dismiss()
                        startActivity(Intent(context, MainActivity::class.java))

                    } else {
                        customProgress.hideProgress()
                        Utility().showOkDialog(
                            context,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }

                } else if (statuscode == 403) {
                    customProgress.hideProgress()
                    Utility().showInactiveDialog(
                        context,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                } else {
                    customProgress.hideProgress()
                    Utility().showOkDialog(
                        context,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                customProgress.hideProgress()
                Utility().showOkDialog(
                    context,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }

        })
    }

    fun getengineerlist(dialog: Dialog) {
        val call: Call<JsonObject> = apiInterface.getengineerlist()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("Response_code", "=" + response.code());
                if (response.code() == 200) {
                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val list = data.optJSONArray("engineers_list")
                        if (list != null && list.length() > 0) {
                            engineerlist!!.clear()
                            engineerlist!!.addAll(
                                Gson().fromJson(
                                    list.toString(),
                                    Array<EngineerModel>::class.java
                                ).toList()
                            )

                            setengineerdata(dialog, engineerlist!!)

                        }
                    }
                } else if (response.code() == 403) {
                    if (response.errorBody() != null) {
                        val `object` = JSONObject(response.errorBody()!!.string())

                        Utility().showOkDialog(
                            context,
                            resources.getString(R.string.app_name),
                            `object`.optString("message")
                        )
                    }

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utility().showOkDialog(
                    context,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )

            }
        })

    }

    private fun setengineerdata(dialog: Dialog, engineerlist: ArrayList<EngineerModel>) {
        stringengineerlist.clear()
        for (items in engineerlist) {

            val count = items.work_order_count

            stringengineerlist.add(items.engineer_name + " (" + count + ")")

        }

        val engineeradapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, stringengineerlist)
        val edtEngineerList =
            dialog.findViewById(R.id.edtEngineerList) as MaterialAutoCompleteTextView
        edtEngineerList.setAdapter(engineeradapter)
        edtEngineerList.threshold = 0
        edtEngineerList.keyListener = null
        edtEngineerList.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                edtEngineerList.showDropDown()
                Utility.hideKeyboard(context)
                return false

            }

        })

        edtEngineerList.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                engineer_id = engineerlist.get(position).engineer_id
                Log.e("Engineer_id", "=" + engineer_id)
            }

        })
    }


    fun acceptjob(
        jobid: Int,
        priority: Int,
        type: Int,
        engineer_id: Int,
        dialog: Dialog
    ) {
        customProgress.showProgress(context, getString(R.string.please_wait), false)
        val call: Call<JsonObject> = apiInterface.acceptjob(jobid, priority, type, engineer_id)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                try {
                    val jsonObject = JSONObject(response.body().toString())
                    val statuscode = jsonObject.optInt("status_code")
                    if (statuscode == 200) {
                        customProgress.hideProgress()
                        if (jsonObject.optBoolean("status")) {
                            Toast.makeText(
                                context,
                                jsonObject.optString("message"),
                                Toast.LENGTH_LONG
                            )
                                .show()
                            dialog.dismiss()
                            startActivity(Intent(context, MainActivity::class.java))
                            finishAffinity()
                        } else {
                            customProgress.hideProgress()
                            Utility().showOkDialog(
                                context,
                                resources.getString(R.string.app_name),
                                jsonObject.optString("message")
                            )
                        }

                    } else if (statuscode == 403) {
                        customProgress.hideProgress()
                        Utility().showInactiveDialog(
                            context,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    } else {
                        customProgress.hideProgress()
                        Utility().showOkDialog(
                            context,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }
                } catch (e: Exception) {
                    Log.e("Exceptions", "=" + e.message)
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utility().showOkDialog(
                    context,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }

        })
    }


    private fun disable(layout: ViewGroup) {
        layout.isEnabled = false
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i)
            if (child is ViewGroup) {
                disable(child)
                layout.setBackgroundColor(ContextCompat.getColor(this, R.color.whitelight))
            } else {
                child.isEnabled = false
            }
        }
    }


    private fun enable(layout: ViewGroup) {
        layout.isEnabled = true
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i)
            if (child is ViewGroup) {
                layout.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                enable(child)
            } else {
                child.isEnabled = true
            }
        }
    }

    override fun onBackPressed() {
        if (changedStatus) {
            val intent = Intent()
            intent.putExtra("JobType", Content.JOBTYPE)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            super.onBackPressed()
        }
    }


}

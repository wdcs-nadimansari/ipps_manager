package com.webclues.IPPSManager.fragments.EngineerList

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSManager.Modelclass.JobStatusModel
import com.webclues.IPPSManager.Modelclass.MetalResponseItem

import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.EngineerDetailsActivity
import com.webclues.IPPSManager.adapter.JobOrderAdapter
import com.webclues.IPPSManager.adapter.PaginationScrollListener
import com.webclues.IPPSManager.service.APIClient
import com.webclues.IPPSManager.utility.Content
import com.webclues.IPPSManager.utility.CustomProgress
import com.webclues.IPPSManager.utility.Log
import com.webclues.IPPSManager.utility.Utility
import com.webclues.IPPSManager.service.ApiInterface
import kotlinx.android.synthetic.main.fragment_all.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EngineerJobRequestFragment : Fragment() {

    lateinit var srlJobs: SwipeRefreshLayout
    lateinit var rvJobs: RecyclerView
    lateinit var customProgress: CustomProgress
    lateinit var apiInterface: ApiInterface
    var jobOrderAdapter: JobOrderAdapter? = null
    var jobList: ArrayList<MetalResponseItem> = ArrayList()
    var jobstatuslist: ArrayList<JobStatusModel>? = arrayListOf()
    var page: Int = 1
    var PAGE_START: Int = 1
    var priority: Int = 0
    var engineer_id: Int = 0
    lateinit var status: String
    var currentpage: Int = PAGE_START
    var isloading: Boolean = false
    var islastpage: Boolean = false
    lateinit var engineerDetailsActivity: EngineerDetailsActivity


    private val mReceiver =
        object : BroadcastReceiver() {       //Setpriorirty data in EngineerJobRequest
            override fun onReceive(context: Context, intent: Intent) {
                Content.STATUS_PRIORITY = intent.getStringExtra(Content.PRIORITY)
                if (Content.STATUS_PRIORITY.equals(getString(R.string.pending))) {
                    Content.FILTER_PRIORITY = Content.PENDING
                } else if (Content.STATUS_PRIORITY.equals(getString(R.string.low))) {
                    Content.FILTER_PRIORITY = Content.LOW
                } else if (Content.STATUS_PRIORITY.equals(getString(R.string.medium))) {
                    Content.FILTER_PRIORITY = Content.MEDIUM
                } else if (Content.STATUS_PRIORITY.equals(getString(R.string.high))) {
                    Content.FILTER_PRIORITY = Content.HIGH
                } else {
                    Content.FILTER_PRIORITY = Content.ALL
                }
                islastpage = false
                jobOrderAdapter!!.jobstatuslist!!.clear()
                jobOrderAdapter!!.isloadingadded = false
                loadFirstpage()
            }


        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(activity!!)
            .registerReceiver(mReceiver, IntentFilter("ReconfirmBroadCast"))

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_all, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }

    fun initView(view: View) {
        engineerDetailsActivity = activity as EngineerDetailsActivity
        apiInterface = APIClient.getretrofit(activity!!).create(ApiInterface::class.java)
        customProgress = CustomProgress.instance
        isloading = false
        islastpage = false
        engineer_id = engineerDetailsActivity.engineer_id

        srlJobs = view.findViewById(R.id.srlJobs)
        rvJobs = view.findViewById(R.id.rvJobs)
        rvJobs.setLayoutManager(
            WrapContentLinearLayoutManager(
                activity!!
            )
        );
        jobOrderAdapter = JobOrderAdapter(activity!!)
        rvJobs.adapter = jobOrderAdapter
        rvJobs.addOnScrollListener(object :
            PaginationScrollListener(rvJobs.layoutManager as LinearLayoutManager) {
            override fun loadMoreItems() {
                isloading = true
                currentpage += 1
                loadNextPage(
                    currentpage,
                    Content.JOB_REQUEST,
                    Content.FILTER_PRIORITY,
                    engineer_id.toString()
                )
            }

            override fun isLoading(): Boolean {

                return isloading
            }

            override fun isLastPage(): Boolean {

                return islastpage
            }


        })

        srlJobs.setOnRefreshListener {
            islastpage = false
            jobOrderAdapter!!.jobstatuslist!!.clear()
            loadFirstpage()
        }



        loadFirstpage()


    }


    fun loadFirstpage(               //load first page data in EngineerJobRequest


    ) {
        srlJobs.isRefreshing = false
        currentpage = PAGE_START
        customProgress.showProgress(context!!, getString(R.string.please_wait), false)
        val call: Call<JsonObject> =
            apiInterface.jobstatus(
                currentpage,
                Content.JOB_REQUEST,
                Content.FILTER_PRIORITY,
                engineer_id.toString()
            )
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    customProgress.hideProgress()
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        Log.e("JsonData", "=" + data)
                        val joblist = data.optJSONArray("job_list")
                        if (joblist != null && joblist.length() > 0) {

                            jobstatuslist!!.clear()

                            jobstatuslist!!.addAll(
                                Gson().fromJson(
                                    joblist.toString(),
                                    Array<JobStatusModel>::class.java
                                ).toList()
                            )
                            jobOrderAdapter!!.addall(jobstatuslist)


                            if (currentpage.toString() < jsonObject.optString("total_pages")) jobOrderAdapter!!.addloadingfooter()
                            else islastpage = true


                        }
                    } else {
                        customProgress.hideProgress()

                        Utility().showOkDialog(
                            context!!,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }


                } else if (statuscode == 403) {
                    customProgress.hideProgress()
                    Utility().showInactiveDialog(
                        context!!,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )

                } else {
                    customProgress.hideProgress()

                    Utility().showOkDialog(
                        context!!,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                }
                jobOrderAdapter!!.notifyDataSetChanged()


            }


            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                customProgress.hideProgress()
                Utility().showOkDialog(
                    context!!,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }

        })
    }

    private fun loadNextPage(          //load next page in EngineerJobRequest
        pagenumber: Int,
        jobstatus: Int,
        filterpriority: Int,
        engineerid: String
    ) {

        val call: Call<JsonObject> =
            apiInterface.jobstatus(pagenumber, jobstatus, filterpriority, engineerid)
        call.enqueue(object : Callback<JsonObject> {


            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                jobOrderAdapter!!.removeloadingfooter()
                isloading = false


                if (response.code() == 200) {

                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {


                        val data = jsonObject.optJSONObject("data")
                        val joblist = data.optJSONArray("job_list")
                        if (joblist != null && joblist.length() > 0) {
                            jobstatuslist!!.clear()

                            jobstatuslist!!.addAll(
                                Gson().fromJson(
                                    joblist.toString(),
                                    Array<JobStatusModel>::class.java
                                ).toList()
                            )
                            jobOrderAdapter!!.addall(jobstatuslist)

                            if (currentpage.toString() != jsonObject.optString("total_pages")) jobOrderAdapter!!.addloadingfooter()
                            else islastpage = true


                        }
                    } else {
                        customProgress.hideProgress()

                        Utility().showOkDialog(
                            context!!,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }
                }
            }


            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utility().showOkDialog(
                    context!!,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }

        })


    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(mReceiver)
    }

    inner class WrapContentLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
        //... constructor
        override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: IndexOutOfBoundsException) {
                Log.e("TAG", "meet a IOOBE in RecyclerView")
            }

        }
    }

}

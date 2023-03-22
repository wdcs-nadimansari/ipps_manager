package com.webclues.IPPSManager.fragments.JobOrderList


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.webclues.IPPSManager.Modelclass.MetalResponseItem

import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.MainActivity
import com.webclues.IPPSManager.adapter.EngineerTaskAdapter
import com.webclues.IPPSManager.adapter.JobOrderAdapter
import kotlinx.android.synthetic.main.adp_joborder.*

/**
 * A simple [Fragment] subclass.
 */
class AssignedFragment : Fragment() {

    lateinit var srlJobs: SwipeRefreshLayout
    lateinit var rvJobs: RecyclerView
    lateinit var jobOrderAdapter: JobOrderAdapter
    lateinit var engineertaskadapter: EngineerTaskAdapter
    var jobList: ArrayList<MetalResponseItem> = ArrayList()
    var page: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_all, container, false)
        initView(view)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val mainActivity: MainActivity

        if (context is MainActivity) {
            mainActivity = context
        }
    }

    fun initView(view: View) {

//        srlJobs=view.findViewById(R.id.srlJobs)
        rvJobs = view.findViewById(R.id.rvJobs)
        rvJobs.layoutManager = LinearLayoutManager(context)
        rvJobs.setHasFixedSize(true)
//        if (activity is MainActivity) {
//            jobOrderAdapter = JobOrderAdapter(activity!!, jobList)
//            rvJobs.adapter = jobOrderAdapter
//        } else {
//            engineertaskadapter = EngineerTaskAdapter(activity!!, jobList)
//            rvJobs.adapter = engineertaskadapter
//        }
//
//        setdata()

        /* srlJobs.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
             override fun onRefresh() {
                 page= 1
                 callApi_project_list(page,false)
             }

         })*/
        /*rvJobs.addOnScrollListener(object : PaginationScrollListener(rvJobs.layoutManager as LinearLayoutManager){
            override fun isLastPage(): Boolean {
                return lastpage<=page
            }

            override fun isLoading(): Boolean {
                return  isLoading
            }

            override fun loadMoreItems() {
                Log.e("Load More-->","Called")
                isLoading = true
                page= page+1
                callApi_project_list(page,false)
            }

        })*/
    }

    private fun setdata() {
     /*   txtJobDescription?.setText(
            resources.getString(R.string.metaldesc) + "\n" + resources.getString(
                R.string.metal_desc
            )
        )*/
        jobList.clear()

        jobList.add(
            MetalResponseItem(
                R.drawable.metaltechnology,
                getString(R.string.metal_priority),
                getString(R.string.metalname),
                getString(R.string.metaldesc) + "\n" + getString(R.string.metal_desc),
                getString(R.string.metaldate),
                getString(R.string.assigned),"",""
            )
        )
        jobList.add(
            MetalResponseItem(
                R.drawable.metaltechnology1,
                "Low",
                getString(R.string.metalname1),
                getString(R.string.metaldesc) + "\n" + getString(R.string.metal_desc),
                getString(R.string.metaldate),
                getString(R.string.assigned),"",""
            )
        )
        jobList.add(
            MetalResponseItem(
                R.drawable.metaltechnology2,
                resources.getString(R.string.metal_priority),
                getString(R.string.metalname2),
                getString(R.string.metaldesc) + "\n" + getString(R.string.metal_desc),
                getString(R.string.metaldate),
                getString(R.string.assigned),"",""
            )
        )
        jobList.add(
            MetalResponseItem(
                R.drawable.metaltechnology3,
                "Low",
                getString(R.string.metalname3),
                getString(R.string.metaldesc) + "\n" + getString(R.string.metal_desc),
                getString(R.string.metaldate),
                getString(R.string.assigned),"",""
            )
        )
        jobList.add(
            MetalResponseItem(
                R.drawable.metaltechnology,
                "Low",
                getString(R.string.metalname3),
                getString(R.string.metaldesc) + "\n" + getString(R.string.metal_desc),
                getString(R.string.metaldate),
                getString(R.string.assigned),"",""
            )
        )
        if (activity is MainActivity) {

            jobOrderAdapter.notifyDataSetChanged()
        } else {
            engineertaskadapter.notifyDataSetChanged()
        }

    }

}

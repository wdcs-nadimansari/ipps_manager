package com.webclues.IPPSManager.fragments


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
import com.webclues.IPPSManager.activity.EngineerDetailsActivity
import com.webclues.IPPSManager.activity.MainActivity
import com.webclues.IPPSManager.adapter.EngineerTaskAdapter
import kotlinx.android.synthetic.main.adp_joborder.*

/**
 * A simple [Fragment] subclass.
 */
class EngineerTaskOrderFragment : Fragment() {

    lateinit var srlJobs: SwipeRefreshLayout
    lateinit var rvJobs: RecyclerView
    lateinit var EngineerTaskAdapter: EngineerTaskAdapter
    var EngineerJobList: ArrayList<MetalResponseItem> = ArrayList()
    var page: Int = 1
    lateinit var engineerDetailsActivity: EngineerDetailsActivity

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
        engineerDetailsActivity = activity as EngineerDetailsActivity
        engineerDetailsActivity.engineer_id
//        srlJobs=view.findViewById(R.id.srlJobs)
        rvJobs = view.findViewById(R.id.rvJobs)
        rvJobs.layoutManager = LinearLayoutManager(context)
        rvJobs.setHasFixedSize(true)
        EngineerTaskAdapter = EngineerTaskAdapter(activity!!, EngineerJobList)
        rvJobs.adapter = EngineerTaskAdapter
        setdata()

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
        /*  txtJobDescription?.setText(
              resources.getString(R.string.metaldesc) + "\n" + resources.getString(
                  R.string.metal_desc
              )
          )*/
        EngineerJobList.clear()

        EngineerJobList.add(
            MetalResponseItem(
                R.drawable.metaltechnology3,
                getString(R.string.metal_priority),
                getString(R.string.machinename),
                getString(R.string.locationname),
                getString(R.string.metaldate),
                getString(R.string.job_request), getString(R.string.problemname), ""
            )
        )
        EngineerJobList.add(
            MetalResponseItem(
                R.drawable.metaltechnology,
                getString(R.string.metal_priority),
                getString(R.string.machinename),
                getString(R.string.locationname),
                getString(R.string.metaldate),
                getString(R.string.job_request), getString(R.string.problemname), ""
            )
        )
        EngineerJobList.add(
            MetalResponseItem(
                R.drawable.metaltechnology,
                resources.getString(R.string.metal_priority),
                getString(R.string.machinename),
                getString(R.string.locationname),
                getString(R.string.metaldate),
                getString(R.string.job_request), getString(R.string.problemname), ""
            )
        )

        EngineerTaskAdapter.notifyDataSetChanged()
    }
}

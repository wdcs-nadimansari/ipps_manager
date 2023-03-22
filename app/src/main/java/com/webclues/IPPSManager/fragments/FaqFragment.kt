package com.webclues.IPPSManager.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSManager.Modelclass.FaqResponse
import com.webclues.IPPSManager.Modelclass.FaqResponseItem

import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.MainActivity
import com.webclues.IPPSManager.adapter.FaqAdapter
import com.webclues.IPPSManager.service.APIClient
import com.webclues.IPPSManager.service.checkNetworkState
import com.webclues.IPPSManager.utility.CustomProgress
import com.webclues.IPPSManager.utility.Utility
import com.webclues.IPPSManager.service.ApiInterface
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class FaqFragment : Fragment(), ExpandableListView.OnChildClickListener {


    var elvFaq: ExpandableListView? = null
    private var faqAdapter: FaqAdapter? = null
    private var ExpandableListDetail: java.util.HashMap<String, List<String>> = hashMapOf()
    lateinit var customProgress: CustomProgress
    lateinit var apiInterface: ApiInterface
    var faqlist: ArrayList<FaqResponse> = arrayListOf()
    var width: Int = 0
    val prevExpandPosition = intArrayOf(-1)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_faq, container, false)
        return view;
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview(view)
    }

    private fun initview(view: View) {
        customProgress = CustomProgress.instance
        elvFaq = view.findViewById(R.id.elvFaq)
        elvFaq!!.setOnChildClickListener(this)
        ExpandableListDetail = FaqResponseItem(activity!!).getData()

        if (checkNetworkState(context!!)) {
            faq()
        } else {
            Utility().showOkDialog(
                context!!,
                resources.getString(R.string.app_name),
                resources.getString(R.string.error_internet_ln)
            )
        }
    }

    override fun onChildClick(
        parent: ExpandableListView?,
        v: View?,
        groupPosition: Int,
        childPosition: Int,
        id: Long
    ): Boolean {
        val index = elvFaq!!.getFlatListPosition(
            ExpandableListView.getPackedPositionForChild(
                groupPosition,
                childPosition
            )
        )
        elvFaq!!.setItemChecked(index, true)

        return true
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).txtTitle.setText(resources.getString(R.string.faq))
    }


    private fun faq() {
        customProgress.showProgress(activity!!, getString(R.string.please_wait), false)
        apiInterface = APIClient.getretrofit(activity!!).create(ApiInterface::class.java)
        var call: Call<JsonObject> = apiInterface.faq()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                customProgress.hideProgress()

                var jsonObject = JSONObject(response.body().toString())
                var statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    if (jsonObject.optBoolean("status")) {
                        var data = jsonObject.optJSONObject("data")
                        var faqdata = data.getJSONArray("faqs_list")
                        if (faqdata != null && faqdata.length() > 0) {
                            faqlist.clear()
                            faqlist.addAll(
                                Gson().fromJson(
                                    faqdata.toString(),
                                    Array<FaqResponse>::class.java
                                ).toList()
                            )
                            SetFaqlist(faqlist)
                        }

                    } else {
                        Utility().showOkDialog(
                         context!!,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )

                    }
                } else if (statuscode == 403) {
                    Utility().showInactiveDialog(
                       context!!,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )


                } else {
                    Utility().showOkDialog(
                       context!!,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                }
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

    private fun SetFaqlist(faqlist: ArrayList<FaqResponse>) {


        val listdatachild: java.util.HashMap<String, List<String>> = hashMapOf()
        val listdataheader: ArrayList<String> = arrayListOf()
        for (i in 0..faqlist.size - 1) {
            listdataheader.add(faqlist.get(i).title)

            val listsubmenu: ArrayList<String> = arrayListOf()
            listsubmenu.add(faqlist.get(i).description)
            listdatachild.put(faqlist.get(i).title, listsubmenu)

        }


        faqAdapter =
            FaqAdapter(activity!!, listdataheader, listdatachild)
        elvFaq!!.setAdapter(faqAdapter)
        elvFaq!!.setOnGroupExpandListener({ groupPosition ->
            if (prevExpandPosition[0] >= 0 && prevExpandPosition[0] !== groupPosition) {
                elvFaq!!.collapseGroup(prevExpandPosition[0])

            }

            prevExpandPosition[0] = groupPosition
        })
        elvFaq!!.setOnGroupCollapseListener {
            object : ExpandableListView.OnGroupCollapseListener {
                override fun onGroupCollapse(groupPosition: Int) {
                    elvFaq!!.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.white))

                }

            }
        }

    }
}
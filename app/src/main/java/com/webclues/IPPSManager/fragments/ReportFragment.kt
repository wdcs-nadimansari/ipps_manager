package com.webclues.IPPSManager.fragments


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSManager.Modelclass.SensorResponseItem
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.MainActivity
import com.webclues.IPPSManager.adapter.SensorAdapter
import com.webclues.IPPSManager.service.APIClient
import com.webclues.IPPSManager.utility.CustomProgress
import com.webclues.IPPSManager.utility.TinyDb
import com.webclues.IPPSManager.utility.Utility
import com.webclues.IPPSManager.service.ApiInterface
import kotlinx.android.synthetic.main.activity_sensor_and_sorter.*
import kotlinx.android.synthetic.main.fragment_report.*
import kotlinx.android.synthetic.main.fragment_report.rvSensorHistory
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ReportFragment : Fragment(), View.OnClickListener {
    private var SorterHistorylist: ArrayList<SensorResponseItem>? = arrayListOf()
    private var SensorHistoryList: ArrayList<SensorResponseItem> = ArrayList()
    lateinit var mContext: Context
    lateinit var apiInterface: ApiInterface
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_report, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = activity!!
        initview(view)
    }


    private fun initview(view: View) {
        apiInterface = APIClient.getretrofit(activity!!).create(ApiInterface::class.java)
        rvSensorHistory.layoutManager = LinearLayoutManager(mContext)
        rvSensorHistory.setHasFixedSize(true)
        rvSensorHistory.adapter = SensorAdapter(mContext, SensorHistoryList)
        loadSensorList()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivback -> {
//                startActivity(Intent(activity!!, MainActivity::class.java))
            }
            /* R.id.txtSensor -> {
                 startActivity(Intent(activity!!, SensorAndSorterActivity::class.java))
             }*/
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).txtTitle.setText(resources.getString(R.string.report))
    }

    fun loadSensorList() {

        CustomProgress.instance.showProgress(activity!!, getString(R.string.please_wait), false)
        val call: Call<JsonObject> =
            apiInterface.sensor_list(TinyDb(requireActivity()).getString(Utility().getCompany_ID(requireActivity())))
        call.enqueue(object : Callback<JsonObject> {

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                CustomProgress.instance.hideProgress()
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {

                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val jsonLIst = data.optJSONArray("sensor_list")
                        SensorHistoryList.clear()
                        SensorHistoryList.addAll(Gson().fromJson(jsonLIst.toString(), Array<SensorResponseItem>::class.java).toList())
                        if (SensorHistoryList.size!=0){
                            rvSensorHistory.visibility=View.VISIBLE
                            txtNoRecord.visibility=View.GONE
                        }else{
                            rvSensorHistory.visibility=View.GONE
                            txtNoRecord.visibility=View.VISIBLE
                        }
                        rvSensorHistory.adapter?.notifyDataSetChanged()
                    }


                } else if (statuscode == 403) {

                    Utility().showInactiveDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))

                } else {
                    Utility().showOkDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))
                }


            }


            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Utility().showOkDialog(context!!, resources.getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))
            }

        })
    }
}

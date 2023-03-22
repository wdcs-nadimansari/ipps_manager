package com.webclues.IPPSManager.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSManager.Modelclass.EngineerModel

import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.MainActivity
import com.webclues.IPPSManager.adapter.EngineerAdapter
import com.webclues.IPPSManager.service.APIClient
import com.webclues.IPPSManager.utility.CustomProgress
import com.webclues.IPPSManager.utility.Utility
import com.webclues.IPPSManager.service.ApiInterface
import kotlinx.android.synthetic.main.activity_engineer_list.*
import kotlinx.android.synthetic.main.activity_engineer_list.rvEngneer
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_engineer.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
 class EngineerFragment : Fragment() {


    lateinit var customProgress: CustomProgress
    lateinit var apiInterface: ApiInterface
    lateinit var engineeradapter: EngineerAdapter
    var engineerlist: ArrayList<EngineerModel> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_engineer, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initview()
    }

    private fun initview() {
        customProgress = CustomProgress.instance
        apiInterface = APIClient.getretrofit(context!!).create(ApiInterface::class.java)

        rvEngneer.layoutManager = LinearLayoutManager(context)
        rvEngneer.setHasFixedSize(true)
        engineeradapter = EngineerAdapter(context!!, engineerlist);
        rvEngneer.adapter = engineeradapter

        SetEngineerdata()
    }


    private fun SetEngineerdata() {       //Set Engineerlist data

        customProgress.showProgress(context!!, getString(R.string.please_wait), false)
        val call: Call<JsonObject> = apiInterface.getengineerlist()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                customProgress.hideProgress()
                if (response.code() == 200) {
                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val list = data.optJSONArray("engineers_list")
                        if (list != null && list.length() > 0) {
                            engineerlist.clear()

                            engineerlist.addAll(
                                Gson().fromJson(
                                    list.toString(),
                                    Array<EngineerModel>::class.java
                                ).toList()
                            )
                            engineeradapter.notifyDataSetChanged()

                        } else {
                            rvEngneer.visibility = View.GONE
                            txtNoRecord.visibility = View.VISIBLE
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
                customProgress.hideProgress()
                Utility().showOkDialog(
                    context!!,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).txtTitle.setText(resources.getString(R.string.engineers))
    }

}

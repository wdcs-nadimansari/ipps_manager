package com.webclues.IPPSManager.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSManager.Modelclass.EngineerModel
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.adapter.EngineerAdapter
import com.webclues.IPPSManager.service.APIClient
import com.webclues.IPPSManager.utility.CustomProgress
import com.webclues.IPPSManager.utility.Utility
import com.webclues.IPPSManager.service.ApiInterface
import kotlinx.android.synthetic.main.activity_engineer_list.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EngineerListActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var toolbar: Toolbar
    lateinit var engineeradapter: EngineerAdapter
    lateinit var customProgress: CustomProgress
    lateinit var apiInterface: ApiInterface
    lateinit var context: Context
    var engineerlist: ArrayList<EngineerModel> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_engineer_list)
        context = this
        initview()
    }

    private fun initview() {
        customProgress = CustomProgress.instance
        apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)
//        toolbar = findViewById(R.id.Toolbar)
        setSupportActionBar(toolbar)

        ivBack.setOnClickListener(this)

        rvEngneer.layoutManager = LinearLayoutManager(this)
        rvEngneer.setHasFixedSize(true)
        engineeradapter = EngineerAdapter(context, engineerlist);
        rvEngneer.adapter = engineeradapter

        SetEngineerdata()
    }


    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.ivBack -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    private fun SetEngineerdata() {
        customProgress.showProgress(this, getString(R.string.please_wait), false)
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

                        }
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
                Utility().showOkDialog(
                    context,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )
            }

        })

    }


    override fun onResume() {
        super.onResume()
        txtTitle.setText(resources.getString(R.string.engineers))
        ivmenu.visibility = View.GONE
        ivback.visibility = View.VISIBLE
    }

}

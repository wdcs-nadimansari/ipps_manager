package com.webclues.IPPSManager.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSManager.Modelclass.PositionListReponse
import com.webclues.IPPSManager.Modelclass.companymodel
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.service.APIClient
import com.webclues.IPPSManager.service.checkNetworkState
import com.webclues.IPPSManager.utility.*
import com.webclues.IPPSManager.service.ApiInterface
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var apiInterface: ApiInterface
    var stringcoumpunyarraylist: ArrayList<String>? = arrayListOf()
    var companyarraylist: ArrayList<companymodel>? = arrayListOf()
    var positionarraylist: ArrayList<PositionListReponse>? = arrayListOf()
    var stringpositionarraylist: ArrayList<String>? = arrayListOf()
    lateinit var deviceid: String
    lateinit var fcmtoken: String
    lateinit var companyid: String
    lateinit var positionid: String
    lateinit var tinyDb: TinyDb
    lateinit var customProgress: CustomProgress
    lateinit var context: Context


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        context = this
        initView()
    }

    private fun initView() {
        customProgress = CustomProgress.instance
        deviceid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)


        btnRegister.setOnClickListener(this)
        txtLogin.setOnClickListener(this)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            Log.e("fcm_token", "=" + it.token)
            TinyDb(
                context
            ).putString(Content.FCM_TOKEN, it.token)
            fcmtoken = it.token

        }

        getpositionlist()

        getcompunylist()


    }

    override fun onClick(view: View?) {

        when (view?.id) {
            R.id.btnRegister ->
                if (isvalidate()) {
                    signup()

                }
            R.id.txtLogin -> {
                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                finishAffinity()

            }
        }
    }

    private fun isvalidate(): Boolean {
        var isValid = true

        if (!Utility().isValidFirstName(context, edtFirstName)) {
            isValid = false
        }
        if (!Utility().isValidLastName(context, edtLastName)) {
            isValid = false
        }
        if (!Utility().isEmailValid(context, edtEmailAddress)) {
            isValid = false
        }
        if (!Utility().isPhoneValid(context, edtPhone)) {
            isValid = false
        }
        if (!Utility().isPositionValid(context, edtPosition)) {
            isValid = false
        }
        if (!Utility().isCompanyValid(context, edtCompany)) {
            isValid = false
        }
        if (!checkNetworkState(context)) {
            Utility().showOkDialog(
                context,
                resources.getString(R.string.app_name),
                resources.getString(R.string.error_internet_ln)
            )
            isValid = false
        }

        return isValid
    }

    fun signup() {                 //Signup Api call

        customProgress.showProgress(this, getString(R.string.please_wait), false)
        apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)
        var call: Call<JsonObject> = apiInterface.dosignup(
            edtEmailAddress.text.toString().trim(),
            edtFirstName.text.toString().trim(),
            edtLastName.text.toString().trim(),
            edtPhone.text.toString().trim(),
            companyid,
            deviceid,
            fcmtoken,
            Content.DEVICE_TYPE,
            Content.MANAGER_POSITION,
            positionid
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                customProgress.hideProgress()
                Log.e("Response_code", "=" + response.code());
                if (response.code() == 200) {
                    var jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        var data = jsonObject.optJSONObject("data")
                        Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show()

                        startActivity(Intent(context, LoginActivity::class.java))
                        finish()
                    } else {
                        Utility().showOkDialog(context, resources.getString(R.string.app_name), jsonObject.optString("message"))
                    }
                } else {
                    val jsonObject = JSONObject(response.body().toString())

                    Utility().showOkDialog(context, resources.getString(R.string.app_name), jsonObject.optString("message"))
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                customProgress.hideProgress()
                Utility().showOkDialog(context, resources.getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))

            }
        })

    }


    fun getcompunylist() {                  //Companylist Api call'
        apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)
        var call: Call<JsonObject> = apiInterface.getcompanieslist()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("Response_code", "=" + response.code());
                if (response.code() == 200) {
                    var jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        var data = jsonObject.optJSONObject("data")
                        var coumpanylist = data.optJSONArray("companies_list")
                        if (coumpanylist != null && coumpanylist.length() > 0) {
                            companyarraylist!!.clear()

                            companyarraylist!!.addAll(
                                Gson().fromJson(
                                    coumpanylist.toString(),
                                    Array<companymodel>::class.java
                                ).toList()
                            )

                            Setcoumpunydata(companyarraylist!!)

                        }
                    } else {
                        Utility().showOkDialog(
                            context,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
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

    private fun Setcoumpunydata(coumpunyarraylist: ArrayList<companymodel>) {   //Setcompany data

        for (items in coumpunyarraylist) {
            stringcoumpunyarraylist!!.add(items.company_name)

        }

        edtCompany.setAdapter(
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                stringcoumpunyarraylist!!
            )
        )
        edtCompany.threshold = 0
        edtCompany.keyListener = null
        edtCompany.setOnTouchListener(View.OnTouchListener { v, event ->
            edtCompany.showDropDown()
            Utility.hideKeyboard(this)
            return@OnTouchListener false
        })
        edtCompany.setOnItemClickListener { parent, view, position, id ->
            companyid = coumpunyarraylist.get(position).company_id.toString()
        }

    }


    fun getpositionlist() {             //Position Api call
        customProgress.showProgress(this, getString(R.string.please_wait), false)
        apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)
        var call: Call<JsonObject> = apiInterface.getpositionlist(Content.MANAGER_POSITION)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                customProgress.hideProgress()
                Log.e("Response_code", "=" + response.code());
                if (response.code() == 200) {

                    var jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        var data = jsonObject.optJSONObject("data")
                        var positionlist = data.optJSONArray("positions_list")
                        if (positionlist != null && positionlist.length() > 0) {
                            positionarraylist!!.clear()

                            positionarraylist!!.addAll(
                                Gson().fromJson(
                                    positionlist.toString(),
                                    Array<PositionListReponse>::class.java
                                ).toList()
                            )

                            Setpositiondata(positionarraylist!!)

                        }
                    } else {
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


    private fun Setpositiondata(positionarraylist: ArrayList<PositionListReponse>) {     //Setposition data

        for (item in positionarraylist) {
            stringpositionarraylist!!.add(item.position_name)

        }
        edtPosition.setAdapter(
            ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_dropdown_item, stringpositionarraylist!!
            )
        )
        edtPosition.threshold = 0
        edtPosition.keyListener = null
        edtPosition.setOnTouchListener(View.OnTouchListener { v, event ->
            edtPosition.showDropDown()
            Utility.hideKeyboard(this)
            return@OnTouchListener false
        })
        edtPosition.setOnItemClickListener { parent, view, position, id ->
            positionid = positionarraylist.get(position).position_id.toString()
        }
    }


}

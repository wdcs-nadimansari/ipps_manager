package com.webclues.IPPSManager.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.JsonObject

import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.MainActivity
import com.webclues.IPPSManager.service.APIClient
import com.webclues.IPPSManager.service.checkNetworkState
import com.webclues.IPPSManager.utility.CustomProgress
import com.webclues.IPPSManager.utility.Log
import com.webclues.IPPSManager.utility.Utility
import com.webclues.IPPSManager.service.ApiInterface
import kotlinx.android.synthetic.main.fragment_change_password.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class ChangePasswordFragment : Fragment(), View.OnClickListener {

    lateinit var customProgress: CustomProgress
    lateinit var apiInterface: ApiInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_change_password, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    private fun initView() {
        customProgress = CustomProgress.instance
        btnChangePassword.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnChangePassword -> {
                if (checkValidation()) {
                    changepassword(
                        edtOldPassword.text.toString().trim(),
                        edtNewPassWord.text.toString().trim(),
                        edtConformPass.text.toString().trim()
                    )
                }
            }
        }
    }

    private fun checkValidation(): Boolean {

        var isValid = true
        if (!Utility().isCurrentPasswordValid(context!!, edtOldPassword)) {
            isValid = false
        }
        if (!Utility().isPassvalid(context!!, edtNewPassWord)) {
            isValid = false
        }
        if (!Utility().isConfirmPassvalid(context!!, edtNewPassWord, edtConformPass)) {
            isValid = false
        }
        if (!checkNetworkState(context!!)) {
            Utility().showOkDialog(
                context!!,
                resources.getString(R.string.app_name),
                resources.getString(R.string.error_internet_ln)
            )
            isValid = false
        }
        return isValid
    }


    private fun ClearPassword() {
        edtOldPassword.setText("")
        edtNewPassWord.setText("")
        edtConformPass.setText("")

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).txtTitle.setText(resources.getString(R.string.change_password))
    }

    private fun changepassword(oldpassword: String, newpassword: String, confirmpassword: String) {      //Changepassword Api call
        customProgress.showProgress(activity!!, getString(R.string.please_wait), false)
        apiInterface = APIClient.getretrofit(activity!!).create(ApiInterface::class.java)
        var call: Call<JsonObject> = apiInterface.changepassword(
            oldpassword, newpassword, confirmpassword

        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                customProgress.hideProgress()
                Log.e("Response_code", "=" + response.code());
                var jsonObject = JSONObject(response.body().toString())
                var statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    if (jsonObject.optBoolean("status")) {
                        var data = jsonObject.optJSONObject("data")
                        Utility().showOkDialog(
                          context!!,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                        ClearPassword()
//                        showpopup()
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


}

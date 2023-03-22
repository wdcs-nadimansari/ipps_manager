package com.webclues.IPPSManager.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.application.mApplicationClass
import com.webclues.IPPSManager.service.APIClient
import com.webclues.IPPSManager.service.checkNetworkState
import com.webclues.IPPSManager.utility.*
import com.webclues.IPPSManager.service.ApiInterface
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var apiInterface: ApiInterface
    lateinit var deviceid: String
    lateinit var fcmtoken: String
    lateinit var customProgress: CustomProgress
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        context = this
        initview()
    }

    private fun initview() {
        customProgress = CustomProgress.instance
        deviceid = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        btnlogin.setOnClickListener(this)
        tvregister.setOnClickListener(this)
        tvforgot.setOnClickListener(this)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            Log.e("fcm_token", "=" + it.token)
            TinyDb(context).putString(Content.FCM_TOKEN, it.token)
            fcmtoken = it.token

        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnlogin -> if (isvalidate()) {
                login(
                    edtEmail.text.toString().trim(), deviceid, fcmtoken, Content.DEVICE_TYPE,
                    edtPassword.text.toString().trim(), Content.MANAGER_POSITION, "no"
                )
            }
            R.id.tvregister -> {
                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            }
            R.id.tvforgot -> {
                startActivity(Intent(this@LoginActivity, ForgetPasswordActivity::class.java))
            }
        }
    }

    private fun isvalidate(): Boolean {
        var isValid = true

        if (!Utility().isEmailValid(context, edtEmail)) {
            isValid = false
        }
        if (!Utility().isPasswordValid(context, edtPassword)) {
            isValid = false
        }
        if (!checkNetworkState(context)) {
            Utility().showOkDialog(this, resources.getString(R.string.app_name), resources.getString(R.string.error_internet_ln))
            isValid = false

        }
        return isValid
    }

    fun login(                         //Login Api call
        email: String,
        deviceid: String,
        fcmtoken: String,
        device_type: String,
        password: String,
        operatorposition: String,
        destroy_auth: String
    ) {
        customProgress.showProgress(this, getString(R.string.please_wait), false)
        apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)
        var call: Call<JsonObject> = apiInterface.dologin(
            email,
            deviceid, fcmtoken, device_type,
            password,
            operatorposition, destroy_auth

        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    customProgress.hideProgress()
                    val jsonObject = JSONObject(response.body().toString())
                    val statuscode = jsonObject.optInt("status_code")

                    if (statuscode == 200) {
                        Log.e("Login>>Response>>", response.body().toString());
                        if (jsonObject.optBoolean("status")) {
                            val data = jsonObject.optJSONObject("data")
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show()

                            SetPrefData(data)


                        } else {
                            customProgress.hideProgress()
                            Utility().showOkDialog(context, resources.getString(R.string.app_name), jsonObject.optString("message"))

                        }
                    } else if (statuscode == 402) {
                        customProgress.hideProgress()
                        showpopuplogin(context, resources.getString(R.string.app_name), jsonObject.optString("message"))


                    } else {
                        customProgress.hideProgress()
                        Utility().showOkDialog(context, resources.getString(R.string.app_name), jsonObject.optString("message"))
                    }
                } else {
                    customProgress.hideProgress()
                    Utility().showOkDialog(context, resources.getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                customProgress.hideProgress()
                Utility().showOkDialog(context, resources.getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))

            }
        })

    }

    private fun SetPrefData(data: JSONObject) {

        TinyDb(context).putBoolean(Content.IS_LOGIN, true)
        TinyDb(context).putString(Content.USER_DATA, data.toString())
        TinyDb(context).putString(Content.USER_ID, data.optString(Content.USER_ID))
        TinyDb(context).putString(Content.FIREBASE_UID, data.optJSONObject("firebase_credential").optString(Content.FIREBASE_UID))
        TinyDb(context).putString(Content.AUTORIZATION_TOKEN, "Bearer " + data.optString(Content.AUTORIZATION_TOKEN))
        TinyDb(context).putString(Content.FCM_TOKEN, fcmtoken)
        mApplicationClass.getInstance()?.setFirebaseUser(data)
        startActivity(Intent(context, MainActivity::class.java))
        finish()

    }

    fun showpopuplogin(context: Context, Title: String, Message: String) {
        val dialog = Dialog(context)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.window!!.setBackgroundDrawableResource(R.drawable.rectangle_background);
        dialog.setContentView(R.layout.popup_login)
        dialog.show()

        val txtTitle = dialog.findViewById(R.id.txtTitle) as TextView
        val txtMessage = dialog.findViewById(R.id.txtMessage) as TextView
        val txtOk = dialog.findViewById(R.id.txtOk) as TextView
        val txtCancel = dialog.findViewById(R.id.txtCancel) as TextView

        txtTitle.setText(Title)
        txtMessage.setText(Message)
        txtCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dialog.dismiss()
            }

        })
        txtOk.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dialog.dismiss()
                login(
                    edtEmail.text.toString().trim(),
                    deviceid,
                    fcmtoken,
                    Content.DEVICE_TYPE,
                    edtPassword.text.toString().trim(),
                    Content.MANAGER_POSITION,
                    Content.DESTROY_AUTH
                )
            }
        })

    }
}
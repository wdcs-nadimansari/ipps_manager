package com.webclues.IPPSManager.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSManager.Modelclass.ProfileResponse

import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.activity.EditProfileActivity
import com.webclues.IPPSManager.activity.MainActivity
import com.webclues.IPPSManager.service.APIClient
import com.webclues.IPPSManager.service.checkNetworkState
import com.webclues.IPPSManager.utility.*
import com.squareup.picasso.Picasso
import com.webclues.IPPSManager.service.ApiInterface
import kotlinx.android.synthetic.main.fragment_profile.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment(), View.OnClickListener {

    lateinit var edtFirstName: EditText
    lateinit var edtLastName: EditText
    lateinit var edtEmail: EditText
    lateinit var edtCompany: AutoCompleteTextView
    lateinit var edtPosition: AutoCompleteTextView
    lateinit var edtPhone: EditText
    lateinit var btnEditProfile: Button
    lateinit var customProgress: CustomProgress
    lateinit var apiInterface: ApiInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
        initview(view)
        return view
    }

    private fun initview(view: View) {
        customProgress = CustomProgress.instance
        edtFirstName = view.findViewById(R.id.edtFirstName)
        edtLastName = view.findViewById(R.id.edtLastName)
        edtEmail = view.findViewById(R.id.edtEmail)
        edtCompany = view.findViewById(R.id.edtCompany)
        edtPosition = view.findViewById(R.id.edtPosition)
        edtPhone = view.findViewById(R.id.edtPhone)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnEditProfile.setOnClickListener(this)
        setData()
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnEditProfile -> {
                startActivityForResult(
                    Intent(context!!, EditProfileActivity::class.java),
                    Content.REQ_EDIT_PROFILE
                )

            }
        }
    }


    fun setData() {

        edtFirstName.keyListener = null
        edtLastName.keyListener = null
        edtEmail.keyListener = null
        edtPhone.keyListener = null
        edtCompany.keyListener = null
        edtPosition.keyListener = null


        edtFirstName.setTextColor(ContextCompat.getColor(context!!, R.color.fontColorBlack60))
        edtLastName.setTextColor(ContextCompat.getColor(context!!, R.color.fontColorBlack60))
        edtEmail.setTextColor(ContextCompat.getColor(context!!, R.color.fontColorBlack60))
        edtPhone.setTextColor(ContextCompat.getColor(context!!, R.color.fontColorBlack60))
        edtCompany.setTextColor(ContextCompat.getColor(context!!, R.color.fontColorBlack60))
        edtPosition.setTextColor(ContextCompat.getColor(context!!, R.color.fontColorBlack60))


        if (checkNetworkState(context!!)) {

            getprofiledetails(TinyDb(context!!).getString(Content.USER_ID))
        } else {
            Utility().showOkDialog(
                context!!,
                resources.getString(R.string.app_name),
                resources.getString(R.string.error_internet_ln)
            )
        }


    }

    private fun getprofiledetails(userid: String) {      //Profile Api call
        customProgress.showProgress(activity!!, getString(R.string.please_wait), false)
        apiInterface = APIClient.getretrofit(activity!!).create(ApiInterface::class.java)
        var call: Call<JsonObject> = apiInterface.getprofiledetails(
            userid
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                customProgress.hideProgress()
                var jsonObject = JSONObject(response.body().toString())
                var statuscode = jsonObject.optInt("status_code")
                Log.e("Response_code", "=" + response.code());
                if (statuscode == 200) {
                    if (jsonObject.optBoolean("status")) {
                        var data = jsonObject.optJSONObject("data")
                        var profileResponse =
                            Gson().fromJson(data.toString(), ProfileResponse::class.java)
                        SetProfileData(profileResponse)

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

    private fun SetProfileData(profileResponse: ProfileResponse) {    //Set profile data
        Picasso.get().load(profileResponse.profile_pic)
            .placeholder(R.drawable.ic_placeholder_profile).error(R.drawable.ic_placeholder_profile)
            .into(ivProfile)
        edtFirstName.setText(profileResponse.first_name)
        edtLastName.setText(profileResponse.last_name)
        edtEmail.setText(profileResponse.email)
        edtPhone.setText(profileResponse.phone)
        edtPosition.setText(profileResponse.position.position_name)
        edtCompany.setText(profileResponse.company.company_name)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Content.REQ_EDIT_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                getprofiledetails(TinyDb(activity!!).getString(Content.USER_ID))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).txtTitle.setText(getString(R.string.profile))
    }

}

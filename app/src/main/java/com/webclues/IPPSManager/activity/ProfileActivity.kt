package com.webclues.IPPSManager.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.KeyListener
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.utility.Utility
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity(),View.OnClickListener {

    var positionList: ArrayList<String> = arrayListOf("Position 1", "Position 2", "Position 3")
    var companyList: ArrayList<String> = arrayListOf("Company 1", "Company 2", "Company 3")
    var isEdit: Boolean = false
    lateinit var mContext: Context
    lateinit var keyListner: KeyListener
    lateinit var emailListner: KeyListener
    lateinit var numkeyListner: KeyListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initview()
    }

    private fun initview() {


        btnEditProfile.tag = "edit"
        setData()
        ivBack.setOnClickListener(this)
        btnEditProfile.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                startActivity(Intent(this@ProfileActivity, MainActivity::class.java))
            }
            R.id.btnEditProfile -> {
                if (btnEditProfile.tag.equals("edit")) {
                    isEdit = true
                    btnEditProfile.tag = "save"
                    setEditMode()
                } else {
                    if (checkValidation()) {
                        callSaveProfile()
                    }
                }
            }
        }
    }

    private fun checkValidation(): Boolean {
        var isValid = true
        if (!Utility().isGeneralValidation(this, edtFirstName)) {
            isValid = false
        } else if (!Utility().isEmailValid(this, edtEmail)) {
            isValid = false
        } else if (!Utility().isPhoneValid(this, edtPhone)) {
            isValid = false
        } else if (!Utility().isPositionValid(this, edtPosition)) {
            isValid = false
        } else if (!Utility().isCompanyValid(this, edtCompany)) {
            isValid = false
        }
        return isValid
    }

    fun setData() {
        keyListner = edtFirstName.keyListener
        numkeyListner = edtPhone.keyListener
        emailListner = edtEmail.keyListener
        edtFirstName.keyListener = null
        edtEmail.keyListener = null
        edtPhone.keyListener = null
        edtCompany.keyListener = null
        edtPosition.keyListener = null
        /* edtFirstName.isEnabled = false
        edtEmail.isEnabled = false
        edtPhone.isEnabled = false
        edtCompany.isEnabled = false
        edtPosition.isEnabled = false*/
        ivEditImage.visibility = View.GONE

        edtFirstName.setTextColor(ContextCompat.getColor(mContext, R.color.fontColorBlack60))
        edtEmail.setTextColor(ContextCompat.getColor(mContext, R.color.fontColorBlack60))
        edtPhone.setTextColor(ContextCompat.getColor(mContext, R.color.fontColorBlack60))
        edtCompany.setTextColor(ContextCompat.getColor(mContext, R.color.fontColorBlack60))
        edtPosition.setTextColor(ContextCompat.getColor(mContext, R.color.fontColorBlack60))

        edtFirstName.setText("Charles Xeviers")
        edtEmail.setText("charles@gmail.com")
        edtPhone.setText("98987654312")
        edtCompany.setText("X men")
        edtPosition.setText("HOD")

    }

    fun setEditMode() {
        /* edtFirstName.isEnabled = true
         edtEmail.isEnabled = true
         edtPhone.isEnabled = true
         edtCompany.isEnabled = true
         edtPosition.isEnabled = true*/
        edtFirstName.keyListener = keyListner
        edtEmail.keyListener = emailListner
        edtPhone.keyListener = numkeyListner

        ivEditImage.visibility = View.VISIBLE

        edtFirstName.setTextColor(ContextCompat.getColor(mContext, R.color.fontColorBlack))
        edtEmail.setTextColor(ContextCompat.getColor(mContext, R.color.fontColorBlack))
        edtPhone.setTextColor(ContextCompat.getColor(mContext, R.color.fontColorBlack))
        edtCompany.setTextColor(ContextCompat.getColor(mContext, R.color.fontColorBlack))
        edtPosition.setTextColor(ContextCompat.getColor(mContext, R.color.fontColorBlack))

        btnEditProfile.text = getString(R.string.save)
        btnEditProfile.tag = "save"
        frmProfile.setOnClickListener(this)
        val companyAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item, companyList
        )
        val positionAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_dropdown_item, positionList
        )
        edtPosition.setAdapter(positionAdapter)
        edtCompany.setAdapter(companyAdapter)
        edtPosition.threshold = 0
        edtCompany.threshold = 0
        edtPosition.keyListener = null
        edtCompany.keyListener = null
        edtPosition.setOnTouchListener(View.OnTouchListener { v, event ->
            edtPosition.showDropDown()
            Utility.hideKeyboard(this)
            return@OnTouchListener false
        })

        edtCompany.setOnTouchListener(View.OnTouchListener { v, event ->
            edtCompany.showDropDown()
            Utility.hideKeyboard(this)
            return@OnTouchListener false
        })
    }

    fun callSaveProfile() {}
}

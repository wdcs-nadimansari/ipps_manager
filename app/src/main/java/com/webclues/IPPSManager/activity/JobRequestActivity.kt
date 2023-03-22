package com.webclues.IPPSManager.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.zxing.integration.android.IntentIntegrator
import com.webclues.IPPSManager.Modelclass.QRcodeModel
import com.webclues.IPPSManager.R
import com.webclues.IPPSManager.adapter.AddPhotosAdapter
import com.webclues.IPPSManager.service.APIClient
import com.webclues.IPPSManager.service.checkNetworkState
import com.webclues.IPPSManager.utility.Content
import com.webclues.IPPSManager.utility.CustomProgress
import com.webclues.IPPSManager.utility.Log
import com.webclues.IPPSManager.utility.Utility
import com.webclues.IPPSManager.Modelclass.LocationModel
import com.webclues.IPPSManager.Modelclass.MachineModel
import com.webclues.IPPSManager.Modelclass.ProblemModel
import com.webclues.IPPSManager.service.ApiInterface
import kotlinx.android.synthetic.main.activity_job_request.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class JobRequestActivity : AppCompatActivity() {


    var imageList: ArrayList<String> = ArrayList()
    var locationList: ArrayList<LocationModel> = arrayListOf()
    var stringlocationlist: ArrayList<String> = arrayListOf()
    var problemlist: ArrayList<ProblemModel> = arrayListOf()
    var stringproblemlist: ArrayList<String> = arrayListOf()
    var machinelist: ArrayList<MachineModel> = arrayListOf()
    var stringmachinelist: ArrayList<String> = arrayListOf()
    lateinit var qrscan: IntentIntegrator
    lateinit var txtScanQRCode: TextView
    lateinit var imagepath: String
    lateinit var FileName: String

    lateinit var locationid: String
    lateinit var problemid: String
    lateinit var machineid: String
    lateinit var mContext: Context
    lateinit var imageAdapter: AddPhotosAdapter
    lateinit var apiInterface: ApiInterface
    lateinit var customProgress: CustomProgress

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_request)
        mContext = this
        initView()
    }

    fun initView() {
        txtScanQRCode = findViewById(R.id.txtScanQRCode)
        qrscan = IntentIntegrator(this)
        customProgress = CustomProgress.instance
        apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)

        imageList.add("+")
        rvPhotos.layoutManager = LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false)
//        Collections.reverse(imageList)

        imageAdapter = AddPhotosAdapter(this, imageList, object : AddPhotosAdapter.AddPhotos {
            override fun addphoto() {
                if (imageList.size - 1 < Content.MAX_IMAGE_LIMIT) {
                    if (isPermissionGranted()) {
                        AlertDialogForImagePicker()
                    }
                    txtImageError.visibility = View.GONE
                } else {
                    txtImageError.visibility = View.VISIBLE
                    txtImageError.setText(R.string.error_msg_max_image)
                }

            }

            override fun removephoto(position: Int) {
                imageList.removeAt(position)
                imageAdapter.notifyItemRemoved(position)
                imageAdapter.notifyItemRangeChanged(position, imageAdapter.itemCount)
//                imageAdapter.notifyItemRemoved(position)
                if (imageList.size < Content.MAX_IMAGE_LIMIT) {
                    txtImageError.visibility = View.GONE
                    if (!imageList.contains("+")) {
                        imageList.add("+")
                    }
                }

            }

        })

        rvPhotos.adapter = imageAdapter
        imageAdapter.notifyDataSetChanged()


        btnSend.setOnClickListener({
            if (isValidate()) {
                addjobrequest()
            }
        })

        edtDescription.setOnTouchListener(View.OnTouchListener { v, event ->

            v.parent.requestDisallowInterceptTouchEvent(true)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(
                    false
                )
            }
            false
        })

        ivBack.setOnClickListener({
            onBackPressed()
        })

        txtScanQRCode.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                qrscan.initiateScan()
            }
        })


        getlocationlist()

        getproblemlist()

        edtAddComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(edtAddComment.hasFocus()){
                    scrollUpToMyWantedPosition()
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }
/*
    fun isValidate(): Boolean {
        var valid = false

        when {
            (imageList.size == 1) -> {
                txtImageError.visibility = View.VISIBLE
                txtImageError.setText(R.string.error_msg_image_blank)
            }
            edtJobTitle.text.isNullOrBlank() -> {
                edtJobTitle.error = getString(R.string.error_msg_job_title_blank)
            }
            edtDescription.text.isNullOrBlank() -> {
                edtDescription.error = getString(R.string.error_msg_description_blank)
            }
            edtMachine.text.isNullOrBlank() -> {
                edtMachine.error = getString(R.string.error_msg_machine_blank)
            }
            edtLocation.text.isNullOrBlank() -> {
                edtLocation.error = getString(R.string.error_msg_location_blank)
            }
            edtProblem.text.isNullOrBlank() -> {
                edtProblem.error = getString(R.string.error_msg_problem_blank)
            }
            edtAddComment.text.isNullOrBlank() -> {
                edtAddComment.error = getString(R.string.error_msg_comment_blank)
            }
            else -> valid = true
        }


        return valid;
    }*/


    fun isValidate(): Boolean {
        var valid = true

/*
        if (imageList.size == 1) {
            txtImageError.visibility = View.VISIBLE
            txtImageError.setText(R.string.error_msg_image_blank)
            valid = false
        }
*/
        /*       if (edtJobTitle.text.isNullOrBlank()) {
                   edtJobTitle.error = getString(R.string.error_msg_job_title_blank)
                   valid = false
               }
               if (edtDescription.text.isNullOrBlank()) {
                   edtDescription.error = getString(R.string.error_msg_description_blank)
                   valid = false
               }*/
        if (edtLocation.text.isNullOrBlank()) {
            edtLocation.error = getString(R.string.error_msg_location_blank)
            valid = false
        }
        if (edtMachine.text.isNullOrBlank()) {
            edtMachine.error = getString(R.string.error_msg_machine_blank)
            valid = false
        }
        if (edtProblem.text.isNullOrBlank()) {
            edtProblem.error = getString(R.string.error_msg_problem_blank)
            valid = false
        }

        if (!checkNetworkState(this)) {
            Utility().showOkDialog(this, resources.getString(R.string.app_name), resources.getString(R.string.error_internet_ln))
            valid = false
        }
        return valid
    }

    private fun getlocationlist() {
        locationList.clear()
        val call: Call<JsonObject> = apiInterface.getlocationlist()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data");
                        val list = data.optJSONArray("locations_list")
                        if (list != null && list.length() > 0) {

                            locationList.addAll(Gson().fromJson(list.toString(), Array<LocationModel>::class.java).toList())
                            stringlocationlist.clear()
                            Setlocationdata(locationList)
                        }
                    } else {

                        Utility().showOkDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))
                    }

                } else if (statuscode == 403) {
                    Utility().showInactiveDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))
                } else {
                    Utility().showOkDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Utility().showOkDialog(mContext, resources.getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))
            }

        })
    }

    private fun Setlocationdata(locationarraylist: ArrayList<LocationModel>) {

        for (items in locationarraylist) {
            stringlocationlist.add(items.location_name)
        }
        edtLocation.setAdapter(
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stringlocationlist))
        edtLocation.threshold = 0
        edtLocation.keyListener = null
        edtLocation.setOnTouchListener(View.OnTouchListener { v, event ->
            edtLocation.showDropDown()
            Utility.hideKeyboard(this)
            return@OnTouchListener false
        })

        edtLocation.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                locationid = locationarraylist.get(position).location_id.toString()
                edtMachine.setText("")
                edtProblem.setText("")
                stringmachinelist.clear()
                getmachinelist(locationid)
            }

        })
    }


    private fun getmachinelist(locationid: String) {
        machinelist.clear()
        val call: Call<JsonObject> = apiInterface.getmachinelist(locationid)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200) {
                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data");
                        val list = data.optJSONArray("machines_list")
                        if (list != null && list.length() > 0) {

                            machinelist.addAll(Gson().fromJson(list.toString(), Array<MachineModel>::class.java).toList())
                            SetMachinelist(machinelist)
                        }
                    } else {
                        Utility().showOkDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))
                    }

                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                Utility().showOkDialog(mContext, resources.getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))
            }

        })
    }

    private fun SetMachinelist(machinelist: ArrayList<MachineModel>) {

        for (items in machinelist) {
            stringmachinelist.add(items.machine_name)
        }
        edtMachine.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stringmachinelist!!))
        edtMachine.threshold = 0
        edtMachine.keyListener = null
        edtMachine.setOnTouchListener(View.OnTouchListener { v, event ->
            Log.e("Machinelist", "=" + stringmachinelist)
            if (stringmachinelist.size != 0) {
                edtMachine.showDropDown()
            }
            Utility.hideKeyboard(this)
            return@OnTouchListener false
        })
        edtMachine.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                machineid = machinelist.get(position).machine_id.toString()
            }

        })

    }

    private fun getproblemlist() {
        problemlist.clear()
        val call: Call<JsonObject> = apiInterface.getproblemlist()
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200) {
                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val list = data.optJSONArray("problems_list")
                        if (list != null && list.length() > 0) {

                            problemlist.addAll(Gson().fromJson(list.toString(), Array<ProblemModel>::class.java).toList())
                            stringproblemlist.clear()
                            setproblemdata(problemlist)

                        }
                    } else {
                        Utility().showOkDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utility().showOkDialog(mContext, resources.getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))
            }

        })
    }

    private fun setproblemdata(problemlist: ArrayList<ProblemModel>) {
        for (items in problemlist) {
            stringproblemlist.add(items.problem_name)
        }
        edtProblem.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, stringproblemlist!!))
        edtProblem.threshold = 0
        edtProblem.keyListener = null
        edtProblem.setOnTouchListener(View.OnTouchListener { v, event ->
            edtProblem.showDropDown()
            Utility.hideKeyboard(this)
            return@OnTouchListener false
        })
        edtProblem.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                problemid = problemlist.get(position).problem_id.toString()
            }

        })
    }

    private fun addjobrequest() {

        customProgress.showProgress(this, getString(R.string.please_wait), false)
        val location: RequestBody =
                locationid.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val machine: RequestBody =
                machineid.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val problemid: RequestBody =
                problemid.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val comment: RequestBody = edtAddComment.text.toString().trim()
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val Imagelist = arrayOfNulls<MultipartBody.Part>(imageList.size)
        for (i in 0..imageList.size - 1) {

            if (!imageList.get(i).contains("+")) {
                val file = File(imageList[i])
                if (file.exists()) {
                    val imagebody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    Imagelist[i] = MultipartBody.Part.createFormData("image[]", file.name, imagebody)
                    Log.e("editImage>>filepath", file.path)
                } else {
                    file.mkdirs()
                    val imagebody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    Imagelist[i] = MultipartBody.Part.createFormData("image[]", file.name, imagebody)
                    Log.e("editImage>>filepath", file.path)
                }
            }
            /*      val filepath =
                      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)*/


            Log.e("locatonid", "=" + locationid)
            Log.e("machineid", "=" + machineid)
            Log.e("problemid", "=" + problemid)


        }


        val call: Call<JsonObject> =
            apiInterface.addjob(machine, problemid, location, comment, Imagelist)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    customProgress.hideProgress()
                    if (jsonObject.optBoolean("status")) {

                        for (i in 0..imageList.size - 1) {
                            if (!imageList.get(i).contains("+")) {
                                val fdelete: File = File(imagepath)
                                if (fdelete.exists()) {
                                    fdelete.delete()
                                }
                            }
                        }

                        var data = jsonObject.optJSONObject("data")
                        startActivity(Intent(mContext, RequestSucessActivity::class.java))
                    } else {
                        customProgress.hideProgress()
                        Utility().showOkDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))
                    }
                } else if (statuscode == 403) {
                    customProgress.hideProgress()
                    Utility().showInactiveDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))
                } else {
                    customProgress.hideProgress()
                    Utility().showOkDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                customProgress.hideProgress()
                Utility().showOkDialog(mContext, resources.getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))
            }

        })

    }


    /**
     * @ Function Name      : isPermissionGranted
     * @ Function Params    : none
     * @ Function Purpose   : to check that if permission of camera and storage is granted or not.
     * if not then show popup to grant the permission.
     */
    fun isPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 1
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                AlertDialogForImagePicker()
            } else {
                showPermissionNotAllowed()
            }
        }
    }

    private fun showPermissionNotAllowed() {
        var dialog = AlertDialog.Builder(this)

        dialog.setMessage(resources.getString(R.string.lbl_grant_required_permissions_line))
            .setPositiveButton("Yes") { dialog, which ->
                goToPermissionSetting()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
                finish()
            }

        dialog.show()
    }

    private fun goToPermissionSetting() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 1000)
    }

    private fun AlertDialogForImagePicker() {
        Utility.hideKeyboard(mContext)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.image_picker_popup, null)
        //        View dialogView = inflater.inflate(R.layout.pop   _up_picker, null);
        val mpopup = PopupWindow(dialogView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true) //Creation of popup
        mpopup.animationStyle = android.R.style.Animation_Dialog
        (dialogView.findViewById<View>(R.id.txtcamera) as TextView).setOnClickListener {
            ImagePicker.with(this)
                .crop(1f, 1f)                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .cameraOnly()
                .start(Content.REQ_PICK_IMAGE)

            mpopup.dismiss()
        }
        (dialogView.findViewById<View>(R.id.txtGallery) as TextView).setOnClickListener {
            ImagePicker.with(this)
                .crop(1f, 1f)                //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .galleryOnly()
                .start(Content.REQ_PICK_IMAGE)
            mpopup.dismiss()
        }
        (dialogView.findViewById<View>(R.id.txtCancel) as TextView).setOnClickListener { mpopup.dismiss() }
        mpopup.showAtLocation(dialogView, Gravity.CENTER, 0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Content.REQ_PERMISSION) {
            isPermissionGranted()
        } else if (requestCode == Content.REQ_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data
//                imgProfile.setImageURI(fileUri)


                //You can get File object from intent
                val file: File? = ImagePicker.getFile(data)

                //You can also get File Path from intent
                Log.e("Image path-->", ImagePicker.getFilePath(data).toString())
                FileName = fileUri?.lastPathSegment!!
                imagepath = ImagePicker.getFilePath(data).toString()
//                imageList.add(imagepath)
//                imageAdapter.notifyDataSetChanged()

                imageList.add(0, imagepath)
                Log.e("Image List size-->", imageList.size)
                if (imageList.size.equals(Content.MAX_IMAGE_LIMIT + 1)) {
                    imageList.remove("+")
                }
                imageAdapter.notifyDataSetChanged()

            }
        } else {
            val result =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show()

                } else {
                    Barcodescan(result.contents)
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun Barcodescan(Qrcode: String) {
        val call: Call<JsonObject> = apiInterface.Scanqrcode(Qrcode)
        call.enqueue(object : Callback<JsonObject> {


            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        val qRcodeModel = Gson().fromJson(data.toString(), QRcodeModel::class.java)
                        Setdata(qRcodeModel)
                        getlocationlist()
                    } else {
                        Utility().showOkDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))
                    }
                } else if (statuscode == 403) {
                    Utility().showInactiveDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message"))

                } else {
                    Utility().showOkDialog(mContext, resources.getString(R.string.app_name), jsonObject.optString("message")
                    )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Utility().showOkDialog(mContext, resources.getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))

            }

        })

    }

    private fun Setdata(data: QRcodeModel) {

        locationid = data.location.location_id.toString()
        edtLocation.setText(data.location.location_name)

        machineid = data.machine.machine_id.toString()
        edtMachine.setText(data.machine.machine_name)

        getmachinelist(locationid)


    }


    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
    }

    private fun scrollUpToMyWantedPosition() =
        with(scrollview) {
            postDelayed({
                smoothScrollBy(0, btnSend.y.toInt())
            }, 200)
        }
}

package com.webclues.IPPSManager.activity

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import com.webclues.IPPSManager.R
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSManager.Modelclass.ChatMemberModel
import com.webclues.IPPSManager.Modelclass.GroupListModel
import com.webclues.IPPSManager.Modelclass.UserRespone
import com.webclues.IPPSManager.adapter.MessageListAdapter
import com.webclues.IPPSManager.application.mApplicationClass
import com.webclues.IPPSManager.service.APIClient
import com.webclues.IPPSManager.utility.*
import com.webclues.IPPSManager.utility.ChatConstants.*
import com.webclues.IPPSManager.service.ApiInterface
import kotlinx.android.synthetic.main.activity_chat.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class ChatActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var messageAdapter: MessageListAdapter
    lateinit var mContext: Context
    lateinit var apiInterface: ApiInterface
    private val chatList = ArrayList<DataSnapshot>()

    //    private HashMap<String, String> userNameHashMap = new HashMap<>();
    private val userNameHashMap: HashMap<String, ChatMemberModel> = HashMap<String, ChatMemberModel>()
    var userID: String? = ""
    var myUid: String? = ""

    companion object {
        var groupId: String? = null
    }

    var currentIndex = 50
    var endHasBeenReached: Boolean = true
    var isFromNotifications = false
    var Imagepath: String = ""
    lateinit var groupModel: GroupListModel

    private val messageListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.e("messageListener", "messageListener")

            chatList.clear()
            val chats = dataSnapshot.children
            for (message in chats) {
//                if (message.child(message).value.toString().length > 0) {
                chatList.add(0, message)
//                }
            }
            messageAdapter.notifyDataSetChanged()
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private val countUpdateReceiver: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            try {
                val reference = FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(mContext)).child(chatGroups).child(groupModel?.id.toString())
                reference.child(Users).child(myUid!!).child(unreadCount).setValue(0)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        mContext = this
        var intent = intent
        if (intent.extras != null) {
            groupModel = Gson().fromJson(intent.getStringExtra("chat_group"), GroupListModel::class.java)

        }
        userID = mApplicationClass.getPreference()?.getString(Content.USER_ID)
        myUid = mApplicationClass.getPreference()?.getString(Content.FIREBASE_UID)

        groupId = groupModel.id
//        Picasso.get().load(UserProfile!!).into(ivProfile)
        val reference = FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(mContext)).child(chatGroups).child(groupModel?.id!!)
        reference.child(Users).child(myUid.toString()).child(unreadCount).setValue(0)
        reference.child(Users).child(myUid!!).child(unreadCount).addValueEventListener(countUpdateReceiver)

        try {
            if (mApplicationClass.notificationModels != null && mApplicationClass.notificationModels.size > 0) {
                for (i in mApplicationClass.notificationModels.indices) {
                    if (mApplicationClass.notificationModels[i].chatListModel?.id.equals(groupModel?.id, true)) {
                        (applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancel(resources.getString(R.string.app_name), mApplicationClass.notificationModels[i].id)
                        mApplicationClass.notificationModels.removeAt(i)
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        initView()
    }

    fun initView() {


        txtUserName.text = groupModel.group_name


        rvChat.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, true)
        messageAdapter = MessageListAdapter(mContext, chatList)
        rvChat.adapter = messageAdapter

        ivBack.setOnClickListener(this)
        ivSend.setOnClickListener(this)
        ivGallery.setOnClickListener(this)

        setData()
        FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(mContext)).child(chatGroups).child(groupId!!).child(messages).limitToLast(currentIndex).addValueEventListener(messageListener)

    }

    private fun setData() {
        CustomProgress.instance.showProgress(this, getString(R.string.please_wait), false)
        FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(mContext))
            .child(chatGroups)
            .child(groupId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    pd.dismiss()
                    CustomProgress.instance.hideProgress()
                    if (dataSnapshot.value != null) {
                        try {
                            val user = dataSnapshot.child(Users).children

                            for (snapshot in user) {
                                if (snapshot.key != myUid) {
                                    var member = ChatMemberModel()
                                    member.user_fid = snapshot.key
                                    member.user_id = snapshot.child(user_id).value.toString()
                                    member.first_name = snapshot.child(first_name).value.toString()
                                    member.last_name = snapshot.child(last_name).value.toString()
                                    member.user_type = snapshot.child(user_type).value.toString()
                                    member.email = snapshot.child(email).value.toString()
                                    member.profile_pic = snapshot.child(profile_pic).value.toString()
                                    groupModel.chatMembers.add(member)
                                    userNameHashMap[snapshot.key!!] = member
                                }
                            }
                            txtMemberCount.text = groupModel.chatMembers.size.toString() + " participants"
                            messageAdapter.notifyDataSetChanged()
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                Utility.hideSoftKeyBoard(this)
                onBackPressed()
            }
            R.id.ivGallery -> {
                if (isPermissionGranted()) {
                    AlertDialogForImagePicker()
                }
            }
            R.id.ivSend -> {
                if (!edtMessage.text.isNullOrBlank()) {
/*
                    messageList.add(
                        ChatMessageItem(
                            Content.CHAT_TEXT_TYPE,
                            edtMessage.text.toString()
                        )
                    )
                    messageAdapter.notifyDataSetChanged()
                    edtMessage.text?.clear()*/
                    sendMessage(edtMessage.text.toString().trim(), Content.CHAT_TEXT_TYPE, Imagepath)
                }else{
                    Utility().showOkDialog(mContext, resources.getString(R.string.app_name),
                        resources.getString(R.string.error_message_blank))

                }
//                Utility.closeKeyboard(this)
            }
        }
    }

    private fun sendMessage(messageText: String, type: String, image: String) {
        val reference = FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(mContext)).child(chatGroups).child(groupModel?.id.toString())
        val userData = Gson().fromJson(TinyDb(this).getString(Content.USER_DATA), UserRespone::class.java)
        try {
            val messageObj = JSONObject()

            messageObj.put(messageType, type)
            messageObj.put(message, messageText)
            messageObj.put(messageImage, image)
            messageObj.put(messageTime, ServerValue.TIMESTAMP)
            messageObj.put(sender_id, userID)
            messageObj.put(sender_Uid, myUid)
            messageObj.put(sender_first_name, userData.first_name)
            messageObj.put(sender_last_namee, userData.last_name)
            messageObj.put(sender_profile_pic, userData.profile_pic)


            reference.child(lastUpdate).setValue(ServerValue.TIMESTAMP)
            if(type == Content.CHAT_IMAGE_TYPE){
                reference.child(mLastMessages).setValue("Image")
            }else{
                reference.child(mLastMessages).setValue(messageText)
            }

            reference.child(messages).push().setValue(mApplicationClass.getInstance()?.toMap(messageObj)) { databaseError, databaseReference ->
                for (key in userNameHashMap.keys) {

                    reference
                        .child(Users)
                        .child(key)
                        .child(deviceMeta)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot1: DataSnapshot) {
                             /*   val devices = dataSnapshot1.child().children
                                for (snapshot in devices) {*/
                                    if (dataSnapshot1.hasChild(deviceType) && dataSnapshot1.child(deviceType).getValue().toString().equals(Content.DEVICE_TYPE, ignoreCase = true)) {

                                        try {
                                            val data = JsonObject()
                                            val notification = JsonObject()

                                            data.addProperty(message, messageText)
                                            data.addProperty(messageType, type)
                                            data.addProperty(messageImage, image)
                                            data.addProperty("chatMessage", true)
                                            data.addProperty("type", "chat")
                                            data.addProperty("groupId", groupModel?.id)
                                            data.addProperty("group_name", groupModel?.group_name)
                                            data.addProperty(sender_Uid, myUid)
                                            data.addProperty(sender_first_name, userData.first_name)
                                            data.addProperty(sender_last_namee, userData.last_name)
                                            Log.e("Send Notification to--", userNameHashMap[key]?.first_name + "" + userNameHashMap[key]?.last_name)
                                            mApplicationClass.getInstance()?.sendNotifications(dataSnapshot1.child(fcmToken).value.toString(), data, notification)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }else {
                                        try {
                                            val data = JsonObject()
                                            val notification = JsonObject()
                                            if (type == Content.CHAT_IMAGE_TYPE) {
                                                notification.addProperty("body", "Image")
                                            } else {
                                                notification.addProperty("body", messageText)
                                            }
                                            notification.addProperty("title", "New Message in " + groupModel?.group_name + " from " + userData.first_name)

                                            data.addProperty(message, messageText)
                                            data.addProperty(messageType, type)
                                            data.addProperty(messageImage, image)
                                            data.addProperty("chatMessage", true)
                                            data.addProperty("type", "chat")
                                            data.addProperty("groupId", groupModel?.id)
                                            data.addProperty("group_name", groupModel?.group_name)
                                            data.addProperty(sender_Uid, myUid)
                                            data.addProperty(sender_first_name, userData.first_name)
                                            data.addProperty(sender_last_namee, userData.last_name)
                                            Log.e("Send Notification to--", userNameHashMap[key]?.first_name + "" + userNameHashMap[key]?.last_name)
                                            mApplicationClass.getInstance()?.sendNotifications(dataSnapshot1.child(fcmToken).value.toString(), data, notification)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
//                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })

                }
            }
            edtMessage.text?.clear()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun isPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(
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
        val mpopup = PopupWindow(
            dialogView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        ) //Creation of popup
        mpopup.animationStyle = android.R.style.Animation_Dialog
        (dialogView.findViewById<View>(R.id.txtcamera) as TextView).setOnClickListener {
            ImagePicker.with(this)
                .crop(
                    1f,
                    1f
                )                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .cameraOnly()
                .start(Content.REQ_PICK_IMAGE)

            mpopup.dismiss()
        }
        (dialogView.findViewById<View>(R.id.txtGallery) as TextView).setOnClickListener {
            ImagePicker.with(this)
                .crop(
                    1f,
                    1f
                )                //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(
                    1080,
                    1080
                )    //Final image resolution will be less than 1080 x 1080(Optional)
                .galleryOnly()
                .start(Content.REQ_PICK_IMAGE)
            mpopup.dismiss()
        }
        (dialogView.findViewById<View>(R.id.txtCancel) as TextView).setOnClickListener { mpopup.dismiss() }
        mpopup.showAtLocation(dialogView, Gravity.CENTER, 0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
                Imagepath = ImagePicker.getFilePath(data).toString()
//                imageList.add(fileUri.toString())
//                imageAdapter.notifyDataSetChanged()

//                chatList.add(ChatMessageItem(Content.CHAT_IMAGE_TYPE, Imagepath!!))
//                sendMessage("",Content.CHAT_IMAGE_TYPE, Imagepath!!)
                uploadImage()
                messageAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun uploadImage() {
        CustomProgress.instance.showProgress(this, getString(R.string.please_wait), false)
        apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)



        var profilepic: MultipartBody.Part? = null

        if (Imagepath!!.trim().length > 0) {
            val file: RequestBody = File(Imagepath).asRequestBody("multipart/form-data".toMediaTypeOrNull())
            profilepic = MultipartBody.Part.createFormData("image", File(Imagepath).name, file)
        }

        val call: Call<JsonObject> = apiInterface.upload_image(
            profilepic
        )
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                CustomProgress.instance.hideProgress()
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    if (jsonObject.optBoolean("status")) {
                        Log.e("Upload Image>>Responce>>", jsonObject.toString())
                        val data = jsonObject.optJSONObject("data")
                        if (data.optString("path").isNotBlank()) {
                            sendMessage("", Content.CHAT_IMAGE_TYPE, data.optString("path"))
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

                CustomProgress.instance.hideProgress()
                Utility().showOkDialog(mContext, resources.getString(R.string.app_name), getString(R.string.error_something_is_wrong_ln))

            }

        })
    }


}
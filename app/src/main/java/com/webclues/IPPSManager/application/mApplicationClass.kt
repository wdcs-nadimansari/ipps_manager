package com.webclues.IPPSManager.application

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSManager.Interface.OnChatListUpdate
import com.webclues.IPPSManager.Modelclass.GroupListModel
import com.webclues.IPPSManager.Modelclass.NotificationModel
import com.webclues.IPPSManager.utility.*
import com.webclues.IPPSManager.utility.ChatConstants.*
import com.webclues.IPPSManager.service.ApiInterface
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class mApplicationClass : MultiDexApplication(), ChatConstants {
    companion object {
        private var mInstance: mApplicationClass? = null
        private var myUid: String? = null
        private var mSharedPrefers: TinyDb? = null
        var mChatGroups: ArrayList<GroupListModel> = ArrayList()
        var mChatMeta = HashMap<String, Int>()
        var isChatLoading = false
        public var onChatlistUpdate: OnChatListUpdate? = null
        public val notificationModels: ArrayList<NotificationModel> = ArrayList()
        fun getInstance(): mApplicationClass? {
            return mInstance
        }

        fun getPreference(): TinyDb? {
            return mSharedPrefers;
        }
    }

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        mSharedPrefers = TinyDb(mInstance!!)
        MultiDex.install(mInstance)
        myUid = mSharedPrefers?.getString(Content.FIREBASE_UID)

        startDataBaseSynnc()
    }

    //    Firebase Methods
    private fun startDataBaseSynnc() {
        FirebaseApp.initializeApp(this)

        if (FirebaseDatabase.getInstance() != null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            FirebaseDatabase.getInstance().reference.keepSynced(true)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                if (mSharedPrefers?.getString(Content.FIREBASE_UID) != TinyDb.DEFULT_STRING) {
                    getMyChatList()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }, 1000)


    }

    fun setFirebaseUser(data: JSONObject) {
        Log.e("SetFirebaseUser", "Call")
        try {

            val user = JSONObject()
            user.put(first_name, data.getString("first_name"))
            user.put(last_name, data.getString("last_name"))
            user.put(user_id, data.getString("user_id"))
            user.put(profile_pic, data.getString("profile_pic"))
            user.put(email, data.getString("email"))
            user.put(user_type, data.getString("user_type"))
            user.put(unreadCount, 0)
            var device = JSONObject()
            device.put(fcmToken, mSharedPrefers?.getString(Content.FCM_TOKEN))
            device.put(deviceType,Content.DEVICE_TYPE)
            user.put(deviceMeta, device)
            FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(this))
                .child(chatGroups).child(staffGroup).child(Users)
                .child(data.getJSONObject("firebase_credential").getString("firebase_uid"))
                .setValue(toMap(user))

            FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(this))
                .child(chatGroups).child(operatorGroup).child(Users)
                .child(data.getJSONObject("firebase_credential").getString("firebase_uid"))
                .setValue(toMap(user))

        } catch (e: Exception) {
            e.printStackTrace()
        }
        clearData()
        getMyChatList()
    }

    fun clearData() {
        notificationModels.clear()
        mChatGroups.clear()
        mChatMeta.clear()
    }

    fun EditFirebaseUser(data: JSONObject) {
        Log.e("EditFirebaseUser", "Call")
        try {
            var ref: DatabaseReference = FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(this))
                .child(chatGroups).child(staffGroup).child(Users)
                .child(data.getJSONObject("firebase_credential").getString("firebase_uid"))
            ref.child(first_name).setValue(data.optString("first_name"))
            ref.child(last_name).setValue(data.optString("last_name"))
            ref.child(email).setValue(data.optString("email"))
            ref.child(profile_pic).setValue(data.getString("profile_pic"))

            var ref2: DatabaseReference = FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(this))
                .child(chatGroups).child(operatorGroup).child(Users)
                .child(data.getJSONObject("firebase_credential").getString("firebase_uid"))
            ref2.child(first_name).setValue(data.optString("first_name"))
            ref2.child(last_name).setValue(data.optString("last_name"))
            ref2.child(email).setValue(data.optString("email"))
            ref2.child(profile_pic).setValue(data.getString("profile_pic"))
        } catch (e: Exception) {
            Log.e("Edit firebase error:", e.message)
        }

    }

    fun removeFCMToken() {
        FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(this))
            .child(chatGroups).child(staffGroup).child(Users)
            .child(mSharedPrefers?.getString(Content.FIREBASE_UID)!!)
            .child(deviceMeta).child(fcmToken).removeValue()

        FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(this))
            .child(chatGroups).child(operatorGroup).child(Users)
            .child(mSharedPrefers?.getString(Content.FIREBASE_UID)!!)
            .child(deviceMeta).child(fcmToken).removeValue()
//        clearData()
//        clearDatabase()
    }


    fun getMyChatList() {
        try {
            isChatLoading = true
            FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(this))
                .child(chatGroups)
//                .orderByChild("/$Users/$myUid/$active").equalTo(true)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {


//                        Log.e("getMyChatListdataSnapshot", "--" + dataSnapshot.value)

                        if (dataSnapshot?.value != null) {
                            val mGroups = dataSnapshot.children
                            for (grps in mGroups) {
                                setMetaListeners(grps)
                            }
                            Log.e("Chat List--", Gson().toJson(mChatGroups))
                            isChatLoading = false
//                            getUserMeta(0, mChatGroups, myUid!!)
                        } else {
                            Log.e("Chat List--", "Not Exist")
                            isChatLoading = false
                            onChatlistUpdate?.UpdateList(mChatGroups)
                        }


                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getChatList(): ArrayList<GroupListModel>? {
        return mChatGroups
    }

    private fun setMetaListeners(dataSnapshot: DataSnapshot) {
        if (mSharedPrefers?.getString(Content.FIREBASE_UID) != TinyDb.DEFULT_STRING) {
            FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(this))
                .child(chatGroups)
                .child(dataSnapshot.key!!)
                .child(Users)
                .child(myUid!!)
                .child(unreadCount)
                .addValueEventListener(unreadCountValueEventListener)
            FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(this))
                .child(chatGroups)
                .child(dataSnapshot.key!!)
                .child(mLastMessages)
                .addValueEventListener(lastMessagesValueEventListener)
            FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(this))
                .child(chatGroups)
                .child(dataSnapshot.key!!)
                .child(lastUpdate)
                .addValueEventListener(lastUpdateValueEventListener)
        }
        Log.e("setMetaListeners", "--" + dataSnapshot.value)
        try {
            val model = GroupListModel()
            model.id = dataSnapshot.key.toString()
            if (model.id == staffGroup) {
                model.group_name = "Staff Group"
            } else {
                model.group_name = "Operator Group"
            }
            if(dataSnapshot.hasChild(lastUpdate) && dataSnapshot.child(lastUpdate).value.toString().isNotBlank()) {
                model.lastUpdate = dataSnapshot.child(lastUpdate).value.toString().toLong()
            }else{
                model.lastUpdate=0
            }
            if (dataSnapshot.hasChild(mLastMessages)) {
                if (dataSnapshot.child(mLastMessages).value.toString().isBlank()) {
                    model.lastMessage = "Image"
                } else {
                    model.lastMessage = dataSnapshot.child(mLastMessages).value.toString()
                }

            }
            if(dataSnapshot.child(Users).child(myUid!!).hasChild(unreadCount)){
                model.unreadCount = dataSnapshot.child(Users).child(myUid!!).child(unreadCount).value.toString().toInt()
                mChatMeta[dataSnapshot.key!!] = dataSnapshot.child(Users).child(myUid!!).child(unreadCount).value.toString().toInt()
            }else{
                model.unreadCount=0
            }
            model.has_message = dataSnapshot.hasChild(messages)
            /*val mUserIds = dataSnapshot.child(Users).children
            for (Ids in mUserIds) {
                if (!Ids.key.equals(myUid, ignoreCase = true)) {
                    val member = ChatMemberModel()
                    member.first_name = Ids.child(first_name).value.toString()
                    member.last_name = Ids.child(last_name).value.toString()
                    member.email = Ids.child(email).value.toString()
                    member.profile_pic = Ids.child(profile_pic).value.toString()
                    member.user_id = Ids.child(user_id).value.toString()
                    member.user_fid = Ids.key.toString()
                    member.user_type = Ids.child(user_type).value.toString()
                    model.chatMembers.add(member)
                }
            }*/


            var exist = false
            for (i in mChatGroups.indices) {
                if (mChatGroups[i].id.equals(model.id)) {
                    exist = true
                }
            }
            if (!exist) {
                mChatGroups.add(model)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val unreadCountValueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.e("unreadCountValueEventListener", "-->" + dataSnapshot.value.toString())
            updateCount(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private val lastMessagesValueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.e("lastMessagesValueEventListener", "-->" + dataSnapshot.value.toString())
            updateLastMessages(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private val lastUpdateValueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            Log.e("unreadCountValueEventListener", "-->" + dataSnapshot.value.toString())
            updateLastUpdate(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {}
    }

    private fun updateCount(dataSnapshot: DataSnapshot?) {
        var updatePosi = -1
        if (!isChatLoading && dataSnapshot != null && dataSnapshot.value != null) {
            for (i in mChatGroups.indices) {
                if (mChatGroups[i].id.equals(dataSnapshot.ref.parent!!.key, true)) {
                    if (mChatGroups[i].unreadCount != dataSnapshot.value.toString().toInt()) {
                        updatePosi = i
                    }
                }
            }
            if (dataSnapshot.value.toString().toInt() > 0) {
                if (updatePosi > -1) {
                    val model: GroupListModel = mChatGroups[updatePosi]
                    mChatGroups.removeAt(updatePosi)
                    model.unreadCount = dataSnapshot.value.toString().toInt()
                    /* for (i in 0 until model.chatMembers.size){
                         if(model.chatMembers[i].user_fid.equals(myUid)){
                             model.chatMembers[i].unreadCount=dataSnapshot.value.toString().toInt()
                         }
                     }*/
                    model.has_message = true
                    mChatGroups.add(0, model)
                }
            } else {
                if (updatePosi > -1) {
                    mChatGroups[updatePosi].unreadCount = 0
                    if (dataSnapshot.value.toString().toInt() > 0) {
                        mChatGroups[updatePosi].has_message = true
                    }
                }
            }
            onChatlistUpdate?.UpdateList(mChatGroups)
            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("countReceiver"))
        }
    }

    private fun updateLastMessages(dataSnapshot: DataSnapshot?) {
        var updatePosi = -1
        if (!isChatLoading && dataSnapshot != null && dataSnapshot.value != null) {
            for (i in mChatGroups.indices) {
                Log.e("key", "--" + dataSnapshot.ref.parent!!.key)
                if (mChatGroups[i].id.equals(dataSnapshot.ref.parent!!.key, true)) {
                    updatePosi = i
                }
            }
            if (updatePosi > -1) {
                val model: GroupListModel = mChatGroups[updatePosi]
                mChatGroups.removeAt(updatePosi)
                model.lastMessage = dataSnapshot.value.toString()
                if (dataSnapshot.value.toString().trim { it <= ' ' }.length > 0) {
                    model.has_message = true
                }
                mChatGroups.add(0, model)
            }
            onChatlistUpdate?.UpdateList(mChatGroups)
        }
    }

    private fun updateLastUpdate(dataSnapshot: DataSnapshot?) {
        var updatePosi = -1
        if (!isChatLoading && dataSnapshot != null && dataSnapshot.value != null) {
            for (i in mChatGroups.indices) {
                if (mChatGroups[i].id.equals(dataSnapshot.ref.parent!!.key, true)) {
                    updatePosi = i
                }
            }
            if (updatePosi > -1) {
                if(dataSnapshot.value.toString().isNotBlank()){
                    val model: GroupListModel = mChatGroups[updatePosi]
                    mChatGroups.removeAt(updatePosi)
                    model.lastUpdate = dataSnapshot.value.toString().toLong()
                    mChatGroups.add(0, model)
                }

            }
            onChatlistUpdate?.UpdateList(mChatGroups)
        }
    }

    fun clearDatabase() {
        notificationModels.clear()
        mChatGroups.clear()
        mChatMeta.clear()
        FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(this))
            .child(chatGroups)
//            .orderByChild("/$groupMembers/$myUid/$active").equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot?.value != null) {
                        val mGroups = dataSnapshot.children
                        for (grps in mGroups) {
                            FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(baseContext as Context))
                                .child(chatGroups)
                                .child(dataSnapshot.key!!)
                                .child(Users)
                                .child(myUid!!)
                                .child(unreadCount)
                                .removeEventListener(unreadCountValueEventListener)
                            FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(baseContext as Context))
                                .child(chatGroups)
                                .child(dataSnapshot.key!!)
                                .child(mLastMessages)
                                .child(myUid!!)
                                .removeEventListener(lastMessagesValueEventListener)
                            FirebaseDatabase.getInstance().reference.child(Utility().getCompany_ID(baseContext as Context))
                                .child(chatGroups)
                                .child(dataSnapshot.key!!)
                                .child(lastUpdate)
                                .removeEventListener(lastUpdateValueEventListener)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

    }

    fun sendNotifications(notificationTo: String, data: JsonObject?, notification: JsonObject?) {
        val obj = JsonObject()
        try {
            obj.addProperty("to", notificationTo)
            obj.addProperty("priority", "high")
            obj.addProperty("content_available", true)

            if (data != null) {
                obj.add("data", data)
            }
            if (notification != null) {
                val mIterator = notification.keySet()
                try {
                    if (notification != null && mIterator.size > 0) {
                        obj.addProperty("mutable_content", true)
                        obj.add("notification", notification)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val okClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(60 + 60.toLong(), TimeUnit.SECONDS)
            .readTimeout(60 + 60.toLong(), TimeUnit.SECONDS)
            .addNetworkInterceptor(
                Interceptor { chain ->
                    val request: Request
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                        .addHeader("Authorization", "key=" + Content.SERVER_KEY)
                        .addHeader("Content-Type", "application/json")
                    request = requestBuilder.build()
                    chain.proceed(request)
                })
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://fcm.googleapis.com/fcm/")
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: ApiInterface = retrofit.create(ApiInterface::class.java)
        Log.e("Notification Object==",obj.toString())
        val call: Call<JsonObject?>? = service.sendFCM(obj)
        call?.enqueue(object : Callback<JsonObject?> {
            override fun onResponse(call1: Call<JsonObject?>, response: retrofit2.Response<JsonObject?>) {
                try {
                    val jo = JSONObject(response.body().toString())
                    if (jo.getInt("failure") == 1 && jo.getInt("success") == 0) {
                        Log.e("notificationTo", " -- $notificationTo")
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call1: Call<JsonObject?>, t: Throwable) {
                Log.e("onResponse", t.fillInStackTrace().toString() + "")
            }
        })
    }

    fun toMap(`object`: JSONObject): Map<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        val keysItr = `object`.keys()
        while (keysItr.hasNext()) {
            val key = keysItr.next()
            var value = `object`[key]
            if (value is JSONObject) {
                value = toMap(value)
            }
            map[key] = value
        }
        return map
    }
}
package com.webclues.IPPSManager.Modelclass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import retrofit2.http.Field

data class JobStatusModel(

    val created_at: Long? = 0,

    val engineer_name: String? = null,

    var images: Images? = null,

    val job_id: Int = 0,


    val job_status: Int = 0,


    val location: String? = null,

    val machine_name: String? = null,

    val priority: Int = 0,

    val problem: String? = null
)

class Images(
    val image_id: Int,
    val image_url: String
)
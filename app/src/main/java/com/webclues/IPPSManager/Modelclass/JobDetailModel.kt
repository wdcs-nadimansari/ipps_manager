package com.webclues.IPPSManager.Modelclass

data class JobDetailModel(
    val comment: String,
    val engineer_id: String,
    val engineer_name: String,
    val images: List<Image>,
    val job_duration: String,
    val job_id: Int,
    val job_status: Int,
    val location_name: String,
    val problem_name: String,
    val machine_name: String,
    val created_user_name: String,
    val priority: Int,
    val created_time: Long,
    val decline_reason: String,
    val engineer_comment: String,
    val incomplete_reason: String,
    val declined_by: String,
    val declined_by_user: String,
    val job_start_time: String
)

data class Image(
    val image_id: String,
    val image_url: String
)
package com.webclues.IPPSManager.Modelclass

data class EngineerModel(
    val company: EngineerCompany,
    val email: String,
    val engineer_id: Int,
    val engineer_name: String,
    val phone: String,
    val position: EngineerPosition,
    val profile_pic: String,
    val work_order_count: Int
)
data class EngineerCompany(
    val company_id: Int,
    val company_name: String
)

data class EngineerPosition(
    val position_id: Int,
    val position_name: String
)


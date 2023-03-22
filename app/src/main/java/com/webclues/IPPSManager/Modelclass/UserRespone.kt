package com.webclues.IPPSManager.Modelclass

data class UserRespone(
    val company: Companylist,
    val email: String,
    val firebase_credential: Firebase_Credential,
    val first_name: String,
    val last_name: String,
    val oauth_token: String,
    val phone: String,
    val position: Positionlist,
    val profile_pic: String,
    val user_id: Int,
    val user_type: Int
)
data class Companylist(
    val company_id: Int,
    val company_name: String
)
data class Positionlist(
    val position_id: Int,
    val position_name: String
)
data class Firebase_Credential(
    val firebase_email: String,
    val firebase_password: String,
    val firebase_uid: String
)
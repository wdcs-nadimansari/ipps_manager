package com.webclues.IPPSManager.Modelclass

data class ProfileResponse(
    val company: Company,
    val email: String,
    val firebase_credential: FirebaseCredential,
    val first_name: String,
    val last_name: String,
    val phone: String,
    val position: Position,
    val profile_pic: String,
    val user_id: Int
)
data class Company(
    val company_id: Int,
    val company_name: String
)
data class FirebaseCredential(
    val firebase_email: String,
    val firebase_password: String,
    val firebase_uid: String
)
data class Position(
    val position_id: Int,
    val position_name: String
)
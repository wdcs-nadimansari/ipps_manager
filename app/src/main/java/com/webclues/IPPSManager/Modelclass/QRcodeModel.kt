package com.webclues.IPPSManager.Modelclass

data class QRcodeModel(
    val location: QRLocation,
    val machine: QRMachine

)

data class QRMachine(
    val machine_id: Int,
    val machine_name: String
)

data class QRLocation(
    val location_id: Int,
    val location_name: String
)
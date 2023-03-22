package com.webclues.IPPSManager.Modelclass

data class BeaconSensorModel(
    val temperature: String,
    val timestamp: Long,
    val x_axis: String,
    val y_axis: String,
    val z_axis: String,
    val value: String,
    val bdt: String
)
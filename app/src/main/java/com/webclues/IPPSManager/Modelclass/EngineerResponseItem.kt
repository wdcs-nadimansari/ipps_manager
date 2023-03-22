package com.webclues.IPPSManager.Modelclass

class EngineerResponseItem {
     var EngneerProfile: Int? = null
     var EngineerName: String? = null
     var EngineerOccupation: String? = null

    constructor(EngneerProfile: Int, EngineerName: String, EngineerOccupation: String) {
        this.EngneerProfile = EngneerProfile
        this.EngineerName = EngineerName
        this.EngineerOccupation = EngineerOccupation
    }
}
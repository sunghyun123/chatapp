package com.example.chatapplication

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null

    var benchWeight: String? = null
    var squatWeight:String? = null
    var pullUpCount:String? = null
    var level: String? = null
    var profileImage:String? = null

    constructor(){}

    constructor(name: String?, email: String?, uid: String?, benchWeight: String?, squtWeight : String?, pullUpCount : String?,level: String?,profileImage: String){
        this.name = name
        this.email = email
        this.uid = uid
        this.benchWeight = benchWeight
        this.squatWeight = squtWeight
        this.pullUpCount = pullUpCount
        this.level = level
        this.profileImage = profileImage
    }
}
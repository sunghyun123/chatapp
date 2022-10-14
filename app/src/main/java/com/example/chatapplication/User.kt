package com.example.chatapplication

class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var benchWeight_: String? = null
    var squtWeight_:String? = null
    var pullUpCount_:String? = null
    var level_: String? = null
    var profileImage_:String? = null
    constructor(){}

    constructor(name: String?, email: String?, uid: String?, benchWeight: String?, squtWeight : String?, pullUpCount : String?,level: String?,profileImage_: String?){
        this.name = name
        this.email = email
        this.uid = uid
        this.benchWeight_ = benchWeight
        this.squtWeight_ = squtWeight
        this.pullUpCount_ = pullUpCount
        this.level_ = level
        this.profileImage_ = profileImage_
    }
}
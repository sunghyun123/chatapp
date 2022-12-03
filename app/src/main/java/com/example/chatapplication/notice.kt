package com.example.chatapplication

class notice {
    var title: String? = null
    var Contents: String? = null
    var Image:String? = null
    var uid : String? = null
    var likes: Int? = null
    var number: String? = null
    constructor(){}

    constructor(title : String?,
                Contents: String?,
                Image: String?,
                uid: String?,
                likes: Int?,
                number: String?
    ){
        this.title = title
        this.Contents = Contents
        this.Image = Image
        this.uid = uid
        this.likes = likes
        this.number = number
    }

    fun uplikes() {
        var a = this.likes;
        this.likes = a!!.toInt() + 1
    }
}
package com.example.chatapplication

class notice {
    var title: String? = null
    var Contents: String? = null
    var Image:String? = null
    var likes: Int? = null
    constructor(){}

    constructor(title : String?,
                Contents: String?,
                Image: String?,
    ){
        this.title = title
        this.Contents = Contents
        this.Image = Image
        this.likes = 0
    }

    fun uplikes() {
        var a = this.likes;
        this.likes = a!!.toInt() + 1
    }
}
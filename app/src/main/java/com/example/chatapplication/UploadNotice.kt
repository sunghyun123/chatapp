package com.example.chatapplication

import android.content.Intent
import android.icu.text.CaseMap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class UploadNotice : AppCompatActivity() {
    private  lateinit var  mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var  storage: FirebaseStorage
    private lateinit var selectImage: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        mAuth = FirebaseAuth.getInstance() //파이어베이스에 데이터를 추가하거나 조회하기 위해 변수 선언(정의)
        storage = FirebaseStorage.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_loader)
    }

    private  fun WritePost(
        Title:String,
        Contents: String,
        uid: String,
        Image : String
    ) {
        addUserToDatabase(
            Title,
            Contents,
            uid,
            Image,
        )
        val intent = Intent(this@UploadNotice, nboardActivity::class.java)
        startActivity(intent)
    }

    private fun addUserToDatabase(
        Title:String,
        Contents: String,
        uid: String,
        Image : String,
    ){
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("notice").child(uid).setValue(notice(
            Title,
            Contents,
            Image,
        ))
    }

}





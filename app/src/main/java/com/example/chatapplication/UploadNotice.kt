package com.example.chatapplication

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.icu.text.CaseMap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime

class UploadNotice : AppCompatActivity() {
    private  lateinit var  mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var  storage: FirebaseStorage
    private lateinit var selectImage: Uri
    private lateinit var uid : String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        mAuth = FirebaseAuth.getInstance() //파이어베이스에 데이터를 추가하거나 조회하기 위해 변수 선언(정의)
        storage = FirebaseStorage.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_loader)
        uid = mAuth.currentUser!!.uid;
        var a = System.currentTimeMillis().toString();
        UploadPost(intent.getStringExtra("title").toString(),intent.getStringExtra("content").toString(),intent.getStringExtra("img").toString(),a)
    }


    private  fun UploadPost(Title : String?,
                            Contents : String?,
                            Img : String?,
                            key: String?){

        if(Img != "null") {
            selectImage = Img!!.toUri()
            uploadPhoto(selectImage,key)
        }
        addUserToDatabase(
            Title,
            Contents,
            Img,
            key
        )
        val intent = Intent(this@UploadNotice, nboardActivity::class.java)
        finish()
        startActivity(intent)
    }



    private fun uploadPhoto(selectImage: Uri, key: String?) {// 파이어베이스에 이미지 파일 올리고, 올린파일 다시 받아오는 코드.
        val fileName = mAuth.currentUser?.uid.toString()
        storage = FirebaseStorage.getInstance()
        storage.reference.child("article/notice").child(uid).child(key.toString())
            .putFile(selectImage)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // 파일 업로드에 성공했기 때문에 파일을 다 시 받아 오도록 해야함
                    storage.reference.child("article/notice").child(uid).child(key.toString()).downloadUrl
                }
            }
        //회원가입이 완료된 유저의 데이터베이스를 생성한다
    }

    //회원가입이 완료된 유저의 데이터베이스를 생성한다
    @SuppressLint("NewApi")
    private fun addUserToDatabase(
        Title : String?,
        Contents : String?,
        Img : String?,
        key: String?
    ){
        mDbRef = FirebaseDatabase.getInstance().getReference()

            mDbRef.child("notice").child(uid).child(key.toString()).setValue(notice(Title,Contents,Img,uid,0,key.toString()))



    }
}





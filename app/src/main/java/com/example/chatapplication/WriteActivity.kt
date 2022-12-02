package com.example.chatapplication

import android.content.Intent
import android.icu.text.CaseMap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class WriteActivity : AppCompatActivity() {
    private  lateinit var  mAuth: FirebaseAuth
    private lateinit var selectImage: Uri
    private lateinit var uid : String
    private lateinit var  Title : EditText
    private lateinit var  contents : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        mAuth = FirebaseAuth.getInstance() //파이어베이스에 데이터를 추가하거나 조회하기 위해 변수 선언(정의)
        uid = mAuth.currentUser!!.uid;

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)

    }


}





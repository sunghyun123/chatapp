package com.example.chatapplication

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SignUp3 : AppCompatActivity() {

    private lateinit var level:EditText// 운동수준
    private lateinit var btnSignUp: Button

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up3)
        btnSignUp = findViewById(R.id.btnSignUp)
        level = findViewById(R.id.edt_Level)
        supportActionBar?.hide() // 상태 표시줄 숨기기
        btnSignUp.setOnClickListener {
            myStartActivity()
        }
    }
    private fun myStartActivity() {
        val nextIntent = Intent(this@SignUp3, SignUpLast::class.java)
        nextIntent.putExtra("uName2", intent.getStringExtra("uName1").toString())
        nextIntent.putExtra("uEmail2", intent.getStringExtra("uEmail1").toString())
        nextIntent.putExtra("uPw2", intent.getStringExtra("uPw1").toString())
        nextIntent.putExtra("photo2", intent.getStringExtra("photo1").toString())
        nextIntent.putExtra("benchWight2",intent.getStringExtra("benchWight1").toString())
        nextIntent.putExtra("squatWight2", intent.getStringExtra("squatWight1").toString())
        nextIntent.putExtra("pullUpCount2", intent.getStringExtra("pullUpCount1").toString())
        nextIntent.putExtra("level2", level.text.toString())
        startActivity(nextIntent)
    }
}
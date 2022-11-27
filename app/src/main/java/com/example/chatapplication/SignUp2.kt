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

class SignUp2 : AppCompatActivity() {


    private lateinit var benchWight: EditText//벤치 무게
    private lateinit var squatWight: EditText//스쿼트 무게
    private lateinit var pullUpCount: EditText// 풀업 갯수
    private lateinit var btnSignUp: Button


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //상태 표시 줄을 감춘다(로그아웃기능 감춤)
        setContentView(R.layout.activity_sign_up2)
        benchWight = findViewById(R.id.edt_BenchPower)
        squatWight = findViewById(R.id.edt_SqutPower)
        pullUpCount = findViewById(R.id.edt_PullUpPower)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            myStartActivity()
        }
    }

    private fun myStartActivity() {
        if (benchWight.text.isNotEmpty() && squatWight.text.isNotEmpty() && pullUpCount.text.isNotEmpty()) {
            val nextIntent = Intent(this@SignUp2, SignUp3::class.java)
            nextIntent.putExtra("uName1", intent.getStringExtra("uName").toString())
            nextIntent.putExtra("uEmail1", intent.getStringExtra("uEmail").toString())
            nextIntent.putExtra("uPw1", intent.getStringExtra("uPw").toString())
            nextIntent.putExtra("photo1", intent.getStringExtra("photo").toString())
            nextIntent.putExtra("benchWight1", benchWight.text.toString())
            nextIntent.putExtra("squatWight1", squatWight.text.toString())
            nextIntent.putExtra("pullUpCount1", pullUpCount.text.toString())
            startActivity(nextIntent)
        }
        else{
            Toast.makeText(this,"there is empty space", Toast.LENGTH_SHORT).show()
        }
    }
}
package com.example.chatapplication

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.net.URI


class popActivity : AppCompatActivity() {

    private lateinit var edtName: TextView
    private lateinit var benchWight: TextView
    private lateinit var squatWeight: TextView
    private lateinit var pullUpCount: TextView
    private lateinit var level:TextView
    private lateinit var back: Button
    private lateinit var ProfileImg : ImageView
    private lateinit var file : File
    private lateinit var chat: Button

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop)
        supportActionBar?.hide() //상태 표시 줄을 감춘다(로그아웃기능 감춤)
        //selectImage 받은 데이터로 초기화
        supportActionBar?.hide() // 상태 표시줄 숨기기

        edtName = findViewById(R.id.edt_name)
        benchWight = findViewById(R.id.edt_BenchPower)
        squatWeight = findViewById(R.id.edt_SqutPower)
        pullUpCount = findViewById(R.id.edt_PullUpPower)
        level = findViewById(R.id.edt_Level)
        back = findViewById(R.id.back)
        chat = findViewById(R.id.chat)
        ProfileImg = findViewById(R.id.ProfileImg)
        //회원가입 버튼에 온클릭 이벤트 추가(입력된 이름과 이메일과 비밀번호를 String으로 받아와 signup 함수 실행)

        edtName.text = "이름 : " + intent.getStringExtra("name").toString()
        benchWight.text = "벤치 프레스무게 : " + intent.getStringExtra("benchWeight").toString()
        squatWeight.text = "스쿼트 무게 : " + intent.getStringExtra("squatWeight").toString()
        pullUpCount.text = "풀업 무게 : " +intent.getStringExtra("pullUpCount").toString()
        level.text = "운동 경력 : " + intent.getStringExtra("level").toString()

        Glide.with(this)
            .load(File(intent.getStringExtra("img").toString()))
            .into(ProfileImg)
        back.setOnClickListener {
            finish()

        }
        chat.setOnClickListener {
            var uidk = intent.getStringExtra("uid")
            var namek = intent.getStringExtra("name")
            val intent = Intent(this@popActivity, ChattActivity::class.java)
            intent.putExtra("uid",uidk)
            intent.putExtra("name",namek )
            startActivity(intent)
            finish()
        }

    }

}


//회원가입이 완료된 유저의 데이터베이스를 생성한다

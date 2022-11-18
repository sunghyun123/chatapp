package com.example.chatapplication

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class ForceTerminationService : Service() {
    private lateinit var  mAuth: FirebaseAuth // 파이어베이스 유저관련 접속하기위한 변수
    private  lateinit var mDbRef: DatabaseReference// 파이어베이스 리얼타임베이스 접근하기위한 변수

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onTaskRemoved(rootIntent: Intent) { //핸들링 하는 부분
        Log.e("Error", "onTaskRemoved - 강제 종료 $rootIntent")
        mAuth = FirebaseAuth.getInstance()// 나자신의 유저정보 m이 my의 약자다

        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child( mAuth.currentUser?.uid.toString()).child("state").setValue("OFF")// 온오프 표시

        Toast.makeText(this, "onTaskRemoved ", Toast.LENGTH_SHORT).show()
        stopSelf() //서비스 종료
    }
}




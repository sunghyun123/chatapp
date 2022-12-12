package com.example.chatapplication

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.chatapplication.ChattActivity
import com.example.chatapplication.R
import java.io.File

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
    var waitTime = 0L

    override fun onBackPressed() {
        if(System.currentTimeMillis() - waitTime >=1500 ) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finish() // 액티비티 종료
        }
    }

}


//회원가입이 완료된 유저의 데이터베이스를 생성한다

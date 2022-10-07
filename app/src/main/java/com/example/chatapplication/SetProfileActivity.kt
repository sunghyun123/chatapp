package com.example.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SetProfileActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var benchWight: EditText
    private lateinit var squtWight: EditText
    private lateinit var pullUpCount: EditText
    private lateinit var level:EditText
    private lateinit var btnSignUp: Button
    private  lateinit var  mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)

        supportActionBar?.hide() // 상태 표시줄 숨기기

        mAuth = FirebaseAuth.getInstance() //파이어베이스에 데이터를 추가하거나 조회하기 위해 변수 선언(정의)

        //findViewById(R.id.~)를 하게되면 xml 레이아웃에서 id가 ~인 객체에 접근하여 값을 변경할 수 있다
        edtName = findViewById(R.id.edt_name)
        benchWight = findViewById(R.id.edt_BenchPower)
        squtWight = findViewById(R.id.edt_SqutPower)
        pullUpCount = findViewById(R.id.edt_PullUpPower)
        level = findViewById(R.id.edt_Level)
        btnSignUp = findViewById(R.id.btnSignUp)

        //회원가입 버튼에 온클릭 이벤트 추가(입력된 이름과 이메일과 비밀번호를 String으로 받아와 signup 함수 실행)
        btnSignUp.setOnClickListener {
            val name = edtName.text.toString()
            var user =  mAuth.currentUser;
            val email = user?.email.toString();
            val benchWights = benchWight.text.toString()
            val squtWights = squtWight.text.toString()
            val pullUpCounts = pullUpCount.text.toString()
            val levels = level.text.toString()
            signUp(name,email, benchWights, squtWights, pullUpCounts, levels)
        }
    }
    // signup 함수, createUserWithEmailAndPassword(매크로같은거일듯) 입력한 이름과 이메일, 비밀번호를 firebase에 전달 후 성공 유무를 확인하여 화면을 전환시키거나 메세지를 출력한다
    private  fun signUp(name:String, email: String,benchWeight: String,squtWeight: String,pullUpCount:String,level:String){

                    addUserToDatabase(
                        name,
                        email,
                        mAuth.currentUser?.uid!!,
                        benchWeight,
                        squtWeight,
                        pullUpCount,
                        level,

                    )
                    val intent = Intent(this@SetProfileActivity, MainActivity::class.java)
                    finish()
                    startActivity(intent)

    }


    //회원가입이 완료된 유저의 데이터베이스를 생성한다
    private fun addUserToDatabase(
        name: String,
        email: String,
        uid: String,
        benchWeight: String,
        squtWeight: String,
        pullUpCount: String,
        level:String
    ){
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(User(name,email,uid,benchWeight,squtWeight,pullUpCount,level))
    }
    }


    //회원가입이 완료된 유저의 데이터베이스를 생성한다

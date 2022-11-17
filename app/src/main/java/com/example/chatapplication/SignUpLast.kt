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
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SignUpLast : AppCompatActivity() {


    private lateinit var btnSignUp: Button
    private  lateinit var  mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var  storage: FirebaseStorage
    private lateinit var selectImage:Uri
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_last)

        supportActionBar?.hide() // 상태 표시줄 숨기기

        mAuth = FirebaseAuth.getInstance() //파이어베이스에 데이터를 추가하거나 조회하기 위해 변수 선언(정의)
        storage = FirebaseStorage.getInstance()
        //findViewById(R.id.~)를 하게되면 xml 레이아웃에서 id가 ~인 객체에 접근하여 값을 변경할 수 있다

        //회원가입 버튼에 온클릭 이벤트 추가(입력된 이름과 이메일과 비밀번호를 String으로 받아와 signup 함수 실행)

            val name = intent.getStringExtra("uName2").toString()
            val email = intent.getStringExtra("uEmail2").toString()
            val password = intent.getStringExtra("uPw2").toString()
            val benchWeight =intent.getStringExtra("benchWight2").toString()
            val squatWeight =intent.getStringExtra("squatWight2").toString()
            val pullUpCount = intent.getStringExtra("pullUpCount2").toString()
            val level =  intent.getStringExtra("level2").toString()
            var a =  intent.getStringExtra("photo2")

            selectImage = Uri.parse(a);

            signUp(name, email,password,benchWeight,squatWeight,pullUpCount,level)

    }
    // signup 함수, createUserWithEmailAndPassword(매크로같은거일듯) 입력한 이름과 이메일, 비밀번호,uid, 각종 운동 무게및 갯수, 운동레벨 를 firebase에 전달 후 성공 유무를 확인하여 화면을 전환시키거나 메세지를 출력한다
    private  fun signUp(name:String, email: String, password: String,benchWeight: String,squatWeight: String,pullUpCount:String,level:String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                        uploadPhoto(selectImage)
                    addUserToDatabase(
                        name,
                        email,
                        mAuth.currentUser?.uid!!,
                        benchWeight,
                        squatWeight,
                        pullUpCount,
                        level,
                    )

                    val intent = Intent(this@SignUpLast, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@SignUpLast, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadPhoto(selectImage: Uri) {// 파이어베이스에 이미지 파일 올리고, 올린파일 다시 받아오는 코드.
        val fileName = mAuth.currentUser?.uid.toString()
        storage.reference.child("article/photo").child(fileName)
            .putFile(selectImage)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // 파일 업로드에 성공했기 때문에 파일을 다 시 받아 오도록 해야함
                    storage.reference.child("article/photo").child(fileName).downloadUrl
                }
            }

        //회원가입이 완료된 유저의 데이터베이스를 생성한다

    }



    //회원가입이 완료된 유저의 데이터베이스를 생성한다
    private fun addUserToDatabase(
        name: String,
        email: String,
        uid: String,
        benchWeight: String,
        squatWeight: String,
        pullUpCount: String,
        level:String
    ){
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(User(name,email,uid,benchWeight,squatWeight,pullUpCount,level,selectImage.toString()))
    }
}

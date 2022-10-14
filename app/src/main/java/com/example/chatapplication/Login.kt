
package com.example.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import java.util.jar.Manifest

class Login : AppCompatActivity() {

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button
    private  lateinit var  mAuth: FirebaseAuth


    //유니티 wakeup같은 기능을 하는 함수 앱 실행시 단 1번만 실행
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
      supportActionBar?.hide() //상태 표시 줄을 감춘다(로그아웃기능 감춤)

        mAuth = FirebaseAuth.getInstance() //파이어베이스에 데이터를 추가하거나 조회하기 위해 변수 선언(정의)


        //findViewById(R.id.~)를 하게되면 xml 레이아웃에서 id가 ~인 객체에 접근하여 값을 변경할 수 있다
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)

        //회원가입 버튼에 온클릭 이벤트 추가(회원가입 화면으로 전환)
        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }


        //로그인 버튼에 온클릭 이벤트 추가(입력된 이메일과 비밀번호 값을 string으로 받아와 login 함수 실행)
        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            login(email,password);
        }

    }

    //login함수 signInWithEmailAndPassword를 이용하여 firebase에 입력한 이메일과 비밀번호 전달, 인증이 성공되면 메인엑티비티로 화면 전환
    //addOnCompleteListener는 통신이 완료된 뒤 성공유무를 확인 할 수 있다.
    private fun login(email: String, password: String){
        if(email.isNotEmpty() && password.isNotEmpty()) { //이메일과 비밀번호가 비어있지 않을때만
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        val intent = Intent(this@Login, MainActivity::class.java)
                        finish()
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@Login, "User does not exist", Toast.LENGTH_SHORT)
                            .show() // 인증이 실패하면 토스트 메세지 출력
                    }
                }
        }
        else{//이메일과 비밀번호값이 비어있으면 안된다는 메세지 출력
            Toast.makeText(this,
                "Email and password cannot be empty",
                Toast.LENGTH_SHORT).show()
        }
    }
}
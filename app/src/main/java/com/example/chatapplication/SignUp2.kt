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

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var benchWight: EditText//벤치 무게
    private lateinit var squtWight: EditText//스쿼트 무게
    private lateinit var pullUpCount: EditText// 풀업 갯수
    private lateinit var level:EditText// 운동수준
    private lateinit var btnSignUp: Button
    private  lateinit var  mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var selectImage: Uri
    private lateinit var ProfileImg : ImageView
    private lateinit var  storage: FirebaseStorage

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide() // 상태 표시줄 숨기기

        mAuth = FirebaseAuth.getInstance() //파이어베이스에 데이터를 추가하거나 조회하기 위해 변수 선언(정의)
        storage = FirebaseStorage.getInstance()
        //findViewById(R.id.~)를 하게되면 xml 레이아웃에서 id가 ~인 객체에 접근하여 값을 변경할 수 있다
        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        benchWight = findViewById(R.id.edt_BenchPower)
        squtWight = findViewById(R.id.edt_SqutPower)
        pullUpCount = findViewById(R.id.edt_PullUpPower)
        level = findViewById(R.id.edt_Level)
        btnSignUp = findViewById(R.id.btnSignUp)
        ProfileImg = findViewById(R.id.ProfileImg)
        //회원가입 버튼에 온클릭 이벤트 추가(입력된 이름과 이메일과 비밀번호를 String으로 받아와 signup 함수 실행)
        btnSignUp.setOnClickListener {
            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            val benchWeight_ =benchWight.text.toString()
            val squtWeight_ =squtWight.text.toString()
            val pullUpCount_ = pullUpCount.text.toString()
            val level_ =  level.text.toString()
            signUp(name, email,password,benchWeight_,squtWeight_,pullUpCount_,level_)
        }
        ProfileImg.setOnClickListener{
            val d = Log.d(TAG, "addImageButton called!!")
            when {
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                -> {
                    // 권한이 존재하는 경우
                    // TODO 이미지를 가져옴
                    getImageFromAlbum()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    // 권한이 거부 되어 있는 경우
                    showPermissionContextPopup()
                }
                else -> {
                    // 처음 권한을 시도했을 때 띄움
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        RESULT_FIRST_USER)
                }
            }

        }
    }

    private fun showPermissionContextPopup() {
        TODO("Not yet implemented")
    }

    private fun getImageFromAlbum() {
        var intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(intent,45)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK) {
            Toast.makeText(this,"잘못된 접근입니다",Toast.LENGTH_SHORT).show()
            return
        }
        when(requestCode){
            requestCode -> {
                val selectedImageURI : Uri? = data?.data
                if( selectedImageURI != null ) {
                    val imageView = findViewById<ImageView>(R.id.ProfileImg)
                    imageView.setImageURI(selectedImageURI)
                    selectImage = selectedImageURI
                }else {
                    Toast.makeText(this,"이미지를 가져오지 못했습니다1",Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this,"이미지를 가져오지 못했습니다2",Toast.LENGTH_SHORT).show()
            }
        }
    }

    // signup 함수, createUserWithEmailAndPassword(매크로같은거일듯) 입력한 이름과 이메일, 비밀번호,uid, 각종 운동 무게및 갯수, 운동레벨 를 firebase에 전달 후 성공 유무를 확인하여 화면을 전환시키거나 메세지를 출력한다
    private  fun signUp(name:String, email: String, password: String,benchWeight: String,squtWeight: String,pullUpCount:String,level:String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (selectImage != null) {// 이미지가 null이 아닐시 즉 앨범에서 얻어오는게 성공했을 시 실행.
                        uploadPhoto(selectImage!!)
                    }
                    addUserToDatabase(
                        name,
                        email,
                        mAuth.currentUser?.uid!!,
                        benchWeight,
                        squtWeight,
                        pullUpCount,
                        level,
                    )

                    val intent = Intent(this@SignUp2, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(this@SignUp2, "Some error occurred", Toast.LENGTH_SHORT).show()
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
        squtWeight: String,
        pullUpCount: String,
        level:String
    ){
        mDbRef = FirebaseDatabase.getInstance().getReference()

        mDbRef.child("user").child(uid).setValue(User(name,email,uid,benchWeight,squtWeight,pullUpCount,level,selectImage!!.toString()))
    }
}
package com.example.chatapplication

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.regex.Pattern


class SignUp : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var pw : String
    private lateinit var btnSignUp: Button
    private lateinit var selectImage: Uri
    private lateinit var ProfileImg : ImageView
    private  lateinit var pattern : Pattern
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide() //상태 표시 줄을 감춘다(로그아웃기능 감춤)
        selectImage =Uri.parse("android.resource://" + "com.example.chatapplication" + "/" + R.drawable.profile);
        edtName = findViewById(R.id.edt_name)
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnSignUp = findViewById(R.id.btnSignUp)
        ProfileImg = findViewById(R.id.ProfileImg)
        btnSignUp.setOnClickListener {
                myStartActivity(SignUp2::class.java)
        }

        pattern = android.util.Patterns.EMAIL_ADDRESS
        ProfileImg.setOnClickListener{
            val d = Log.d(ContentValues.TAG, "addImageButton called!!")
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

    private fun myStartActivity(c: Class<*>) {

        if(edtName.text.isNotEmpty() && edtEmail.text.isNotEmpty() && edtPassword.text.isNotEmpty() && pattern.matcher(edtEmail.text).matches()) {
            val nextIntent = Intent(this@SignUp, SignUp2::class.java)
            nextIntent.putExtra("uName", edtName.text.toString())
            nextIntent.putExtra("uEmail", edtEmail.text.toString())
            nextIntent.putExtra("uPw", edtPassword.text.toString())
            nextIntent.putExtra("photo", selectImage.toString())
            startActivity(nextIntent)

        }
        else if(!pattern.matcher(edtEmail.text).matches()){
            Toast.makeText(this,"there is email input type error", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this,"there is empty space", Toast.LENGTH_SHORT).show()

        }
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
            Toast.makeText(this,"잘못된 접근입니다", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this,"이미지를 가져오지 못했습니다1", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this,"이미지를 가져오지 못했습니다2", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showPermissionContextPopup() {
        TODO("Not yet implemented")
    }
}



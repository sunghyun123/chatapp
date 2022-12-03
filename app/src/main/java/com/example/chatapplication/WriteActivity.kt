package com.example.chatapplication

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.CaseMap
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
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class WriteActivity : AppCompatActivity() {
    private  lateinit var  mAuth: FirebaseAuth
    private lateinit var MainImage: ImageView
    private lateinit var selectImage : Uri
    private lateinit var uid : String
    private lateinit var  Title : EditText
    private lateinit var  contents : EditText
    private lateinit var storage : FirebaseStorage
    private lateinit var mDbRef : DatabaseReference
    private lateinit var noti : notice
    private lateinit var subtn : Button
    private lateinit var imgbtn : Button
    private  var imgon : Boolean = false
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imgon = false;
        setContentView(R.layout.activity_write)
        mAuth = FirebaseAuth.getInstance() //파이어베이스에 데이터를 추가하거나 조회하기 위해 변수 선언(정의)
        uid = mAuth.currentUser!!.uid
        Title = findViewById(R.id.titleEditText)
        contents = findViewById(R.id.contentsEditText)
        MainImage = findViewById(R.id.img)
        subtn = findViewById(R.id.check)
        imgbtn = findViewById(R.id.image)
        mDbRef = FirebaseDatabase.getInstance().getReference()
        imgbtn.setOnClickListener{
            imgon = true;
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
        subtn.setOnClickListener {
            uploadPost(Title.text.toString(), contents.text.toString())
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
        if(resultCode !=  Activity.RESULT_OK) {
            Toast.makeText(this,"잘못된 접근입니다", Toast.LENGTH_SHORT).show()
            return
        }
        when(requestCode){
            requestCode -> {
                val selectedImageURI : Uri? = data?.data
                if( selectedImageURI != null ) {
                    val imageView = findViewById<ImageView>(R.id.img)
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
    private fun uploadPost(title : String, content : String)
    {
        if(title!!.isNotEmpty() && content!!.isNotEmpty())
        {
            val nextIntent = Intent(this@WriteActivity, UploadNotice::class.java)
            nextIntent.putExtra("title", title)
            nextIntent.putExtra("content", content)
            if(imgon)
                nextIntent.putExtra("img",selectImage.toString())
            else
                nextIntent.putExtra("img","null")
            finish()
            startActivity(nextIntent)

        }
        else{
            Toast.makeText(this,"there is empty space", Toast.LENGTH_SHORT).show()
        }
    }



}





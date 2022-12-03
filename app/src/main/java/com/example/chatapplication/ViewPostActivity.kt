package com.example.chatapplication

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import org.w3c.dom.Text
import java.io.File

class ViewPostActivity : AppCompatActivity() {
    private  lateinit var  mAuth: FirebaseAuth
    private lateinit var MainImage: ImageView
    private lateinit var selectImage : Uri
    private lateinit var uid : String
    private lateinit var  Title : TextView
    private lateinit var  contents : TextView
    private lateinit var storage : FirebaseStorage
    private lateinit var mDbRef : DatabaseReference
    private lateinit var noti : notice
    private lateinit var subtn : Button
    private lateinit var imgbtn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_view_post)
        Title = findViewById(R.id.titleEditText)
        contents = findViewById(R.id.contentsEditText)
        MainImage = findViewById(R.id.img)

        Title.text = intent.getStringExtra("Title").toString()
        contents.text = intent.getStringExtra("Content").toString()


        val fileName = mAuth.currentUser?.uid.toString()
        var uri_ = intent.getStringExtra("key").toString()
        var profilefile = File.createTempFile("images","jpeg");
        var sref = storage.reference.child("article/notice").child(fileName).child(uri_)//지금은 로컬인데 메모리로 바꿀것 좀 느리다.
        sref.getFile(profilefile).addOnCompleteListener{
            Glide.with(this)
                .load(File(profilefile.absolutePath))
                .into(MainImage);
        }


//        val fileName = mAuth.currentUser?.uid.toString()
//        var profilefile = File.createTempFile("images","jpeg");
//
//        var sref = storage.reference.child("article/notice").child(fileName)//지금은 로컬인데 메모리로 바꿀것 좀 느리다.
//        sref.getFile(profilefile).addOnCompleteListener{
//            Glide.with(this)
//                .load(File(profilefile.absolutePath))
//                .into(ProfileImg);
//        }


    }
}
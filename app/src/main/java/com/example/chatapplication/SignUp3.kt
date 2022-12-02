package com.example.chatapplication

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SignUp3 : AppCompatActivity() {


    private lateinit var btnSignUp: Button
    private lateinit var sexbtn : Button

    var sex : String? = "무"

    @RequiresApi(Build.VERSION_CODES.M)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up3)
        btnSignUp = findViewById(R.id.btnSignUp)
        sexbtn = findViewById(R.id.edt_Level)

        supportActionBar?.hide() // 상태 표시줄 숨기기
        sexbtn.setOnClickListener {
            var layout = layoutInflater.inflate(R.layout.dialog_num_select, null)
            var build = AlertDialog.Builder(it.context).apply {
                setView(layout)
            }

            val dialog = build.create()
            dialog.show()
            layout.findViewById<NumberPicker>(R.id.number_picker).minValue = 0;
            layout.findViewById<NumberPicker>(R.id.number_picker).maxValue = 1;

            layout.findViewById<NumberPicker>(R.id.number_picker).displayedValues = arrayOf(
                "남자","여자"
            )
            layout.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            layout.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                when(layout.findViewById<NumberPicker>(R.id.number_picker).value){
                    0 -> sex = "남자"
                    1 -> sex = "여자"
                }
                if(sex!="무")
                    sexbtn.text = "신체의 성별 : $sex"
                dialog.dismiss()
            }
        }
        btnSignUp.setOnClickListener {
            myStartActivity()
        }
    }

    private fun myStartActivity() {
        if (sex!="무") {
                val nextIntent = Intent(this@SignUp3, SignUpLast::class.java)
                nextIntent.putExtra("uName2", intent.getStringExtra("uName1").toString())
                nextIntent.putExtra("uEmail2", intent.getStringExtra("uEmail1").toString())
                nextIntent.putExtra("uPw2", intent.getStringExtra("uPw1").toString())
                nextIntent.putExtra("photo2", intent.getStringExtra("photo1").toString())
                nextIntent.putExtra("benchWight2", intent.getStringExtra("benchWight1").toString())
                nextIntent.putExtra("squatWight2", intent.getStringExtra("squatWight1").toString())
                nextIntent.putExtra("pullUpCount2", intent.getStringExtra("pullUpCount1").toString())
                nextIntent.putExtra("lev2", intent.getStringExtra("lev").toString())
                nextIntent.putExtra("sex", sex.toString())
                startActivity(nextIntent)
            }
        else{
            Toast.makeText(this,"there is empty space", Toast.LENGTH_SHORT).show()
        }
    }

}
package com.example.chatapplication

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class SetProfileActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var benchWight: Button
    private lateinit var squatWeight: Button
    private lateinit var pullUpCount: Button
    private lateinit var level:Button
    private lateinit var btnSignUp: Button
    private  lateinit var mAuth: FirebaseAuth
    private lateinit var  storage: FirebaseStorage
    private lateinit var mDbRef: DatabaseReference
    private lateinit var selectImage: Uri
    private lateinit var ProfileImg : ImageView
    private lateinit var file : File
    private lateinit var sexbtn : Button

    var bench : Int? = null
    var squat : Int? = null
    var pullup : Int? = null
    var lev : Int? = null
    var sex : String? = "무"
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)
        supportActionBar?.hide() //상태 표시 줄을 감춘다(로그아웃기능 감춤)
        selectImage = Uri.parse("android.resource://" + "com.example.chatapplication" + "/" + R.drawable.profile);
        supportActionBar?.hide() // 상태 표시줄 숨기기

        mAuth = FirebaseAuth.getInstance() //파이어베이스에 데이터를 추가하거나 조회하기 위해 변수 선언(정의)
        storage = FirebaseStorage.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        //findViewById(R.id.~)를 하게되면 xml 레이아웃에서 id가 ~인 객체에 접근하여 값을 변경할 수 있다
        edtName = findViewById(R.id.edt_name)
        benchWight = findViewById(R.id.edt_BenchPower)
        squatWeight = findViewById(R.id.edt_SqutPower)
        pullUpCount = findViewById(R.id.edt_PullUpPower)
        level = findViewById(R.id.edt_Level)
        btnSignUp = findViewById(R.id.btnSignUp)
        ProfileImg = findViewById(R.id.ProfileImg)
        sexbtn = findViewById(R.id.sex)
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
                if(sex != "무")
                sexbtn.text = "신체 성별 : " + sex.toString()
                dialog.dismiss()
            }

        }

        benchWight.setOnClickListener {
            var layout = layoutInflater.inflate(R.layout.dialog_num_select, null)
            var build = AlertDialog.Builder(it.context).apply {
                setView(layout)
            }
            val dialog = build.create()
            dialog.show()
            layout.findViewById<NumberPicker>(R.id.number_picker).minValue = 0;
            layout.findViewById<NumberPicker>(R.id.number_picker).maxValue = 21;

            layout.findViewById<NumberPicker>(R.id.number_picker).displayedValues = arrayOf(
                "0", "10", "20", "30", "40", "50", "60","70", "80", "90", "100", "110", "120",
                "130", "140", "150", "160", "170", "180","190", "200", "210"
            )
            layout.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            layout.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                bench =  layout.findViewById<NumberPicker>(R.id.number_picker).value *10
                benchWight.text = "벤치프레스 무게 : " + bench.toString()
                dialog.dismiss()
            }

        }
        squatWeight.setOnClickListener {
            var layout = layoutInflater.inflate(R.layout.dialog_num_select, null)
            var build = AlertDialog.Builder(it.context).apply {
                setView(layout)
            }
            val dialog = build.create()
            dialog.show()

            layout.findViewById<NumberPicker>(R.id.number_picker).minValue = 0;
            layout.findViewById<NumberPicker>(R.id.number_picker).maxValue = 30;
            layout.findViewById<NumberPicker>(R.id.number_picker).displayedValues = arrayOf(
                "0","10", "20", "30", "40", "50", "60","70", "80", "90", "100", "110", "120",
                "130", "140", "150", "160", "170", "180","190", "200", "210","220","230", "240", "250",
                "260","270", "280", "290","300"
            )
            layout.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            layout.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                squat =  layout.findViewById<NumberPicker>(R.id.number_picker).value * 10
                squatWeight.text = "스쿼트 무게 : " + squat.toString()
                dialog.dismiss()
            }
        }
        pullUpCount.setOnClickListener {
            var layout = layoutInflater.inflate(R.layout.dialog_num_select, null)
            var build = AlertDialog.Builder(it.context).apply {
                setView(layout)
            }
            val dialog = build.create()
            dialog.show()

            layout.findViewById<NumberPicker>(R.id.number_picker).minValue = 0
            layout.findViewById<NumberPicker>(R.id.number_picker).maxValue = 40

            layout.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            layout.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                pullup =  layout.findViewById<NumberPicker>(R.id.number_picker).value
                pullUpCount.text = "풀업 개수 : " + pullup.toString()
                dialog.dismiss()
            }
        }
        level.setOnClickListener {
            var layout = layoutInflater.inflate(R.layout.dialog_num_select, null)
            var build = AlertDialog.Builder(it.context).apply {
                setView(layout)
            }
            val dialog = build.create()
            dialog.show()

            layout.findViewById<NumberPicker>(R.id.number_picker).minValue = 0
            layout.findViewById<NumberPicker>(R.id.number_picker).maxValue = 300

            layout.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            layout.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                lev =  layout.findViewById<NumberPicker>(R.id.number_picker).value
                level.text = "운동 경력 : " + lev.toString()
                dialog.dismiss()
            }

        }

        //회원가입 버튼에 온클릭 이벤트 추가(입력된 이름과 이메일과 비밀번호를 String으로 받아와 signup 함수 실행)
        btnSignUp.setOnClickListener {
            val name = edtName.text.toString()
            var user =  mAuth.currentUser;
            val email = user?.email.toString();
            signUp(name,email, sex.toString(), bench.toString(), squat.toString(), pullup.toString(),
                lev.toString()
            )
        }
        loadProfile();
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

    private fun loadProfile() {
        val fileName = mAuth.currentUser?.uid.toString()
        var profilefile = File.createTempFile("images","jpeg");

        var sref = storage.reference.child("article/photo").child(fileName)//지금은 로컬인데 메모리로 바꿀것 좀 느리다.
        sref.getFile(profilefile).addOnCompleteListener{
            Glide.with(this)
                .load(File(profilefile.absolutePath))
                .into(ProfileImg);
        }

//        //값들얻어오는거 구현하다맘 금방할듯.
        mDbRef.child("user").child(fileName).child("name").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    edtName.hint = "이름 : "+ dataSnapshot.getValue<String>()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException())
                }
            })
        mDbRef.child("user").child(fileName).child("benchWeight").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                benchWight.hint = "벤치 무게 : "+ dataSnapshot.getValue<String>()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
        mDbRef.child("user").child(fileName).child("squatWeight").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                squatWeight.hint = "스쿼트 무게 : "+ dataSnapshot.getValue<String>()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
        mDbRef.child("user").child(fileName).child("pullUpCount").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                pullUpCount.hint = "풀업 갯수 : "+dataSnapshot.getValue<String>()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
        mDbRef.child("user").child(fileName).child("level").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                level.hint = "운동 수준 : "+dataSnapshot.getValue<String>()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    // signup 함수, createUserWithEmailAndPassword(매크로같은거일듯) 입력한 이름과 이메일, 비밀번호를 firebase에 전달 후 성공 유무를 확인하여 화면을 전환시키거나 메세지를 출력한다
    private  fun signUp(name:String, email: String,sex: String,benchWeight: String,squtWeight: String,pullUpCount:String,level:String){
        if(name.isNotEmpty() && sex!="무" && benchWeight.isNotEmpty() && squtWeight.isNotEmpty() && pullUpCount.isNotEmpty() && level.isNotEmpty()) {
           // 이미지가 null이 아닐시 즉 앨범에서 얻어오는게 성공했을 시 실행.
            uploadPhoto(selectImage)

            addUserToDatabase(
                name,
                email,
                mAuth.currentUser?.uid!!,
                sex,
                benchWeight,
                squtWeight,
                pullUpCount,
                level,
                "on",
                2.0,
                1.0

                )
            val intent = Intent(this@SetProfileActivity, MainActivity::class.java)
            finish()
            startActivity(intent)
        }
        else{
            Toast.makeText(this,"there is empty space", Toast.LENGTH_SHORT).show()
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
    private fun Numberpick(min : Int, max: Int, valueName : String) {

        val nextIntent2 = Intent(this@SetProfileActivity, numberSelectActivity::class.java)
        nextIntent2.putExtra("min",min)
        nextIntent2.putExtra("max",max)
        nextIntent2.putExtra("valueName",valueName)
        nextIntent2.putExtra("atname","SetProfileActivity")
        startActivity(nextIntent2)
    }
    //회원가입이 완료된 유저의 데이터베이스를 생성한다
    private fun addUserToDatabase(
        name: String,
        email: String,
        uid: String,
        sex : String,
        benchWeight: String,
        squtWeight: String,
        pullUpCount: String,
        level:String,
        state:String,
        lat:Double,
        lon:Double
    ){

        mDbRef.child("user").child(uid).setValue(User(name,
            email,
            uid,
            sex,
            benchWeight,
            squtWeight,
            pullUpCount,
            level,
            selectImage.toString(),
            state,
            lat,
            lon))
    }

    var waitTime = 0L

    override fun onBackPressed() {
        if(System.currentTimeMillis() - waitTime >=1500 ) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this,"뒤로가기 버튼을 한번 더 누르면 메인화면으로 돌아갑니다.",Toast.LENGTH_SHORT).show()
        } else {
            finish() // 액티비티 종료
        }
    }
}


    //회원가입이 완료된 유저의 데이터베이스를 생성한다

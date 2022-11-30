package com.example.chatapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.android.gms.location.*


class SignUpLast : AppCompatActivity() {


    private lateinit var btnSignUp: Button
    private  lateinit var  mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var  storage: FirebaseStorage
    private lateinit var selectImage:Uri


    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는
    private val REQUEST_PERMISSION_LOCATION = 10

    private  var textlat : Double = 1.0
    private  var textlon : Double = 1.0

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
            val sex = intent.getStringExtra("sex").toString()
            val level =  intent.getStringExtra("lev2").toString()
            var a =  intent.getStringExtra("photo2")



            selectImage = Uri.parse(a);

            mLocationRequest =  LocationRequest.create().apply {

                priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            }
            startLocationUpdates() // 위도경도 계산 함수 호출
            signUp(name, email,password,sex,benchWeight,squatWeight,pullUpCount,level)

    }
    // signup 함수, createUserWithEmailAndPassword(매크로같은거일듯) 입력한 이름과 이메일, 비밀번호,uid, 각종 운동 무게및 갯수, 운동레벨 를 firebase에 전달 후 성공 유무를 확인하여 화면을 전환시키거나 메세지를 출력한다
    private  fun signUp(name:String,
                        email: String,
                        password: String,
                        sex:String,
                        benchWeight: String,
                        squatWeight: String,
                        pullUpCount:String,
                        level:String ){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                        uploadPhoto(selectImage)
                    addUserToDatabase(
                        name,
                        email,
                        mAuth.currentUser?.uid!!,
                        sex,
                        benchWeight,
                        squatWeight,
                        pullUpCount,
                        level,
                        "ON",
                        textlon,
                        textlat
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

    private fun startLocationUpdates() {

        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        // 기기의 위치에 관한 정기 업데이트를 요청하는 메서드 실행
        // 지정한 루퍼 스레드(Looper.myLooper())에서 콜백(mLocationCallback)으로 위치 업데이트를 요청
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    // 시스템으로 부터 위치 정보를 콜백으로 받음
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            // 시스템에서 받은 location 정보를 onLocationChanged()에 전달
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    // 시스템으로 부터 받은 위치정보를 화면에 갱신해주는 메소드
    fun onLocationChanged(location: Location) {
        mLastLocation = location
        textlat = mLastLocation.latitude // 갱신 된 위도
        textlon = mLastLocation.longitude // 갱신 된 경도

    }

    //회원가입이 완료된 유저의 데이터베이스를 생성한다
    private fun addUserToDatabase(
        name: String,
        email: String,
        uid: String,
        sex: String,
        benchWeight: String,
        squatWeight: String,
        pullUpCount: String,
        level: String,
        state: String,
        lat: Double,
        lon: Double
    ){
        mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("user").child(uid).setValue(User(name,
            email,
            uid,
            sex,
            benchWeight,
            squatWeight,
            pullUpCount,
            level,
            selectImage.toString(),
            state,
            lat,
            lon
        ))
    }
}

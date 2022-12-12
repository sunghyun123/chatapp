package com.example.chatapplication

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog.show
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.math.*


class MainActivity : AppCompatActivity() {
    private  lateinit var  userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>//각 유저들의 데이터를 가지고있는 배열

    private  lateinit var adapter: UserAdapter// 데이터와 뷰를 이어주는 중간다리
    private lateinit var  mAuth: FirebaseAuth // 파이어베이스 유저관련 접속하기위한 변수
    private  lateinit var mDbRef: DatabaseReference// 파이어베이스 리얼타임베이스 접근하기위한 변수

    var T = false



    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null // 현재 위치를 가져오기 위한 변수
    lateinit var mLastLocation: Location // 위치 값을 가지고 있는 객체
    internal lateinit var mLocationRequest: LocationRequest // 위치 정보 요청의 매개변수를 저장하는

    private val REQUEST_PERMISSION_LOCATION = 10
    private  var textlat : Double? = null
    private  var textlon : Double? = null
    private  var distance : String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)//
        setContentView(R.layout.activity_main)

        val setSearchView = findViewById<SearchView>(R.id.search_view_phone_book)
        setSearchView.setOnQueryTextListener(searchViewTextListener)




        startService(Intent(this,ForceTerminationService::class.java))
        //유저,리얼타임데이터베이스에 접근 해서 값얻을수있는 통로뚥기 저 두변수가 길이다.
        mAuth = FirebaseAuth.getInstance()// 나자신의 유저정보 m이 my의 약자다
        mDbRef = FirebaseDatabase.getInstance().getReference()
        //userList 생성자 호출
        userList = ArrayList()
        //sortuserList = ArrayList()
        mAuth = FirebaseAuth.getInstance()

        //어뎁터 생성자. 여기서 이걸해줌으로 각 어뎁터는 하나의 포지션을같는다 연결해버리니까

        adapter = UserAdapter(this, userList)
        //리사이클러뷰 찾아서 잡아주기
        userRecyclerView = findViewById(R.id.userRecyclerView)
        //유저 리사이클러뷰를 어떤식으로 보이게할지 LinearLayout형식으로
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        //어뎁터를 넣어준다. 이거때문에 포지션값이 알아서 정의되는거일지도
        userRecyclerView.adapter = adapter
        T = true

        mLocationRequest =  LocationRequest.create().apply {

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        startLocationUpdates()

        mDbRef.child("user").addValueEventListener(object: ValueEventListener{// 파이어베이스에서 데이터 읽고 쓸수있는 리스너
            override fun onDataChange(snapshot: DataSnapshot) {
            if (T == true) {

                userList.clear()//리스트 초기화
                onadapterStart()
                for (postSnapshot in snapshot.children) {//user데이터베이스에 있는 모든 리스트들이 처음부터 끝까지 끝날떄 까지
                    val currentUser = postSnapshot.getValue(User::class.java)//user 데이터 베이스에있는 값을 첫번째 유저 부터 가져옴
                    //위의 for문을 볼 때 반복문임.
                    //val user = snapshot.getValue<User>()

                    if (mAuth.currentUser?.uid != currentUser?.uid) {//내가 현재 참조된 유저가 아니면
                        userList.add(currentUser!!)//유저리스트에 내자신은 없다. 유저리스트에 참조된 유저 넣기, 이 코드 두번 쓰면 중복출력

                    }

                }

                adapter.notifyDataSetChanged()//어뎁터에게 새로운 유저정보가 리스트에 들어와서 리스트의 크기와 정보가 바뀔거라는 알림.
                // 이녀석이 포지션 잡아줌
            }
        }
            override fun onCancelled(error: DatabaseError) {
        }
        })

            if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            }
            val permissions: Array<String> = arrayOf(
                android.Manifest.permission.ACTIVITY_RECOGNITION,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions, 0)
        if(mAuth.currentUser?.uid.toString() == null)
            mDbRef.child("user").child( mAuth.currentUser?.uid.toString()).child("state").setValue("ON")// 온오프 표시



    }
    var searchViewTextListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            //검색버튼 입력시 호출, 검색버튼이 없으므로 사용하지 않음
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            //텍스트 입력/수정시에 호출
            override fun onQueryTextChange(s: String): Boolean {
                adapter.filter.filter(s)


                return false

            }
        }



    //함수가 호출될때 한번만 실행됨, 상태표시줄에 메뉴가 추가됨
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu) // 생성된 메뉴를 객체화
        return super.onCreateOptionsMenu(menu) // 부모클래스의 onCreateOptionsMenu에 접근, 즉 재귀호출
    }



    //메뉴가 선택되었을때 호출되는 함수, 화면 전환
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.logout){ //첫번째 아이템:로그아웃
            T = false
            val intent = Intent(this@MainActivity, Login::class.java)
            mDbRef.child("user").child( mAuth.currentUser?.uid.toString()).child("state").setValue("OFF")// 온오프 표시
            mAuth.signOut()
            finish()
            startActivity(intent)
            return true
        }
        if(item.itemId == R.id.setProfile){ //두번째 아이템:프로필 설정
            val intent = Intent(this@MainActivity, SetProfileActivity::class.java)

            startActivity(intent)
            return true
        }
        if(item.itemId == R.id.pedometerBtn){ //세번째 아이템 만보기 버튼
            val intent = Intent(this@MainActivity, pedometerActivity::class.java)
            startActivity(intent)
            return true
        }


        if(item.itemId == R.id.setdistance){ //거리설정 버튼
            


            val dialog = CustSeekbar(this)
            dialog.showSeekbar()
            dialog.setOnClickListener(object: CustSeekbar.ButtonClickListener{
                override fun onClicked(text: String) {
                    var split = text.split("k")
                    var dis = ((split[0].toFloat())*1000).toInt()
                    Log.i(ContentValues.TAG,"$dis")
                    adapter.filter.filter(dis.toString())
                }
            })


        }


        if(item.itemId == R.id.notice){ //세번째 아이템 만보기 버튼
            val intent = Intent(this@MainActivity, nboardActivity::class.java)
            startActivity(intent)

            return true

        }
        //여기서 처리
        return true
    }



    fun onadapterStart():Boolean{
        adapter.filter.filter(" ")
        //Log.d(TAG, "SearchVies Text is changed : $s")
        return false

    }



    override fun onRequestPermissionsResult( //권한 설정 창 뜨게하는 코드
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            0 -> {
                if (grantResults.isNotEmpty()){
                    var isAllGranted = true
                    // 요청한 권한 허용/거부 상태 한번에 체크
                    for (grant in grantResults) {
                        if (grant != PackageManager.PERMISSION_GRANTED) {
                            isAllGranted = false
                            break;
                        }
                    }
                    // 요청한 권한을 모두 허용했음.
                    if (isAllGranted) {
                        // 다음 step으로 ~
                    }
                    // 허용하지 않은 권한이 있음. 필수권한/선택권한 여부에 따라서 별도 처리를 해주어야 함.
                    else {
                        if(!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            || !ActivityCompat.shouldShowRequestPermissionRationale(this,
                                android.Manifest.permission.ACTIVITY_RECOGNITION)
                            || !ActivityCompat.shouldShowRequestPermissionRationale(this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION)){
                            // 다시 묻지 않기 체크하면서 권한 거부 되었음.
                        } else {
                            // 접근 권한 거부하였음.
                        }
                    }
                }
            }
        }
    }


    private fun startLocationUpdates() {

        mLocationRequest =  LocationRequest.create().apply {

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        }


        //FusedLocationProviderClient의 인스턴스를 생성.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        mDbRef = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()

        mDbRef.child("user").child( mAuth.currentUser?.uid.toString()).child("lon").setValue(textlon)
        mDbRef.child("user").child( mAuth.currentUser?.uid.toString()).child("lat").setValue(textlat)

    }

}



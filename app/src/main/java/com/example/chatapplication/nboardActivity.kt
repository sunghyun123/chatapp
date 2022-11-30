package com.example.chatapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class nboardActivity : AppCompatActivity() {
    private  lateinit var  noticeRecyclerView: RecyclerView
    private lateinit var noticeList: ArrayList<notice>//각 유저들의 데이터를 가지고있는 배열
    private  lateinit var adapter: noticeAdapter// 데이터와 뷰를 이어주는 중간다리
    private lateinit var  mAuth: FirebaseAuth // 파이어베이스 유저관련 접속하기위한 변수
    private  lateinit var mDbRef: DatabaseReference// 파이어베이스 리얼타임베이스 접근하기위한 변수
    var T = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)//
        setContentView(R.layout.activity_nboard)
        startService(Intent(this,ForceTerminationService::class.java))
        //유저,리얼타임데이터베이스에 접근 해서 값얻을수있는 통로뚥기 저 두변수가 길이다.
        mAuth = FirebaseAuth.getInstance()// 나자신의 유저정보 m이 my의 약자다
        mDbRef = FirebaseDatabase.getInstance().getReference()

        var lon : Double? = null
        //userList 생성자 호출
        noticeList = ArrayList()

        adapter = noticeAdapter(this, noticeList)
        //리사이클러뷰 찾아서 잡아주기
        noticeRecyclerView = findViewById(R.id.noticeRecyclerView)
        //유저 리사이클러뷰를 어떤식으로 보이게할지 LinearLayout형식으로
        noticeRecyclerView.layoutManager = LinearLayoutManager(this)
        //어뎁터를 넣어준다. 이거때문에 포지션값이 알아서 정의되는거일지도
        noticeRecyclerView.adapter = adapter
        T = true



        mDbRef.child("notice").addValueEventListener(object: ValueEventListener {// 파이어베이스에서 데이터 읽고 쓸수있는 리스너
        override fun onDataChange(snapshot: DataSnapshot) {
            if (T == true) {
                noticeList.clear()//리스트 초기화
                for (postSnapshot in snapshot.children) {//user데이터베이스에 있는 모든 리스트들이 처음부터 끝까지 끝날떄 까지

                    val currentnotice = postSnapshot.getValue(notice::class.java)//user 데이터 베이스에있는 값을 첫번째 유저 부터 가져옴
                    //위의 for문을 볼 때 반복문임.
                    //val user = snapshot.getValue<User>()


                        noticeList.add(currentnotice!!)//유저리스트에 내자신은 없다. 유저리스트에 참조된 유저 넣기, 이 코드 두번 쓰면 중복출력
                        //userList.add(user!!)

                }
                adapter.notifyDataSetChanged()//어뎁터에게 새로운 유저정보가 리스트에 들어와서 리스트의 크기와 정보가 바뀔거라는 알림.
                // 이녀석이 포지션 잡아줌
            }
        }
            override fun onCancelled(error: DatabaseError) {


            }
        })


        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

        }


        val permissions: Array<String> = arrayOf(
            android.Manifest.permission.ACTIVITY_RECOGNITION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)

        ActivityCompat.requestPermissions(this, permissions, 0)

        mDbRef.child("user").child( mAuth.currentUser?.uid.toString()).child("state").setValue("ON")// 온오프 표시
    }

    //함수가 호출될때 한번만 실행됨, 상태표시줄에 메뉴가 추가됨


    //메뉴가 선택되었을때 호출되는 함수, 화면 전환
}
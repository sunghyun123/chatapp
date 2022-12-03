package com.example.chatapplication

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class nboardActivity : AppCompatActivity() {
    private lateinit var noticeRecyclerView: RecyclerView
    private lateinit var noticeList: ArrayList<notice>//각 유저들의 데이터를 가지고있는 배열
    private lateinit var adapter: noticeAdapter// 데이터와 뷰를 이어주는 중간다리
    private lateinit var mAuth: FirebaseAuth // 파이어베이스 유저관련 접속하기위한 변수
    private lateinit var mDbRef: DatabaseReference// 파이어베이스 리얼타임베이스 접근하기위한 변수


    var T = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)//
        setContentView(R.layout.activity_nboard)
        startService(Intent(this, ForceTerminationService::class.java))
        //유저,리얼타임데이터베이스에 접근 해서 값얻을수있는 통로뚥기 저 두변수가 길이다.
        mAuth = FirebaseAuth.getInstance()// 나자신의 유저정보 m이 my의 약자다
        mDbRef = FirebaseDatabase.getInstance().getReference()

        var lon: Double? = null
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

        mDbRef.child("notice"  ).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mDbRef.child("user").addValueEventListener(object : ValueEventListener {
                    // 파이어베이스에서 데이터 읽고 쓸수있는 리스너
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (T == true) {
                            for (postSnapshot in snapshot.children) {//user데이터베이스에 있는 모든 리스트들이 처음부터 끝까지 끝날떄 까지
                                val currentUser =
                                    postSnapshot.getValue(User::class.java)//user 데이터 베이스에있는 값을 첫번째 유저 부터 가져옴

                                mDbRef.child("notice").child(currentUser?.uid.toString())
                                    .addValueEventListener(object : ValueEventListener {
                                        // 파이어베이스에서 데이터 읽고 쓸수있는 리스너
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (T == true) {
                                                noticeList.clear()//리스트 초기화
                                                for (postSnapshot in snapshot.children) {//user데이터베이스에 있는 모든 리스트들이 처음부터 끝까지 끝날떄 까지
                                                    val currentnotice_ =
                                                        postSnapshot.getValue(notice::class.java)//user 데이터 베이스에있는 값을 첫번째 유저 부터 가져옴
                                                    //위의 for문을 볼 때 반복문임.
                                                    //val user = snapshot.getValue<User>()
                                                    Log.i(
                                                        ContentValues.TAG,
                                                        "${currentnotice_?.Contents}"
                                                    )
                                                    noticeList.add(currentnotice_!!)
                                                    //유저리스트에 내자신은 없다. 유저리스트에 참조된 유저 넣기, 이 코드 두번 쓰면 중복출력
                                                    //userList.add(user!!)
                                                }
                                                adapter.notifyDataSetChanged()//어뎁터에게 새로운 유저정보가 리스트에 들어와서 리스트의 크기와 정보가 바뀔거라는 알림.
                                                // 이녀석이 포지션 잡아줌
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {

                                        }
                                    })
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        var i : Int? = 0






    }

    //함수가 호출될때 한번만 실행됨, 상태표시줄에 메뉴가 추가됨
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.write, menu) // 생성된 메뉴를 객체화
        return super.onCreateOptionsMenu(menu) // 부모클래스의 onCreateOptionsMenu에 접근, 즉 재귀호출
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.WritePost) { //두번째 아이템:프로필 설정
            val intent = Intent(this@nboardActivity, WriteActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        if (item.itemId == R.id.home) { //세번째 아이템 만보기 버튼
            val intent = Intent(this@nboardActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return true
    }
}
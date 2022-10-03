package com.example.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private  lateinit var  userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>//각 유저들의 데이터를 가지고있는 배열
    private  lateinit var adapter: UserAdapter// 데이터와 뷰를 이어주는 중간다리
    private lateinit var  mAuth: FirebaseAuth // 파이어베이스 유저관련 접속하기위한 변수
    private  lateinit var mDbRef: DatabaseReference// 파이어베이스 리얼타임베이스 접근하기위한 변수


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)//
        setContentView(R.layout.activity_main)


        //유저,리얼타임데이터베이스에 접근 해서 값얻을수있는 통로뚥기 저 두변수가 길이다.
        mAuth = FirebaseAuth.getInstance()// 나자신의 유저정보 m이 my의 약자다
        mDbRef = FirebaseDatabase.getInstance().getReference()
        //userList 생성자 호출
        userList = ArrayList()
        //어뎁터 생성자. 여기서 이걸해줌으로 각 어뎁터는 하나의 포지션을같는다 연결해버리니까
        adapter = UserAdapter(this, userList)
        //리사이클러뷰 찾아서 잡아주기
        userRecyclerView = findViewById(R.id.userRecyclerView)
        //유저 리사이클러뷰를 어떤식으로 보이게할지 LinearLayout형식으로
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        //어뎁터를 넣어준다. 이거때문에 포지션값이 알아서 정의되는거일지도
        userRecyclerView.adapter = adapter



        mDbRef.child("user").addValueEventListener(object: ValueEventListener{// 파이어베이스에서 데이터 읽고 쓸수있는 리스너
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()//리스트 초기화
                for(postSnapshot in snapshot.children){//user데이터베이스에 있는 모든 리스트들이 처음부터 끝까지 끝날떄 까지

                    val currentUser = postSnapshot.getValue(User::class.java)//user 데이터 베이스에있는 값을 첫번째 유저 부터 가져옴
                    //위의 for문을 볼 때 반복문임.

                    if(mAuth.currentUser?.uid != currentUser?.uid){//내가 현재 참조된 유저가 아니면
                        userList.add(currentUser!!)//유저리스트에 내자신은 없다. 유저리스트에 참조된 유저 넣기
                    }
                }
                adapter.notifyDataSetChanged()//어뎁터에게 새로운 유저정보가 리스트에 들어와서 리스트의 크기와 정보가 바뀔거라는 알림.
                // 이녀석이 포지션 잡아줌
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.logout){

            mAuth.signOut()
            val intent = Intent(this@MainActivity, Login::class.java)
            finish()
            startActivity(intent)
            return true
        }
        //여기서 처리
        if(item.itemId == R.id.setProfile){

            mAuth.signOut()
            val intent = Intent(this@MainActivity, Login::class.java)
            finish()
            startActivity(intent)
            return true
        }
        return true
    }
}
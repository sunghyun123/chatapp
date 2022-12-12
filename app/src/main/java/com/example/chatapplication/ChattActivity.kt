package com.example.chatapplication


import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

//채팅창 클래스, 채팅방 생성, 채팅한 메세지 표시 등의 기능을 수행하는 액티비티
class ChattActivity : AppCompatActivity() {

    private  lateinit var chatRecyclerView: RecyclerView
    private  lateinit var messageBox: EditText
    private  lateinit var sendButton: ImageView
    private  lateinit var messageAdapter: MessageAdapter
    private  lateinit var messageList: ArrayList<Message>
    private  lateinit var mDbRef: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    //유니티 wakeup같은 기능을 하는 함수 앱 실행시 단 1번만 실행
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatt)

        val name = intent.getStringExtra("name") // UserAdapter 액티비티의 31번째 줄의 데이터를 받는 함수
        val receiverUid = intent.getStringExtra("uid")// UserAdapter 액티비티의 32번째 줄의 데이터를 받는 함수

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid //Firebase 인증 객체 초기화
        mDbRef = FirebaseDatabase.getInstance().getReference() //Firebase에 데이터를 추가하거나 조회하기 위한 코드, 정의

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid


        supportActionBar?.title = name // 채팅방 상단에 상대방 이름 표시

        //findViewById(R.id.~)를 하게되면 xml 레이아웃에서 id가 ~인 객체에 접근하여 값을 변경할 수 있다
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)

        chatRecyclerView.layoutManager = LinearLayoutManager(this) // 수직(상하)로 리스트를 보여줌
        chatRecyclerView.adapter = messageAdapter



        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                //확실하진 않지만 리얼타임 데이터베이스 값을 읽어오는 함수인듯??
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()


                    //                    // 입력받은 메세지를 채팅창에 출력
                    for(postSnapshot in snapshot.children) {

                        val message = postSnapshot.getValue(Message::class.java)
                        //메세지 리스트에 추가
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })



        // 메세지 데이터베이스 추가, 메세지 보내기 버튼 클릭시 이벤트 발생 함수
        sendButton.setOnClickListener{

            val message = messageBox.text.toString() // 입력된 메세지를 String으로 읽어옴
            Log.i(ContentValues.TAG, "$message")
            if(message != "") {
                val messageObject = Message(message, senderUid) // Message클래스에 매개변수 전달

                mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                    // mDbRef의 자식인 chats의 자식의(이 자식은 DB에 의해 생성된 채팅창 고유 Id임) 메세지란 객체에 푸쉬함
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    } // 보낸 메세지가 성공적으로 Db에 등록될 시 receiverRoom에도(DB) 똑같은 값을 보낸다.
                messageBox.setText("") // 메세지를 보내면 메세지 박스 초기화
                chatRecyclerView.scrollToPosition(messageList.size-2);

            }
            else{
            }

        }
    }
    var waitTime = 0L

    override fun onBackPressed() {
        if(System.currentTimeMillis() - waitTime >=1500 ) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this,"뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finish() // 액티비티 종료
        }
    }
}
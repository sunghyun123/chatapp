package com.example.chatapplication

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import kotlin.math.*

class noticeAdapter(val context: Context, val noticeList: ArrayList<notice>):
    RecyclerView.Adapter<noticeAdapter.noticeViewHolder>() {
    //Adapter한개 정의
    private lateinit var storage: FirebaseStorage
    private lateinit var mAuth: FirebaseAuth
    private var clon: Double = 1.0
    var doubleArray = DoubleArray(3)
    var i: Int = 1
    private var currentuserlon: Double? = 1.0
    private var currentuserlat: Double? = 1.0
    private var mDbRef = FirebaseDatabase.getInstance().getReference()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): noticeViewHolder {//화면에 띄울 뷰 한개 생성
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return noticeViewHolder(view)


    }

    override fun onBindViewHolder(holder: noticeViewHolder, position: Int) {// 뷰 생성시 홀더와 포지션값은 정해짐
        // 방금 생성한 뷰가 화면에서 몇번쨰인지포지션을통해 알아내고 user의정보가 들어있는
        // userlist도 순서는동일하기에 유저리스트[position]을 통해 값을 커런트 유저로 넣어줌


        val currentUser = noticeList[position]//커런트유저는 데이터임
        mAuth = FirebaseAuth.getInstance()
        var lon: Double = 1.0
        var lat: Double = 1.0
        mDbRef.child("user").child(mAuth.currentUser?.uid.toString()).child("lon")
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userlon =
                        dataSnapshot.getValue<Double>()
                    lon = userlon!!

                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            })

        storage = FirebaseStorage.getInstance()
//        val fileName = currentUser.uid.toString()
//
//
//        var profilefile = File.createTempFile("images", "jpeg");
//
//
//        var sref = storage.reference.child("article/photo").child(fileName)
//        if (fileName != null) {
//            sref.getFile(profilefile).addOnCompleteListener {
//                holder.apply {
//                    Glide.with(context)
//                        .load(File(profilefile.absolutePath))
//                        .into(holder.profileimg);
//                }
//            }
//        }


//        holder.textName.text = currentUser.name//커런트유터 데이터에서 화면인 홀더로 이름을 넘겨줘서 띄울수있게함
//        holder.state.text = currentUser.State


        holder.itemView.setOnClickListener {//목록의 뷰를 누를시

            val intent = Intent(context, ViewPostActivity::class.java)// 화면전환할 액티비티 정의
            //전환할 엑티비티로 데이터 넘기기 받는쪽에선 getExtraa

//            intent.putExtra("name", currentUser.name)
//            intent.putExtra("State", currentUser.State)
//            intent.putExtra("benchWeight", currentUser.benchWeight)
//            intent.putExtra("level", currentUser.level)
//            intent.putExtra("profileImage", currentUser.profileImage)
//            intent.putExtra("pullUpCount", currentUser.pullUpCount)
//            intent.putExtra("squatWeight", currentUser.squatWeight)
//                intent.putExtra("bench",currentUser.)
           // intent.putExtra("img", profilefile.absolutePath.toString())

            context.startActivity(intent)//환면전환하기.
        }

    }




    override fun getItemCount(): Int {//유저리스트가 몇개인가 회원가입 한사람 수
        return noticeList.size
    }

    class noticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {// 누를수있는 뷰로 만들기

        val title = itemView.findViewById<TextView>(R.id.title)
        val location = itemView.findViewById<TextView>(R.id.contents)
        val img = itemView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.img_rv_photo)

    }
}


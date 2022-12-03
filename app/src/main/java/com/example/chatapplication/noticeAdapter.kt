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
    private lateinit var mAuth: FirebaseAuth


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): noticeViewHolder {//화면에 띄울 뷰 한개 생성
        val view: View = LayoutInflater.from(context).inflate(R.layout.noticelayout, parent, false)
        return noticeViewHolder(view)


    }

    override fun onBindViewHolder(holder: noticeViewHolder, position: Int) {// 뷰 생성시 홀더와 포지션값은 정해짐

        // 방금 생성한 뷰가 화면에서 몇번쨰인지포지션을통해 알아내고 user의정보가 들어있는
        // userlist도 순서는동일하기에 유저리스트[position]을 통해 값을 커런트 유저로 넣어줌
        val currentnotice = noticeList[position]//커런트유저는 데이터임
        mAuth = FirebaseAuth.getInstance()
        Log.i(ContentValues.TAG,"------${currentnotice.title}")
        holder.itemView.setOnClickListener {//목록의 뷰를 누를시
            val intent = Intent(context, ViewPostActivity::class.java)// 화면전환할 액티비티 정의
            intent.putExtra("Title",currentnotice.title.toString())
            intent.putExtra("Content",currentnotice.Contents.toString())
            intent.putExtra("Img",currentnotice.Image.toString())
            intent.putExtra("uid",currentnotice.uid.toString())
            intent.putExtra("likes",currentnotice.likes.toString())
            intent.putExtra("key",currentnotice.number.toString())
            context.startActivity(intent)//환면전환하기.

        }


        holder.title.text = currentnotice.title.toString()//커런트유터 데이터에서 화면인 홀더로 이름을 넘겨줘서 띄울수있게함
    }


    override fun getItemCount(): Int {//유저리스트가 몇개인가 회원가입 한사람 수
        return noticeList.size
    }

    class noticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {// 누를수있는 뷰로 만들기
        val title = itemView.findViewById<TextView>(R.id.title)

    }

}


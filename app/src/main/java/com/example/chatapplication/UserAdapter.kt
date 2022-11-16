package com.example.chatapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserAdapter(val context: Context, val userList: ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {//Adapter한개 정의
    private var auth : FirebaseAuth? = null
    val user = Firebase.auth.currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {//화면에 띄울 뷰 한개 생성
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {// 뷰 생성시 홀더와 포지션값은 정해짐
        // 방금 생성한 뷰가 화면에서 몇번쨰인지포지션을통해 알아내고 user의정보가 들어있는
        // userlist도 순서는동일하기에 유저리스트[position]을 통해 값을 커런트 유저로 넣어줌
        val currentUser = userList[position]//커런트유저는 데이터임


        holder.textName.text = currentUser.name//커런트유터 데이터에서 화면인 홀더로 이름을 넘겨줘서 띄울수있게함



        if (user != null) {
            holder.textActivity.text = "온라인"
        } else {
            holder.textActivity.text = "오프라인"
        }

        holder.itemView.setOnClickListener {//목록의 뷰를 누를시
            val intent = Intent(context,ChattActivity::class.java)// 화면전환할 액티비티 정의

            //전환할 엑티비티로 데이터 넘기기 받는쪽에선 getExtra씀
            intent.putExtra("name", currentUser.name)
            intent.putExtra("uid",currentUser.uid)


            context.startActivity(intent)//환면전환하기.
        }

    }

    override fun getItemCount(): Int {//유저리스트가 몇개인가 회원가입 한사람 수 
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){// 누를수있는 뷰로 만들기
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        val textActivity = itemView.findViewById<TextView>(R.id.tv_rv_activity)
    }


}
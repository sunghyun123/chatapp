package com.example.chatapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.View.Message
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2


    //채팅방 생성 코드
    //ViewHolder 객체를 생성한다(리스트를 생성한다). 단, 생성되는 객체는 데이터의 전체길이를 초과하지 않는다
    //viewType 에 따라 sentroom일지 receiveroom 일지 결정된다
    //리턴값은 리스트를 만들기위함임. 데이터의 전체길이 초과시 리턴되지 않음
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == 1){

            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            return ReceiveViewHolder(view)
        }else{

            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        }

    }

    //뷰홀더에 데이터를 바인딩해주는 함수
    //새롭게 생성되는 메세지에 현재 포지션값을 할당해준다. 그리고 뷰홀더를 샌트룸에 만들건지 리시버룸에 만들건지 정한다
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]

        if(holder.javaClass == SentViewHolder::class.java){



            val viewHolder = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message


        }else{
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
        }

    }

    //onCreateViewHolder함수의 viewType을 정해주는 함수
    //파이어베이스에 현재 로그인된 사용자와 현재 메세지를 보낸 Id가 일치한다면 샌트룸이 생성
    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }else {
            return ITEM_RECEIVE
        }
    }

    //리사이클뷰를 이용하기위해 필요한 데이터의 전체 길이를 리턴해준다.
    override fun getItemCount(): Int {
        return messageList.size
    }

    //sentMessage,receiveMessage 객체를 생성
    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
    }

}
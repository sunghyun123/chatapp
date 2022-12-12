package com.example.chatapplication.Adapter

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Filter
import android.widget.Filterable

import android.widget.PopupMenu

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.ViewPostActivity
import com.example.chatapplication.WriteActivity
import com.example.chatapplication.View.notice
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage


class noticeAdapter(val context: Context, val noticeList: ArrayList<notice>):
    RecyclerView.Adapter<noticeAdapter.noticeViewHolder>(), Filterable {
    //Adapter한개 정의
    private lateinit var mAuth: FirebaseAuth

    var filteredNotice= ArrayList<notice>()

    var noticeitemFilter = NoticeItemFilter()

    init {
        filteredNotice.addAll(noticeList)
    }

    private lateinit var mDbRef: DatabaseReference
    private lateinit var  storage: FirebaseStorage

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
        val currentnotice = filteredNotice[position]//커런트유저는 데이터임
        mAuth = FirebaseAuth.getInstance()
        Log.i(ContentValues.TAG,"------${currentnotice.title}")
        if(currentnotice.uid.toString() != mAuth.currentUser!!.uid){
            holder.menus.visibility = View.GONE
        }
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
        holder.menus.setOnClickListener {
            val popup = PopupMenu(holder.menus.context, it)
            popup.menuInflater.inflate(R.menu.post, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                if(item.itemId == R.id.delete){
                    storage = FirebaseStorage.getInstance()
                    mDbRef=FirebaseDatabase.getInstance().getReference()
                    mDbRef.child("notice").child(currentnotice.number.toString()).removeValue()
                    storage.reference.child("article/notice").child(currentnotice.uid.toString()).child(currentnotice.number.toString()).delete()
                }
                if(item.itemId == R.id.replace){
                    val intent = Intent(context, WriteActivity::class.java)// 화면전환할 액티비티 정의
                    intent.putExtra("Title",currentnotice.title.toString())
                    intent.putExtra("Content",currentnotice.Contents.toString())
                    intent.putExtra("Img",currentnotice.Image.toString())
                    intent.putExtra("uid",currentnotice.uid.toString())
                    intent.putExtra("likes",currentnotice.likes.toString())
                    intent.putExtra("key",currentnotice.number.toString())

                    context.startActivity(intent)//환면전환하기.
                }
                true
            }
            popup.show()
        }
    }

    inner class NoticeItemFilter : Filter() { // 리사이클러뷰 필터링 클래스, 매개변수로 전달받은 문자열에 따라 리스트를 필터링 해준다. 다른액티비티에서도 adapter를 통해 호출가능, 밑에있는 getFilter함수를 써야한다.
        override fun performFiltering(charSequence: CharSequence): FilterResults { //charSequence란 문자열을 매개변수로 받음 현재는 이름 또는 거리를 매개변수로 받아 필터링한다.
            val filterString = charSequence.toString() //원본 문자열을 toString 형태로 변수에 저장
            val results = FilterResults() //리턴할 결과값 변수 생성
            Log.i(ContentValues.TAG, "$charSequence") //실행순서를 파악하기 위한 로그이다.

            //검색이 필요없을 경우를 위해 원본 배열을 복제
            val filteredList: ArrayList<notice> = ArrayList<notice>()
            //공백제외 아무런 값이 없을 경우 -> 원본 배열의 리스트와 사이즈를 그대로 리턴
            if (filterString.trim { it <= ' ' }.isEmpty()) {
                results.values = noticeList
                results.count = noticeList.size
                return results
                //공백제외 2글자 이하인 경우 -> 이름으로만 검색
            } else if (filterString.trim { it <= ' ' }.length <= 3) {
                for (person in noticeList) {
                    if (person.title!!.contains(filterString)) {
                        filteredList.add(person)
                    }
                }

            }
            else {

                //반복문을 돌려 user리스트안의 모든 유저의 위도경도를 받아와 거리구하기 공식을 사용
                results.values = noticeList
                results.count = noticeList.size
            }
            // Log.i(ContentValues.TAG,"순서")
            results.values = filteredList //필터링된 리스트 리턴
            results.count = filteredList.size //필터링된 리스트의 사이즈를 리턴, 추후에 position으로 쓰임

            return results
        }

        @SuppressLint("NotifyDataSetChanged") //어뎁터에 값이 변경되었다는걸 알려준다
        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            filteredNotice.clear()
            filteredNotice.addAll(filterResults.values as ArrayList<notice>)
            notifyDataSetChanged()
        }
    }


    override fun getItemCount(): Int {//유저리스트가 몇개인가 회원가입 한사람 수
        return filteredNotice.size
    }

    class noticeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {// 누를수있는 뷰로 만들기
        val title = itemView.findViewById<TextView>(R.id.title)
        var menus = itemView.findViewById<androidx.cardview.widget.CardView>(R.id.menu)
    }

    override fun getFilter(): Filter {
        return noticeitemFilter
    }

}


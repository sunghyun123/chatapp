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


class UserAdapter(val context: Context, val userList: ArrayList<User>):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {//Adapter한개 정의
    private lateinit var  storage: FirebaseStorage
    private lateinit var  mAuth: FirebaseAuth
    private var mDbRef = FirebaseDatabase.getInstance().getReference()




        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {//화면에 띄울 뷰 한개 생성
            val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
            return UserViewHolder(view)



        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {// 뷰 생성시 홀더와 포지션값은 정해짐
            // 방금 생성한 뷰가 화면에서 몇번쨰인지포지션을통해 알아내고 user의정보가 들어있는
            // userlist도 순서는동일하기에 유저리스트[position]을 통해 값을 커런트 유저로 넣어줌


            val currentUser = userList[position]//커런트유저는 데이터임
            mAuth = FirebaseAuth.getInstance()
            var lon : Double = 1.0
            var lat : Double = 1.0
            mDbRef.child("user").child( mAuth.currentUser?.uid.toString()).child("lon").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) { //유저 데이터 데이스의 lon값을 받아옴
                    val userlon =
                        dataSnapshot.getValue<Double>()

                    lon = userlon!! //받아온 값을 저장
                    //Log.i(ContentValues.TAG, "$lon")

                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            })

            mDbRef.child("user").child( mAuth.currentUser?.uid.toString()).child("lat").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) { //lat값을 받아옴
                    val userlat =
                        dataSnapshot.getValue<Double>()
                    lat = userlat!!


                    var sum =  DistanceManager.getDistance(currentUser.lat!!, currentUser.lon!!,lat, lon ).toDouble()
                    sum /= 1000;
                    holder.location.text = "나와의 거리 "+sum.toString()+"Km"

                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
                }
            })



            storage = FirebaseStorage.getInstance()

            val fileName = currentUser.uid.toString()


            var profilefile = File.createTempFile("images","jpeg");



            var sref = storage.reference.child("article/photo").child(fileName)
            if(fileName != null) {
                sref.getFile(profilefile).addOnCompleteListener {
                    holder.apply {
                        Glide.with(context)
                            .load(File(profilefile.absolutePath))
                            .into(holder.profileimg);
                    }
                }
            }



            holder.textName.text = currentUser.name//커런트유터 데이터에서 화면인 홀더로 이름을 넘겨줘서 띄울수있게함
            holder.state.text = currentUser.State



            holder.itemView.setOnClickListener {//목록의 뷰를 누를시

                val intent = Intent(context,popActivity::class.java)// 화면전환할 액티비티 정의
                //전환할 엑티비티로 데이터 넘기기 받는쪽에선 getExtraa
                intent.putExtra("uid", currentUser.uid)
                intent.putExtra("name", currentUser.name)
                intent.putExtra("State", currentUser.State)
                intent.putExtra("benchWeight", currentUser.benchWeight)
                intent.putExtra("level", currentUser.level)
                intent.putExtra("profileImage", currentUser.profileImage)
                intent.putExtra("pullUpCount", currentUser.pullUpCount)
                intent.putExtra("squatWeight", currentUser.squatWeight)
//                intent.putExtra("bench",currentUser.)
                intent.putExtra("img",profilefile.absolutePath.toString())

                context.startActivity(intent)//환면전환하기.
            }

    }




    object DistanceManager {

        private const val R = 6372.8 * 1000

        /**
         * 두 좌표의 거리를 계산한다.
         *
         * @param lat1 위도1
         * @param lon1 경도1
         * @param lat2 위도2
         * @param lon2 경도2
         * @return 두 좌표의 거리(m)
         */
        fun getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)
            val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
            val c = 2 * asin(sqrt(a))
            return (R * c).toInt()
        }
    }


    override fun getItemCount(): Int {//유저리스트가 몇개인가 회원가입 한사람 수
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){// 누를수있는 뷰로 만들기

        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        val state = itemView.findViewById<TextView>(R.id.state)
        val location = itemView.findViewById<TextView>(R.id.location)
        val profileimg = itemView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.img_rv_photo)

    }

}
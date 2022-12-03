package com.example.chatapplication


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
    RecyclerView.Adapter<UserAdapter.UserViewHolder>(),Filterable{//Adapter한개 정의,
    private lateinit var  storage: FirebaseStorage
    private lateinit var  mAuth: FirebaseAuth
    private var mDbRef = FirebaseDatabase.getInstance().getReference()
    private lateinit var namelist : ArrayList<String>
    var filteredDistance = ArrayList<User>()

    var TAG = "UserAdapter"
    var itemFilter = ItemFilter()


    var mPosition = 0
    var mSum = 0
    var mLon = 0.0
    var mLat = 0.0


    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){// 누를수있는 뷰로 만들기

        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        val state = itemView.findViewById<TextView>(R.id.state)
        val location = itemView.findViewById<TextView>(R.id.location)
        val profileimg = itemView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.img_rv_photo)

    }
    init {
        filteredDistance.addAll(userList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {//화면에 띄울 뷰 한개 생성
        val con = parent.context
        val inflater = con.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)



    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {// 뷰 생성시 홀더와 포지션값은 정해짐
            // 방금 생성한 뷰가 화면에서 몇번쨰인지포지션을통해 알아내고 user의정보가 들어있는
            // userlist도 순서는동일하기에 유저리스트[position]을 통해 값을 커런트 유저로 넣어줌
            val pos = position
            setPosition(position)

            val currentUser = filteredDistance[position]//커런트유저는 데이터임
            mAuth = FirebaseAuth.getInstance()
            var lon : Double = 1.0
            var lat : Double = 1.0
            mDbRef.child("user").child( mAuth.currentUser?.uid.toString()).child("lon").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) { //유저 데이터 데이스의 lon값을 받아옴
                    val userlon =
                        dataSnapshot.getValue<Double>()

                    lon = userlon!! //받아온 값을 저장
                    setLon(lon)
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

    fun removeitem(position: Int){
        if(position > 0){
            userList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    fun getPosition(): Int {
        return mPosition
    }

    fun setPosition(position: Int){
        mPosition = position
    }
    fun setSum(sum: Int){
        mSum = sum
    }
    fun getSum() : Int{
        return mSum
    }
    fun setLon(lon: Double){
        mLon = lon
    }
    fun setLat(lat: Double){
        mLat = lat
    }
    fun getuserlon() : Double{
        return mLon
    }
    fun getuserlat() : Double{
        return mLat
    }



    inner class ItemFilter : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val filterString = charSequence.toString()
            val results = FilterResults()
            Log.i(ContentValues.TAG, "$charSequence")

            //검색이 필요없을 경우를 위해 원본 배열을 복제
            val filteredList: ArrayList<User> = ArrayList<User>()
            //공백제외 아무런 값이 없을 경우 -> 원본 배열
            if (filterString.trim { it <= ' ' }.isEmpty()) {
                results.values = userList
                results.count = userList.size
                return results
                //공백제외 2글자 이하인 경우 -> 이름으로만 검색
            } else if(filterString.trim { it <= ' ' }.length <= 2 && filterString != "10"){
                for (person in userList) {
                    if (person.name!!.contains(filterString)) {
                        filteredList.add(person)
                    }
                }
                //그 외의 경우(공백제외 2글자 초과) -> 이름/전화번호로 검색
            }
            else {
                var lon4 : Double? = 1.0
                var lat4 : Double? = 1.0
                lon4 = getuserlon()

                lat4 = getuserlat()
                mAuth = FirebaseAuth.getInstance()
                Log.i(ContentValues.TAG, "$lon4")
                for (person in userList) {

//                    mAuth = FirebaseAuth.getInstance()
//                    mDbRef.child("user").child( mAuth.currentUser?.uid.toString()).child("lon").addValueEventListener(object : ValueEventListener {
//                        override fun onDataChange(dataSnapshot: DataSnapshot) { //유저 데이터 데이스의 lon값을 받아옴
//                            val userlon =
//                            dataSnapshot.getValue<Double>()
//                            lon4 = userlon
//
//                            //Log.i(ContentValues.TAG, "$lon4")
//
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            // Failed to read value
//                            Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
//
//                        }
//                    })
//
//                    mDbRef.child("user").child( mAuth.currentUser?.uid.toString()).child("lat").addValueEventListener(object : ValueEventListener {
//                        override fun onDataChange(dataSnapshot: DataSnapshot) { //lat값을 받아옴
//                            val userlat =
//                                dataSnapshot.getValue<Double>()
//                            lat4 = userlat!!
//
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            // Failed to read value
//                            Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
//                        }
                    val msum = DistanceManager.getDistance(person.lat!!, person.lon!!, lat4!!, lon4!! ).toInt()
                    //Log.i(ContentValues.TAG,"$msum")
                    if (filterString.toInt() > msum ) {
                        //Log.i(ContentValues.TAG,"$filterString")
                        filteredList.add(person)
                    }

                }
            }
           // Log.i(ContentValues.TAG,"순서")
            results.values = filteredList
            results.count = filteredList.size

            return results
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            filteredDistance.clear()
            filteredDistance.addAll(filterResults.values as ArrayList<User>)
            notifyDataSetChanged()
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
        return filteredDistance.size
    }



    override fun getFilter(): Filter {
        return itemFilter
    }


}
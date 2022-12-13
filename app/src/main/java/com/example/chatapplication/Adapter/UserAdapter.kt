package com.example.chatapplication.Adapter


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapplication.ChattActivity
import com.example.chatapplication.R
import com.example.chatapplication.View.User
import com.example.chatapplication.popActivity
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
        val gender = itemView.findViewById<TextView>(R.id.sex)
        var star = itemView.findViewById<ImageView>(R.id.star)
    }
    init {
        filteredDistance.addAll(userList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {//화면에 띄울 뷰 한개 생성


        val con = parent.context
        val inflater = con.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)

//        val useview =  view.findViewById<LinearLayout>(R.id.userview)
//
//
//        var bgShape : GradientDrawable = useview.background as GradientDrawable
//        bgShape.setColor(Color.BLUE)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {// 뷰 생성시 홀더와 포지션값은 정해짐
            // 방금 생성한 뷰가 화면에서 몇번쨰인지포지션을통해 알아내고 user의정보가 들어있는
            // userlist도 순서는동일하기에 유저리스트[position]을 통해 값을 커런트 유저로 넣어줌
            val pos = position

            setPosition(position)
            if(position != 0) holder.star.visibility = View.GONE

            val currentUser = filteredDistance[position]//커런트유저는 데이터임
            mAuth = FirebaseAuth.getInstance()
            var lon : Double = 1.0
            var lat : Double = 1.0
            mDbRef.child("user").child( mAuth.currentUser?.uid.toString()).child("lon").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) { //유저 데이터 데이스의 lon값을 받아옴
                    val userlon =
                        dataSnapshot.getValue<Double>()

                    lon = userlon!!
                    setLon(lon) //데이터베이스에서 읽어온 경도를 전역변수에 저장하는 함수
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
                    setLat(lat) //데이터베이스에서 읽어온 위도를 전역변수에 저장하는 함수
                    var sum =  DistanceManager.getDistance(
                        currentUser.lat!!,
                        currentUser.lon!!,
                        lat,
                        lon
                    ).toDouble()

                    //여기서 뷰홀더를 호출하는 이유는 데이터베이스에서 값을 읽어오는 것이 함수내에서 무조건 후순위로 배치되기때문에 밑에 다른 뷰홀더들과 같은 곳에서 호출하면
                    //데이터베이스에서 값을 읽어오지 못하고 출력을 하게되므로 이곳에서 호출

                    if(sum >= 1000) { //거리가 1000이상이면 km단위로 출력
                        sum = sum / 1000
                        holder.location.text = "나와의 거리 " + sum.toString() + "Km"
                    }
                    else //1000보다 작다면 m단위로 출력
                        holder.location.text = "나와의 거리 " + (sum.toInt()).toString() + "m"


                }

                override fun onCancelled(error: DatabaseError) { //예외처리를 하긴 했으나 데이터베이스에 null값이 생성되면 강제종료되는 버그는 아직 존재
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

            holder.state.text = currentUser.State //현재 상태 띄우기
            holder.gender.text = currentUser.sex //성별띄우기



            holder.itemView.setOnClickListener {//목록의 뷰를 누를시

                val intent = Intent(context, popActivity::class.java)// 화면전환할 액티비티 정의
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

                val intent1 = Intent(context, ChattActivity::class.java)
                intent1.putExtra("uid", currentUser.uid)
                intent1.putExtra("name", currentUser.name)

            }

    }

    fun removeitem(position: Int){
        if(position > 0){
            userList.removeAt(position)
            notifyDataSetChanged()
        }
    }

    fun getPosition(): Int { //onbindviewholder에서 list의 인덱스를 얻어오는 함수, 아직 쓰진 않지만 나중에 필요할 수도 있음
        return mPosition
    }

    fun setPosition(position: Int){ //onbindviewholder에서 현재인덱스를 설정해주는 함수
        mPosition = position
    }
    fun setSum(sum: Int){ //거리값을 설정해주는 함수지만 현재는 쓸일이 없음, 삭제해도 무방
        mSum = sum
    }
    fun getSum() : Int{ //위와 마찬가지
        return mSum
    }
    fun setLon(lon: Double){ //현재 로그인된 유저(나)의 경도(lon)값을 리얼타임 데이터베이스에서 읽어온뒤 설정해준다.
        mLon = lon
    }
    fun setLat(lat: Double){ //위도(lat)값
        mLat = lat
    }
    fun getuserlon() : Double{ //현재 로그인된 유저의 경도값 리턴
        return mLon
    }
    fun getuserlat() : Double{//현재 로그인된 유저의 위도값 리턴
        return mLat
    }



    inner class ItemFilter : Filter() { // 리사이클러뷰 필터링 클래스, 매개변수로 전달받은 문자열에 따라 리스트를 필터링 해준다. 다른액티비티에서도 adapter를 통해 호출가능, 밑에있는 getFilter함수를 써야한다.
        override fun performFiltering(charSequence: CharSequence): FilterResults { //charSequence란 문자열을 매개변수로 받음 현재는 이름 또는 거리를 매개변수로 받아 필터링한다.
            val filterString = charSequence.toString() //원본 문자열을 toString 형태로 변수에 저장
            val results = FilterResults() //리턴할 결과값 변수 생성
            Log.i(ContentValues.TAG, "$charSequence") //실행순서를 파악하기 위한 로그이다.

            //검색이 필요없을 경우를 위해 원본 배열을 복제
            val filteredList: ArrayList<User> = ArrayList<User>()
            //공백제외 아무런 값이 없을 경우 -> 원본 배열의 리스트와 사이즈를 그대로 리턴
            if (filterString.trim { it <= ' ' }.isEmpty()) {
                results.values = userList
                results.count = userList.size
                return results
                //공백제외 2글자 이하인 경우 -> 이름으로만 검색 단, 거리로 전달받은 10은 밑에서 처리해야하기 때문에 예외처리 해준다
            } else if(filterString.trim { it <= ' ' }.length <= 2 && filterString != "10"){
                for (person in userList) {
                    if (person.name!!.contains(filterString)) {
                        filteredList.add(person)
                    }
                }

            }
            //그 외의 경우(공백제외 2글자 초과하는 경우) -> 이 경우에는 필터링이 문자열을 거리로 인식하는걸로 하였음, 그래서 검색창에서 10,100 등을 입력하여도 거리로 필터링된다.
            //어차피 이름이 10,100은 아닐테니 그냥 냅둬도 상관없을 것 같다.
            else {
                var lon4 : Double? = 1.0
                var lat4 : Double? = 1.0
                lon4 = getuserlon() //현재 유저의 경도를 받아옴
                lat4 = getuserlat() //현재 유저의 위도를 받아옴

                //반복문을 돌려 user리스트안의 모든 유저의 위도경도를 받아와 거리구하기 공식을 사용
                for (person in userList) {
                    val msum =
                        DistanceManager.getDistance(person.lat!!, person.lon!!, lat4!!, lon4!!)

                    //만약에 설정한 거리보다 현재 거리가 작거나 같을시에만 거리 표시
                    if (filterString.toInt() >= msum ) {
                        filteredList.add(person)
                    }

                }
            }
           // Log.i(ContentValues.TAG,"순서")
            results.values = filteredList //필터링된 리스트 리턴
            results.count = filteredList.size //필터링된 리스트의 사이즈를 리턴, 추후에 position으로 쓰임

            return results
        }

        @SuppressLint("NotifyDataSetChanged") //어뎁터에 값이 변경되었다는걸 알려준다
        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            filteredDistance.clear()
            filteredDistance.addAll(filterResults.values as ArrayList<User>)
            notifyDataSetChanged()
        }

    }
    //거리구하기 오브젝트
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

    //인덱스 설정
    override fun getItemCount(): Int {//유저리스트가 몇개인가 회원가입 한사람 수
        return filteredDistance.size
    }


    //다른액티비티에서는 이 함수를 통해 위의 필터 함수를 호출한다.
    override fun getFilter(): Filter {
        return itemFilter
    }


}
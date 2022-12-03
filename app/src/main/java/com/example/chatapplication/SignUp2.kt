package com.example.chatapplication
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class SignUp2 : AppCompatActivity() {


    private lateinit var benchWight: Button//벤치 무게
    private lateinit var squatWight: Button//스쿼트 무게
    private lateinit var pullUpCount: Button// 풀업 갯수
    private lateinit var level: Button
    private lateinit var btnSignUp: Button
    var bench : Int? = -1
    var squat : Int? = -1
    var pullup : Int? = -1
    var lev : Int? = -1
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //상태 표시 줄을 감춘다(로그아웃기능 감춤)
        setContentView(R.layout.activity_sign_up2)
        benchWight = findViewById(R.id.edt_BenchPower)
        squatWight = findViewById(R.id.edt_SqutPower)
        pullUpCount = findViewById(R.id.edt_PullUpPower)
        btnSignUp = findViewById(R.id.btnSignUp)
        level = findViewById(R.id.edt_Level)



        benchWight.setOnClickListener {
            var layout = layoutInflater.inflate(R.layout.dialog_num_select, null)
            var build = AlertDialog.Builder(it.context).apply {
                setView(layout)
            }
            val dialog = build.create()
            dialog.show()
            layout.findViewById<NumberPicker>(R.id.number_picker).minValue = 0;
            layout.findViewById<NumberPicker>(R.id.number_picker).maxValue = 21;
            layout.findViewById<NumberPicker>(R.id.number_picker).displayedValues = arrayOf(
                "0", "10", "20", "30", "40", "50", "60","70", "80", "90", "100", "110", "120",
                "130", "140", "150", "160", "170", "180","190", "200", "210"
            )
            layout.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            layout.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                bench =  layout.findViewById<NumberPicker>(R.id.number_picker).value *10
                benchWight.text = "벤치프레스 무게 : " + bench.toString()
                dialog.dismiss()
            }

        }
        squatWight.setOnClickListener {
            var layout = layoutInflater.inflate(R.layout.dialog_num_select, null)
            var build = AlertDialog.Builder(it.context).apply {
                setView(layout)
            }
            val dialog = build.create()
            dialog.show()

            layout.findViewById<NumberPicker>(R.id.number_picker).minValue = 0;
            layout.findViewById<NumberPicker>(R.id.number_picker).maxValue = 30;
            layout.findViewById<NumberPicker>(R.id.number_picker).displayedValues = arrayOf(
                "0","10", "20", "30", "40", "50", "60","70", "80", "90", "100", "110", "120",
                "130", "140", "150", "160", "170", "180","190", "200", "210","220","230", "240", "250",
                "260","270", "280", "290","300"
            )
            layout.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            layout.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                squat =  layout.findViewById<NumberPicker>(R.id.number_picker).value * 10
                squatWight.text = "스쿼트 무게 : " + squat.toString()
                dialog.dismiss()
            }
        }
        pullUpCount.setOnClickListener {
            var layout = layoutInflater.inflate(R.layout.dialog_num_select, null)
            var build = AlertDialog.Builder(it.context).apply {
                setView(layout)
            }
            val dialog = build.create()
            dialog.show()

            layout.findViewById<NumberPicker>(R.id.number_picker).minValue = 0
            layout.findViewById<NumberPicker>(R.id.number_picker).maxValue = 40

            layout.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            layout.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                pullup =  layout.findViewById<NumberPicker>(R.id.number_picker).value
                pullUpCount.text = "풀업 개수 : " + pullup.toString()
                dialog.dismiss()
            }
        }
        level.setOnClickListener {
            var layout = layoutInflater.inflate(R.layout.dialog_num_select, null)
            var build = AlertDialog.Builder(it.context).apply {
                setView(layout)
            }
            val dialog = build.create()
            dialog.show()

            layout.findViewById<NumberPicker>(R.id.number_picker).minValue = 0
            layout.findViewById<NumberPicker>(R.id.number_picker).maxValue = 20

            layout.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            layout.findViewById<Button>(R.id.btn_ok).setOnClickListener {
                lev =  layout.findViewById<NumberPicker>(R.id.number_picker).value
                level.text = "운동 경력 : " + lev.toString()
                dialog.dismiss()
            }

        }

        btnSignUp.setOnClickListener {
            myStartActivity()
        }
    }

    private fun myStartActivity() {
        if (bench!= -1 && squat!= -1 && pullup!= -1 &&   lev!= -1) {
            val nextIntent = Intent(this@SignUp2, SignUp3::class.java)
            nextIntent.putExtra("uName1", intent.getStringExtra("uName").toString())
            nextIntent.putExtra("uEmail1", intent.getStringExtra("uEmail").toString())
            nextIntent.putExtra("uPw1", intent.getStringExtra("uPw").toString())
            nextIntent.putExtra("photo1", intent.getStringExtra("photo").toString())
            nextIntent.putExtra("benchWight1", bench.toString())
            nextIntent.putExtra("squatWight1", squat.toString())
            nextIntent.putExtra("pullUpCount1", pullup.toString())
            nextIntent.putExtra("lev", lev.toString())
            startActivity(nextIntent)

        }
        else{
            Toast.makeText(this,"there is empty space", Toast.LENGTH_SHORT).show()
        }
    }
}
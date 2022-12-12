package com.example.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast

class numberSelectActivity : AppCompatActivity() {
    private lateinit var btnok: Button
    private lateinit var np : NumberPicker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_select)
        val min = intent.getStringExtra("min")
        val max = intent.getStringExtra("max")
        val valueName = intent.getStringExtra("valueName")
        val ActivityName = intent.getStringExtra("atname").toString()

        btnok = findViewById(R.id.btn_ok)
        np = findViewById(R.id.number_picker)
        np.minValue = min!!.toInt()
        np.maxValue = max!!.toInt()
        np.wrapSelectorWheel = false
        np.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        btnok.setOnClickListener {
            val nextIntent = Intent(this@numberSelectActivity, ActivityName::class.java)
            nextIntent.putExtra(valueName.toString(),np.value)
            startActivity(nextIntent)
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
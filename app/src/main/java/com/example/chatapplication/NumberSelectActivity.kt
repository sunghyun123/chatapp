package com.example.chatapplication.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import com.example.chatapplication.R

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
}
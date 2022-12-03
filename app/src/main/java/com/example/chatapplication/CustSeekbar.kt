package com.example.chatapplication

import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView

class CustSeekbar(context: Context) {
    private  val dialog = Dialog(context)

    fun showSeekbar(){
        dialog.setContentView(R.layout.seekbar)

        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)


        val cusSeekBar = dialog.findViewById<SeekBar>(R.id.seekBar)
        val textView = dialog.findViewById<TextView>(R.id.seedistance)
        val okbtn = dialog.findViewById<Button>(R.id.okbtn)
        val canbtn =  dialog.findViewById<Button>(R.id.canbtn)



//            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                    textView.text = progress.toString()
//                }
//
//                override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                }
//
//                override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                }
//            }
        cusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var d : Float = ((progress*500).toFloat())/1000
                textView.text = d.toString() + "km"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        okbtn.setOnClickListener {
            onClickListener.onClicked(textView.text.toString())
            dialog.dismiss()
        }

        canbtn.setOnClickListener {
            dialog.dismiss()
        }



        dialog.show()
    }


    interface ButtonClickListener{
        fun onClicked(text: String)
    }



    private lateinit var onClickListener: ButtonClickListener


    fun setOnClickListener(listener: ButtonClickListener){
        onClickListener = listener
    }


}
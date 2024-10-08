package com.example.weatherwear

import android.app.Dialog
import android.content.Context
import android.widget.Button

class ClothingPopup(context: Context) : Dialog(context) {

    private val shutdownClick: Button

    init {
        setContentView(R.layout.activity_clothing_popup)

        shutdownClick = findViewById(R.id.btn_selectClothing)
        shutdownClick.setOnClickListener {
            dismiss() // 팝업 닫기
        }
    }
}

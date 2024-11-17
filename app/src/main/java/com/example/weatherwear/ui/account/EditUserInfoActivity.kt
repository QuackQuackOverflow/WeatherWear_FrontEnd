package com.example.weatherwear.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R

class EditUserInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_info)

        // 기존 사용자 정보 예시 데이터
        val currentUserName = "홍길동"
        val currentUserId = "user123"
        val currentSensitivity = "추위를 잘 탐" // "더위를 잘 탐", "보통" 가능

        // UI 요소 초기화
        val editUserName = findViewById<EditText>(R.id.editUserName)
        val editUserId = findViewById<EditText>(R.id.editUserId)
        val coldButton = findViewById<RadioButton>(R.id.rbtn_userFeelsColdWell)
        val avgButton = findViewById<RadioButton>(R.id.rbtn_userFeelsAvg)
        val heatButton = findViewById<RadioButton>(R.id.rbtn_userFeelsHotWell)
        val saveButton = findViewById<Button>(R.id.saveUserInfoButton)

        // 기존 사용자 정보로 초기화
        editUserName.setText(currentUserName)
        editUserId.setText(currentUserId)
        when (currentSensitivity) {
            "추위를 잘 탐" -> coldButton.isChecked = true
            "보통" -> avgButton.isChecked = true
            "더위를 잘 탐" -> heatButton.isChecked = true
        }

        // 저장 버튼 클릭 이벤트
        saveButton.setOnClickListener {
            val updatedName = editUserName.text.toString().trim()
            val updatedId = editUserId.text.toString().trim()
            val updatedSensitivity = when {
                coldButton.isChecked -> "추위를 잘 탐"
                avgButton.isChecked -> "보통"
                heatButton.isChecked -> "더위를 잘 탐"
                else -> ""
            }

            if (updatedName.isNotEmpty() && updatedId.isNotEmpty()) {
                // 서버로 업데이트된 정보를 전송하거나 SharedPreferences에 저장
                Toast.makeText(
                    this,
                    "정보가 저장되었습니다: $updatedName, $updatedId, $updatedSensitivity",
                    Toast.LENGTH_SHORT
                ).show()
                finish() // 저장 후 액티비티 종료
            } else {
                Toast.makeText(this, "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

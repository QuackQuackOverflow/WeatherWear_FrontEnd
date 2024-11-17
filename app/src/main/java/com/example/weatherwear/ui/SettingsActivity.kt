package com.example.weatherwear.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.ui.account.LoginActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 예시 데이터 설정
        val userName = "홍길동"
        val userId = "user123"
        val coldSensitivity = "높음"

        // 사용자 정보 표시
        findViewById<TextView>(R.id.main).apply {
            findViewById<TextView>(R.id.textViewUserName).text = "사용자 이름: $userName"
            findViewById<TextView>(R.id.textViewUserId).text = "ID: $userId"
            findViewById<TextView>(R.id.textViewColdSensitivity).text = "추위를 잘 탐: $coldSensitivity"
        }

        // 로그아웃 버튼 클릭 이벤트
        findViewById<Button>(R.id.logoutButton).setOnClickListener {
            // 로그아웃 처리 (SharedPreferences 초기화 등)
            Toast.makeText(this, "로그아웃되었습니다.", Toast.LENGTH_SHORT).show()
            // 로그인 화면으로 이동
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        // 사용자 정보 수정 버튼 클릭 이벤트
        findViewById<Button>(R.id.editUserInfoButton).setOnClickListener {
            // 사용자 정보 수정 화면으로 이동
            Toast.makeText(this, "사용자 정보 수정 화면으로 이동합니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, EditUserInfoActivity::class.java)
            startActivity(intent)
        }
    }
}

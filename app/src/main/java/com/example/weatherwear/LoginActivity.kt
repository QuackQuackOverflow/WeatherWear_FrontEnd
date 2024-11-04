package com.example.weatherwear

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 계정 생성 버튼 클릭 시 RegisterActivity로 전환
        val createAccountButton = findViewById<Button>(R.id.createAccountButton) // 계정 생성 버튼 ID
        createAccountButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }


        // '메인화면 바로 가기' 버튼 클릭 시 MainActivity로 전환

        val moveToMainButton = findViewById<Button>(R.id.moveToMainButton) // 메인화면 바로가기 버튼 ID
        moveToMainButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }

    }
}
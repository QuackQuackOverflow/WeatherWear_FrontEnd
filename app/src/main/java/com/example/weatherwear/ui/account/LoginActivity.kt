package com.example.weatherwear.ui.account

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.data.model.User
import com.example.weatherwear.repository.UserRepository
import com.example.weatherwear.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    // UserRepository 인스턴스 생성 (서버 통신용)
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // ID와 비밀번호 입력 필드 가져오기
        val idEditText = findViewById<EditText>(R.id.editTextID)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)

        // 로그인 버튼 클릭 시 서버로 로그인 요청 보내기
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val id = idEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // 로그인 함수 호출
            login(id, password)
        }

        // 계정 생성 버튼 클릭 시 RegisterActivity로 전환
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        createAccountButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        // '메인화면 바로 가기' 버튼 클릭 시 MainActivity로 전환
        val moveToMainButton = findViewById<Button>(R.id.moveToMainButton)
        moveToMainButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // 로그인 요청을 서버로 보내는 함수
    private fun login(id: String, password: String) {
        // User 객체를 생성하여 서버로 보낼 데이터 준비
        val user = User(username = "", id = id, password = password, temperaturePreference = "average")

        // 비동기 작업 수행 (네트워크 요청)
        CoroutineScope(Dispatchers.IO).launch {
            val response = userRepository.loginUser(user)
            if (response.isSuccessful) {
                // 로그인 성공 시 메인 화면으로 이동
                val loggedInUser = response.body()
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "로그인 성공: ${loggedInUser?.username}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish() // 로그인 후 현재 액티비티 종료
                }
            } else {
                // 로그인 실패 시 메시지 표시
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "로그인 실패: ID 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

package com.example.weatherwear.ui.account

import LoginHelper
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherwear.R
import com.example.weatherwear.data.model.User
import com.example.weatherwear.data.model.Member
import com.example.weatherwear.repository.UserRepository
import com.example.weatherwear.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 자동 로그인 시도
        autoLogin()

        // UI 요소 초기화
        val idEditText = findViewById<EditText>(R.id.editTextID)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        val moveToMainButton = findViewById<Button>(R.id.moveToMainButton)
        val connectServerTestButton = findViewById<Button>(R.id.connectServerTestButton)

        // 로그인 버튼 클릭 이벤트
        loginButton.setOnClickListener {
            val id = idEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateInputs(id, password)) {
                login(id, password)
            }
        }

        // 계정 생성 버튼 클릭 이벤트
        createAccountButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

//        // 메인 화면 이동 버튼 클릭 이벤트
//        moveToMainButton.setOnClickListener {
//            val intent = Intent(this@LoginActivity, MainActivity::class.java)
//            startActivity(intent)
//        }
//
//        // 서버 연결 테스트 버튼 클릭 이벤트
//        connectServerTestButton.setOnClickListener {
//            testServerConnection()
//        }
    }

    // 입력값 검증 함수
    private fun validateInputs(id: String, password: String): Boolean {
        return when {
            id.isEmpty() -> {
                showToast("ID를 입력하세요")
                false
            }
            password.isEmpty() -> {
                showToast("비밀번호를 입력하세요")
                false
            }
            else -> true
        }
    }

    // 로그인 요청을 서버로 보내는 함수
    private fun login(id: String, password: String) {
        val user = User(
            memberEmail = id,
            memberPassword = password,
            memberName = "",
            userType = ""
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userRepository.loginUser(user)

                if (response.isSuccessful) {
                    response.body()?.let { member ->
                        saveLoginInfo(member)
                        runOnUiThread {
                            showToast("로그인 성공: ${member.memberName}")
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    } ?: runOnUiThread {
                        showToast("로그인 실패: 잘못된 ID 또는 비밀번호")
                    }
                } else {
                    val errorMessage = parseErrorMessage(response)
                    runOnUiThread { showToast(errorMessage) }
                }
            } catch (e: Exception) {
                runOnUiThread { showToast("네트워크 오류 발생!") }
            }
        }
    }

    // 자동 로그인 함수
    private fun autoLogin() {
        val loginHelper = LoginHelper(this)
        val loginData = loginHelper.getLoginInfo()

        loginData?.let { data ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = userRepository.loginUser(User(data.email, data.password, "", ""))
                    if (response.isSuccessful && response.body() != null) {
                        runOnUiThread {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        runOnUiThread { showToast("자동 로그인 실패: 다시 로그인해주세요.") }
                    }
                } catch (e: Exception) {
                    runOnUiThread { showToast("네트워크 오류 발생: ${e.message}") }
                }
            }
        }
    }

    // 로그인 정보 저장 함수
    private fun saveLoginInfo(member: Member) {
        val loginHelper = LoginHelper(this)
        loginHelper.saveLoginInfo(
            email = member.memberEmail,
            password = member.memberPassword,
            name = member.memberName,
            userType = member.userType
        )
    }

    // 서버 연결 테스트 함수
    private fun testServerConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = userRepository.testConnection()
                runOnUiThread {
                    if (response.isSuccessful) {
                        showToast("서버 연결에 성공했습니다!")
                    } else {
                        showToast("서버 연결 실패: 상태 코드 ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                runOnUiThread { showToast("서버 연결 오류 발생: ${e.message}") }
            }
        }
    }

    // 에러 메시지 파싱
    private fun parseErrorMessage(response: retrofit2.Response<*>): String {
        return try {
            JSONObject(response.errorBody()?.string() ?: "{}").getString("message")
        } catch (e: Exception) {
            "로그인 실패"
        }
    }

    // Toast 메시지 간소화
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

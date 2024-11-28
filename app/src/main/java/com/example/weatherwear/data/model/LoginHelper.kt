import android.content.Context
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class LoginHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
    private val secretKey: SecretKey = generateKey()

    /**
     * AES SecretKey 생성
     */
    private fun generateKey(): SecretKey {
        // 고정 키를 사용할 경우 아래를 주석 해제하고 고정된 키로 초기화
        // val key = "your-secret-key-here".toByteArray()
        // return SecretKeySpec(key, "AES")

        // 동적 키 생성
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(128) // 128비트 키 생성
        return keyGen.generateKey()
    }

    /**
     * AES로 비밀번호 암호화
     */
    private fun encrypt(password: String): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(password.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    }

    /**
     * AES로 비밀번호 복호화
     */
    private fun decrypt(encryptedPassword: String): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decodedBytes = Base64.decode(encryptedPassword, Base64.DEFAULT)
        return String(cipher.doFinal(decodedBytes))
    }

    /**
     * 로그인 정보 저장
     */
    fun saveLoginInfo(email: String, password: String, name: String, userType: String) {
        val encryptedPassword = encrypt(password)
        sharedPreferences.edit().apply {
            putString("memberEmail", email)
            putString("memberPassword", encryptedPassword)
            putString("memberName", name)
            putString("userType", userType)
            apply()
        }
    }

    /**
     * 저장된 로그인 정보 가져오기
     */
    fun getLoginInfo(): LoginData? {
        val email = sharedPreferences.getString("memberEmail", null)
        val encryptedPassword = sharedPreferences.getString("memberPassword", null)
        val name = sharedPreferences.getString("memberName", null)
        val userType = sharedPreferences.getString("userType", null)

        if (email != null && encryptedPassword != null) {
            val decryptedPassword = decrypt(encryptedPassword)
            return LoginData(email, decryptedPassword, name, userType)
        }
        return null
    }

    /**
     * 로그인 정보 삭제
     */
    fun clearLoginInfo() {
        sharedPreferences.edit().clear().apply()
    }

    /**
     * 데이터 클래스: 로그인 정보
     */
    data class LoginData(
        val email: String,
        val password: String,
        val name: String?,
        val userType: String?
    )
}

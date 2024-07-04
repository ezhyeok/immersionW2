package com.example.tab3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 카카오 SDK 초기화
        KakaoSdk.init(this, "YOUR_KAKAO_APP_KEY")

        val kakaoLoginButton: Button = findViewById(R.id.kakaoLoginButton)
        kakaoLoginButton.setOnClickListener {
            UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
                if (error != null) {
                    Toast.makeText(this, "Login Failed: ${error.message}", Toast.LENGTH_SHORT).show()
                } else if (token != null) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    Log.d("KakaoLogin", "AccessToken: ${token.accessToken}")

                    // 로그인 성공 시 MainActivity로 이동
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}

package com.example.tab3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private val clientId = "MTMDmtzwH6YMOms5qrAx"//"YOUR_CLIENT_ID"
    private val clientSecret = "MfynvpFnt3"//"YOUR_CLIENT_SECRET"
    private val redirectUri = "ping"//"YOUR_REDIRECT_URI"
    private val authUrl = "https://nid.naver.com/oauth2.0/authorize"
    private val tokenUrl = "https://nid.naver.com/oauth2.0/token"
    private val userInfoUrl = "https://openapi.naver.com/v1/nid/me"
    private val state = "RANDOM_STATE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val naverLoginButton: Button = findViewById(R.id.naverLoginButton)
        naverLoginButton.setOnClickListener {
            val authIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("$authUrl?response_type=code&client_id=$clientId&redirect_uri=$redirectUri&state=$state")
            )
            startActivity(authIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        val uri = intent.data
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            val code = uri.getQueryParameter("code")
            val state = uri.getQueryParameter("state")
            if (code != null && state != null) {
                getAccessToken(code)
            } else {
                Toast.makeText(this, "Failed to login", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAccessToken(code: String) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("client_id", clientId)
            .add("client_secret", clientSecret)
            .add("redirect_uri", redirectUri)
            .add("code", code)
            .add("state", state)
            .build()

        val request = Request.Builder()
            .url(tokenUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Failed to get access token", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody)
                    val accessToken = json.getString("access_token")
                    getUserInfo(accessToken)
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Failed to get access token", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun getUserInfo(accessToken: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(userInfoUrl)
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Failed to get user info", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val json = JSONObject(responseBody)
                    val responseJson = json.getJSONObject("response")
                    val email = responseJson.getString("email")
                    val nickname = responseJson.getString("nickname")

                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Welcome $nickname", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("email", email)
                        intent.putExtra("nickname", nickname)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Failed to get user info", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}


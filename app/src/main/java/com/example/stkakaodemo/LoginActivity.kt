package com.example.stkakaodemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.user.UserApiClient
import com.supertokens.session.SuperTokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setLoading(false)

        findViewById<Button>(R.id.btLoginKakao).setOnClickListener {
            setLoading(true)
            loginWithKakao()
        }
    }

    fun setLoading(loading: Boolean) {
        val button = findViewById<Button>(R.id.btLoginKakao)
        val loader = findViewById<View>(R.id.loader)

        if (loading) {
            loader.visibility = View.VISIBLE
            button.visibility = View.GONE
        } else {
            loader.visibility = View.GONE
            button.visibility = View.VISIBLE
        }
    }

    fun loginWithKakao() {
        UserApiClient.instance.loginWithKakaoAccount(this) { _, _ ->
            UserApiClient.instance.loginWithNewScopes(this, listOf("account_email")) { token, error ->
                if (error != null) {
                    Log.e(TAG, error.message ?: "ERROR")
                }

                if (token != null) {
                    Log.e(TAG, "Access token: " + token.accessToken)
                    Log.e(TAG, "ID token: " + token.idToken)

                    var body = JSONObject()
                    body.put("thirdPartyId", "kakao")
                    body.put("clientId", "...")
                    body.put("redirectURI", "redirectURI")

                    val authCodeResponse = JSONObject()
                    authCodeResponse.put("access_token", token.accessToken)
                    authCodeResponse.put("id_token", token.idToken ?: "")

                    body.put("authCodeResponse", authCodeResponse)

                    val requestBuilder = Request.Builder()
                    requestBuilder
                        .url("API_DOMAIN/auth/signinup")
                        .post(body.toString().toRequestBody())
                        .header("Content-Type", "application/json; charset=utf-8")

                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val response = NetworkProvider.getInstance(application).newCall(requestBuilder.build()).execute()

                            lifecycleScope.launch(Dispatchers.Main) {
                                if (response.code !== 200) {
                                    Toast.makeText(this@LoginActivity, "Something went wrong", Toast.LENGTH_LONG).show()
                                } else {
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    finish()
                                }
                            }

                        } catch (e: Exception) {
                            Log.e(TAG, "ERROR" + e.message ?: "")
                        }
                    }
                }
            }
        }
    }
}
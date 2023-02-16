package com.example.stkakaodemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.supertokens.session.SuperTokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setLoading(false)

        if (!SuperTokens.doesSessionExist(applicationContext)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<TextView>(R.id.tvUserId).text = SuperTokens.getUserId(applicationContext)

        findViewById<Button>(R.id.btSignout).setOnClickListener {
            setLoading(true)
            lifecycleScope.launch(Dispatchers.IO) {
                val requestBuilder = Request.Builder()
                requestBuilder
                    .url("http://192.168.29.87:3001/auth/signout")
                    .addHeader("rid", "session")
                    .post(JSONObject().toString().toRequestBody())

                NetworkProvider.getInstance(applicationContext).newCall(requestBuilder.build()).execute()

                lifecycleScope.launch(Dispatchers.Main) {
                    startActivity(Intent(this@MainActivity, SplashActivity::class.java))
                    finish()
                }
            }
        }
    }

    fun setLoading(loading: Boolean) {
        val button = findViewById<Button>(R.id.btSignout)
        val loader = findViewById<View>(R.id.loader)
        val content = findViewById<View>(R.id.llContent)

        if (loading) {
            loader.visibility = View.VISIBLE
            button.visibility = View.GONE
            content.visibility = View.GONE
        } else {
            loader.visibility = View.GONE
            button.visibility = View.VISIBLE
            content.visibility = View.VISIBLE
        }
    }
}
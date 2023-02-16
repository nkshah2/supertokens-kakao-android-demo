package com.example.stkakaodemo

import android.content.Context
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.supertokens.session.SuperTokensInterceptor
import okhttp3.OkHttpClient


class NetworkProvider {
    companion object {
        private var instance: OkHttpClient? = null

        fun getInstance(applicationContent: Context): OkHttpClient {
            if (instance == null) {
                val clientBuilder = OkHttpClient.Builder()
                clientBuilder.interceptors().add(SuperTokensInterceptor())
                clientBuilder.cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(applicationContent)))
                instance = clientBuilder.build()
            }

            return instance!!
        }
    }
}
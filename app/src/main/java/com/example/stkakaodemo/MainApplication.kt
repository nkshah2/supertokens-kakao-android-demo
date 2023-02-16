package com.example.stkakaodemo

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.supertokens.session.CustomHeaderProvider
import com.supertokens.session.SuperTokens
import com.supertokens.session.SuperTokensPersistentCookieStore
import java.net.CookieManager
import java.net.CookiePolicy


class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "0738d92ab58d6d7d76498f76ad586028")
        SuperTokens.init(
            this,
            "http://192.168.29.87:3001",
            null,
            null,
            null,
            null,
            null,
        )
        CookieManager.setDefault(
            CookieManager(
                SuperTokensPersistentCookieStore(this),
                CookiePolicy.ACCEPT_ALL
            )
        )
    }
}
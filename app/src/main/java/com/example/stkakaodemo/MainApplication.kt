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
        KakaoSdk.init(this, "...")
        SuperTokens.init(
            this,
            "API_DOMAIN",
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
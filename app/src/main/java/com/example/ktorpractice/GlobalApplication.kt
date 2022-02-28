package com.example.ktorpractice

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import java.lang.Math.log

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        // Kakao SDK 초기화
        KakaoSdk.init(this, "fd8fde38c46bcfda381e8b6b7c2ce500")
        // 키해시 구하기
        log(Utility.getKeyHash(this))
    }


}

fun log(str: Any?) = Log.d("gaeun", "$str")


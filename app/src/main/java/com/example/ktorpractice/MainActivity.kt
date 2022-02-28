package com.example.ktorpractice

import android.content.ContentValues.TAG
import android.media.session.MediaSession
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.ktorpractice.databinding.ActivityMainBinding
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.TokenManager
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo

class MainActivity : AppCompatActivity() {
    private val bind by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private fun needLogin(){
        bind.kakaologinbtn.visibility = View.VISIBLE

    }
    private fun kakaoLoginSuccess() {
        log("카카오 엑세스 토큰 : ${TokenManager.instance.getToken()?.accessToken}")
        // userid, pw 받는거지만 원래
        RetrofitClient.login("grace", "1016"){ call, response ->
            log(response.body())
            // 이게 그 유저
        }

    }

    // 카카오계정으로 로그인 공통 callback 구성
// 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error!= null && token != null) {
            kakaoLoginSuccess()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(bind){
            setContentView(root)

            //토큰 존재여부 확인
            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.accessTokenInfo { token, error ->
                    if (error != null) {
                        // 로그인 필요
                        needLogin()
                    }
                    else {
                        //토큰 유효성 체크 성공(필요 시 토큰 갱신됨) - 로그인 성공
                        kakaoLoginSuccess()
                    }
                }
            }
            else {
                //로그인 필요
                needLogin()
            }

            kakaologinbtn.setOnClickListener {
                // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@MainActivity)) {
                    UserApiClient.instance.loginWithKakaoTalk(this@MainActivity) { token, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡으로 로그인 실패", error)

                            // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                            // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                return@loginWithKakaoTalk
                            }

                            // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                            UserApiClient.instance.loginWithKakaoAccount(this@MainActivity, callback = callback)
                        } else if (token != null) {
                           kakaoLoginSuccess()
                        }
                    }
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(this@MainActivity, callback = callback)
                }



            }
        }
    }
}
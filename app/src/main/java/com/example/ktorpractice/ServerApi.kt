package com.example.ktorpractice

import com.practice.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ServerApi {
    @POST("/login")
    fun login(
        @Body
        user: User
    ): Call<User> // 받는게 없을 땐 void
}
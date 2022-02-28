package com.example.ktorpractice

import com.practice.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val instance = Retrofit.Builder().baseUrl("http://192.168.0.3:8082")
        .addConverterFactory(GsonConverterFactory.create()).build().create(ServerApi::class.java)

    fun login(
        id: String,
        password: String,
        onUnsuccessful: ((Call<User>, Response<User>) -> Unit)? = null,
        onSuccessful: (Call<User>, Response<User>) -> Unit
    ) = instance.login(User(id, password))
        .enqueue(defaultCallback(onUnsuccessful, onSuccessful))
    private fun <T> defaultCallback(
        onUnsuccessful: ((Call<T>, Response<T>) -> Unit)?,
        onSuccessful: (Call<T>, Response<T>) -> Unit
    ) = object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                onSuccessful(call, response)
            } else {
                onUnsuccessful?.run { this(call, response) } ?: log("실패: $response")
            }
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            log("Fail: ${t.message}")
        }
    }
}
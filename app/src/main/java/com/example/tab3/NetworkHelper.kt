package com.example.tab3

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log

object NetworkHelper {
    private const val NAVER_API_BASE_URL = "https://dapi.kakao.com/"

    private const val clientId="KabjHFsCzfE01lAvKE3E"
    private const val clientSecret="j_P240RULB"
    private const val kakaoKey="73a194bb75d5072a94090da00bf4af2c"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                //.header("X-Naver-Client-Id", clientId)
                .header("Authorization", "KakaoAK $kakaoKey")
                .method(original.method, original.body)
                .build()
            Log.d("OkHTTP", "request: ${chain.request()}")
            Log.d("OkHTTP", "request header: ${chain.request().headers}")

            val response=chain.proceed(request)
            Log.d("OkHTTP", "response : $response")
            Log.d("OkHTTP", "response header: ${response.headers}")
            response
        }
        .build()

    private val gson = GsonBuilder().setLenient().create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(NAVER_API_BASE_URL)
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    //val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)
    val retrofitService: NaverApiService = retrofit.create(NaverApiService::class.java)

}

/*
import android.os.Handler.createAsync
import android.util.Log
import androidx.core.os.HandlerCompat.createAsync
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import java.net.URL
import java.net.URLEncoder

import retrofit2.converter.gson.GsonConverterFactory

object NetworkHelper {
    private const val serverBaseUrl = "https://dreamtree-dywzy.run.goorm.io"

    var token: String = ""

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        })

        .addInterceptor {
            // Request
            val request = it.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            Log.d("OkHTTP", "request: ${it.request()}")
            Log.d("OkHTTP", "request header: ${it.request().headers}")

            // Response
            val response = it.proceed(request)

            Log.d("OkHTTP", "response : $response")
            Log.d("OkHTTP", "response header: ${response.headers}")
            response
        }.build()

    private val gson = GsonBuilder().setLenient().create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(serverBaseUrl)
        .client(okHttpClient)
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val retrofitService: RetrofitService = retrofit.create(RetrofitService::class.java)
}

 */

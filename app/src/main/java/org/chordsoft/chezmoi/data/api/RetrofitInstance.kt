package org.chordsoft.chezmoi.data.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Invocation
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    //private const val BASE_URL = "https://chezmoi-api.chordsoft.org/"
    private const val BASE_URL = "http://192.168.1.34:4000/"
    val api: ApiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(RetrofitInvocationLoggingInterceptor())
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .header("Accept-Encoding", "identity")
                    .build()
                chain.proceed(request)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }
}


class RetrofitInvocationLoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val invocation = request.tag(Invocation::class.java)
        val method = invocation?.method()
        val service = invocation?.service()

        val start = System.currentTimeMillis()

        Log.d("APICALL", "${request.method} ${request.url} (${service?.simpleName}.${method?.name})")

        val response = chain.proceed(request)

        val duration = System.currentTimeMillis() - start

        Log.d("APICALL","${response.code} ${request.url} (${duration}ms)")

        return response
    }
}

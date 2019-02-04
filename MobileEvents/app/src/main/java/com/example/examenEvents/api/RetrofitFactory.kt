package com.example.examenEvents.api

import com.google.gson.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.util.*
import java.util.concurrent.TimeUnit

class RetrofitFactory {
    private val okHttpClientBuilder = OkHttpClient.Builder()
        .connectTimeout(6000, TimeUnit.MILLISECONDS)
        .writeTimeout(6000, TimeUnit.MILLISECONDS)
        .readTimeout(6000, TimeUnit.MILLISECONDS).addInterceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .build()
            chain.proceed(newRequest)
        }


    fun getRetrofitInstance(): Retrofit {

//        val builder = GsonBuilder()
//            .registerTypeAdapter(Date::class.java, object : JsonDeserializer<Date> {
//                @Throws(JsonParseException::class)
//                override fun deserialize(
//                    jsonElement: JsonElement,
//                    type: Type,
//                    context: JsonDeserializationContext
//                ): Date {
//                    val string=jsonElement.asJsonPrimitive.asString
//                    val ind=string.indexOf("T")
//                    val date=string.substring(0,ind-1)
//                    return Date(date)
//                }
//            })
//            .create()
        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create()
        return Retrofit.Builder().client(okHttpClientBuilder.build())
            .baseUrl(API.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

}
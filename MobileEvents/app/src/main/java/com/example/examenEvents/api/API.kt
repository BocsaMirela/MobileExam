package com.example.examenEvents.api

import com.example.examenEvents.POJO.Event
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.DELETE
import retrofit2.http.PUT


interface API {

    @GET(".")
    fun getEvents(): Call<List<Event>>

//    @GET(".")
//    fun getEventsPaginated(@Query("lastUpdated") m: Long): Call<List<Event>>

    @GET(".")
    fun getEventsPaginated(@Query("lastUpdated") m: Long, @Query("page") page: Int): Call<Event>

    @POST(".")
    fun addEvent(@Body Event: Event): Call<Event>

    @PUT("{id}")
    fun updateEvent(@Path("id") id: Int, @Body Event: Event): Call<Event>

    @DELETE("{id}")
    fun deleteEvent(@Path("id") id: Int): Call<Event>

    @GET("check")
    fun check(): Call<String>

    companion object {
        val BASE_URL = AppResource.BASE_URL
        val IP = AppResource.IP
    }
}

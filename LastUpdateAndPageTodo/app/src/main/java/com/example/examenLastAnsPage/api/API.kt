package com.example.examenTodos.api

import com.example.examenTodos.POJO.Todo
import com.example.examenTodos.POJO.ServerResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.DELETE
import retrofit2.http.PUT


interface API {

    @GET(".")
    fun getTodos(): Call<List<Todo>>

//    @GET(".")
//    fun getTodosPaginated(@Query("lastUpdated") m: Long): Call<List<Todo>>

    @GET(".")
    fun getTodosPaginated(@Query("lastUpdated") m: Long, @Query("page") page: Int): Call<ServerResponse>

    @POST(".")
    fun addTodo(@Body Todo: Todo): Call<Todo>

    @PUT("{id}")
    fun updateTodo(@Path("id") id: Int, @Body Todo: Todo): Call<Todo>

    @DELETE("{id}")
    fun deleteTodo(@Path("id") id: Int): Call<Todo>

    @GET("check")
    fun check(): Call<String>

    companion object {
        val BASE_URL = AppResource.BASE_URL
        val IP = AppResource.IP
    }
}

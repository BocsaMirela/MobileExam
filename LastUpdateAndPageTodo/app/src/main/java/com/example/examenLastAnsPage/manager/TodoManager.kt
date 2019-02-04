package com.example.examenTodos.manager

import com.example.examenTodos.POJO.Todo
import com.example.examenTodos.POJO.ServerResponse
import com.example.examenTodos.api.API
import com.example.examenTodos.api.RetrofitFactory
import org.json.JSONObject
import retrofit2.Call

class TodoManager {
    private val api = RetrofitFactory()
        .getRetrofitInstance().create(API::class.java)

    fun getAllTodosServer(): Call<List<Todo>> {
        return api.getTodos()
    }

    fun getAllTodosServerUpdated(m: Long,nrPage:Int): Call<ServerResponse> {
        return api.getTodosPaginated(m,nrPage)
    }

    fun addTodoServer(event: Todo): Call<Todo> {
        return api.addTodo(event)
    }

    fun deleteTodoServer(id: Int): Call<Todo> {
        return api.deleteTodo(id)
    }

    fun updateTodoServer(event: Todo): Call<Todo> {
        return api.updateTodo(event.id, event)
    }

}
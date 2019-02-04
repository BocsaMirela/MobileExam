package com.example.examenTodos.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.widget.Toast
import com.example.examenTodos.POJO.ServerResponse
import com.example.examenTodos.POJO.Todo
import com.example.examenTodos.dao.TodoDatabase
import com.example.examenTodos.dao.TodoRepository
import com.example.examenTodos.manager.TodoManager
import org.json.JSONObject

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Array


class TodosViewModel(application: Application) : ViewModel() {

    private val todoManager = TodoManager()
    private val todoRepository: TodoRepository =
        TodoRepository.getInstance(TodoDatabase.getAppDatabase(application).todoDAO())

    var items: MutableLiveData<List<Todo>> = MutableLiveData()

    fun getTodosFromServerUpdatedAndPaginated(m: Long, nrPage: Int): Call<ServerResponse> {
        return todoManager.getAllTodosServerUpdated(m, nrPage)
    }

//    fun getAllTodosFromServer(): Call<List<ServerResponse>> {
//        return todoManager.getAllTodosServer()
//    }

    fun addTodoServer(context: Context, event: Todo) {
//        todoManager.addTodoServer(event).enqueue(object : Callback<Todo> {
//            override fun onFailure(call: Call<Todo>, t: Throwable) {
//                Toast.makeText(
//                    context,
//                    "Something went wrong or no connection! The new event will be saved in your local data",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//
//            override fun onResponse(call: Call<Todo>, response: Response<Todo>) {
//                Toast.makeText(context, "Add was done", Toast.LENGTH_LONG).show()
//                val listOfItems = items.value as ArrayList<Todo>
//                listOfItems.add(event)
//                items.value = listOfItems
//            }
//
//        })
    }

    fun deleteTodoServer(id: Int, context: Context) {
        todoManager.deleteTodoServer(id)
            .enqueue(object : Callback<Todo> {
                override fun onFailure(call: Call<Todo>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Something went wrong or no connection! Delete from local data",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(call: Call<Todo>, response: Response<Todo>) {
                    Toast.makeText(context, "Delete done", Toast.LENGTH_LONG).show()
                }

            })
    }

    fun updateTodoServer(todo: Todo): Call<Todo> {
        return todoManager.updateTodoServer(todo)
    }

//    fun updateTodoServer(context: Context, event: Todo) {
//        todoManager.updateTodoServer(event).enqueue(object : Callback<Todo> {
//            override fun onFailure(call: Call<Todo>, t: Throwable) {
//                Toast.makeText(
//                    context,
//                    "Something went wrong or no connection! Update from local data",
//                    Toast.LENGTH_LONG
//                ).show()
//                val listOfItems = items.value as ArrayList<Todo>
//                listOfItems.remove(event)
//                listOfItems.add(event)
//                items.value = listOfItems
//            }
//
//            override fun onResponse(call: Call<Todo>, response: Response<Todo>) {
//                Toast.makeText(context, "Update done", Toast.LENGTH_LONG).show()
//                val listOfItems = items.value as ArrayList<Todo>
//                listOfItems.remove(event)
//                listOfItems.add(event)
//                items.value = listOfItems
//
//            }
//
//        })
//    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TodosViewModel(application) as T
        }
    }

    fun getAllTodos(): List<Todo> {
        return todoRepository.getAllTodos()
    }

    fun addTodo(todo: Todo): Long {
        return todoRepository.addTodo(todo)
    }

    fun deleteTodo(todo: Todo) {
        return todoRepository.deleteTodo(todo)
    }

    fun updateTodo(todo: Todo) {
        todoRepository.updateTodo(todo)
    }
}

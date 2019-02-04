package com.example.examenTodos.dao

import android.os.AsyncTask
import android.util.Log
import com.example.examenTodos.POJO.Todo
import java.lang.Exception

class TodoRepository(private val todoDAO: TodoDAO) {

    fun getAllTodos(): List<Todo> {
        return LoadAsyncTask(todoDAO).execute().get()
    }

    fun deleteTodo(todo: Todo) {
        DeleteAsyncTask(todoDAO).execute(todo)
    }

    fun updateTodo(todo: Todo) {
        UpdateAsyncTask(todoDAO).execute(todo)
    }
    fun addTodo(todo: Todo): Long {
        return AddAsyncTask(todoDAO).execute(todo).get()
    }

    companion object {
        private var instance: TodoRepository? = null

        fun getInstance(todoDAO: TodoDAO): TodoRepository {
            if (instance == null) {
                instance =
                    TodoRepository(todoDAO)
            }
            return instance as TodoRepository
        }
    }

    private class LoadAsyncTask(val todoDAOTodo: TodoDAO) :
        AsyncTask<Void, Void, List<Todo>>() {
        override fun doInBackground(vararg params: Void?): List<Todo> {
            val choco = todoDAOTodo.getTodos()
            Log.e(" from db all ", choco.size.toString())
            return choco
        }

    }

    private class AddAsyncTask(val todoDAOTodo: TodoDAO) :
        AsyncTask<Todo, Void, Long>() {
        override fun doInBackground(vararg params: Todo?): Long {
            return try {
                todoDAOTodo.insert(params[0]!!)
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }
    }

    private class UpdateAsyncTask(val todoDAOTodo: TodoDAO) :
        AsyncTask<Todo, Void, Boolean>() {
        override fun doInBackground(vararg params: Todo?): Boolean {
            return try {
                todoDAOTodo.update(params[0]!!)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private class DeleteAsyncTask(val todoDAOTodo: TodoDAO) :
        AsyncTask<Todo, Void, Boolean>() {
        override fun doInBackground(vararg params: Todo?): Boolean {
            return try {
                todoDAOTodo.delete(params[0]!!)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

}

package com.example.examenTodos.dao

import android.arch.persistence.room.*
import com.example.examenTodos.POJO.Todo


@Dao
interface TodoDAO {

//    @Query("SELECT * FROM items")
//    fun getItems(): List<Todo>

    @Query("SELECT * FROM items  ORDER BY updated DESC")
    fun getTodos(): List<Todo>

    @Query("SELECT * FROM items")
    fun getTodosTest(): List<Todo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chocolate: Todo): Long

    @Delete
    fun delete(chocolate: Todo)

    @Update
    fun update(chocolate: Todo)

}

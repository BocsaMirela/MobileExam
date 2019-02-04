package com.example.examenTodos.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.examenTodos.POJO.Todo

@Database(entities = [Todo::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDAO(): TodoDAO

    companion object {
        private var INSTANCE: TodoDatabase? = null

        fun getAppDatabase(context: Context): TodoDatabase {
            if (INSTANCE == null) {
                INSTANCE =
                        Room.databaseBuilder(context.applicationContext, TodoDatabase::class.java, "DBtodos")
                            .allowMainThreadQueries().build()

            }
            return INSTANCE as TodoDatabase

        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}
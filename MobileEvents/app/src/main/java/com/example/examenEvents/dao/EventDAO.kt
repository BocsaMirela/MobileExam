package com.example.examenEvents.dao

import android.arch.persistence.room.*
import com.example.examenEvents.POJO.Event


@Dao
interface EventDAO {

//    @Query("SELECT * FROM items")
//    fun getItems(): List<Event>

    @Query("SELECT * FROM items")
    fun getEvents(): List<Event>

    @Query("SELECT * FROM items")
    fun getEventsTest(): List<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chocolate: Event): Long

    @Delete
    fun delete(chocolate: Event)

    @Update
    fun update(chocolate: Event)

    @Query("DELETE FROM items")
    fun deleteAll()

}

package com.example.examenEvents.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.examenEvents.POJO.Event

@Database(entities = [Event::class], version = 1)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDAO(): EventDAO

    companion object {
        private var INSTANCE: EventDatabase? = null

        fun getAppDatabase(context: Context): EventDatabase {
            if (INSTANCE == null) {
                INSTANCE =
                        Room.databaseBuilder(context.applicationContext, EventDatabase::class.java, "DBevents")
                            .allowMainThreadQueries().build()

            }
            return INSTANCE as EventDatabase

        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}
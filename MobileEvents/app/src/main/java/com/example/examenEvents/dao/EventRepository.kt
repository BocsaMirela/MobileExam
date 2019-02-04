package com.example.examenEvents.dao

import android.os.AsyncTask
import android.util.Log
import com.example.examenEvents.POJO.Event
import java.lang.Exception

class EventRepository(private val eventDAO: EventDAO) {

    fun getAllEvents(): List<Event> {
        return LoadAsyncTask(eventDAO).execute().get()
    }

    fun deleteEvent(event: Event) {
        DeleteAsyncTask(eventDAO).execute(event)
    }

    fun updateEvent(event: Event) {
        UpdateAsyncTask(eventDAO).execute(event)
    }
    fun addEvent(event: Event): Long {
        return AddAsyncTask(eventDAO).execute(event).get()
    }

    companion object {
        private var instance: EventRepository? = null

        fun getInstance(eventDAO: EventDAO): EventRepository {
            if (instance == null) {
                instance =
                    EventRepository(eventDAO)
            }
            return instance as EventRepository
        }
    }

    private class LoadAsyncTask(val eventDAOEvent: EventDAO) :
        AsyncTask<Void, Void, List<Event>>() {
        override fun doInBackground(vararg params: Void?): List<Event> {
            val choco = eventDAOEvent.getEvents()
            Log.e(" from db all ", choco.size.toString())
            return choco
        }

    }

    private class AddAsyncTask(val eventDAOEvent: EventDAO) :
        AsyncTask<Event, Void, Long>() {
        override fun doInBackground(vararg params: Event?): Long {
            return try {
                eventDAOEvent.insert(params[0]!!)
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }
    }

    private class UpdateAsyncTask(val eventDAOEvent: EventDAO) :
        AsyncTask<Event, Void, Boolean>() {
        override fun doInBackground(vararg params: Event?): Boolean {
            return try {
                eventDAOEvent.update(params[0]!!)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private class DeleteAsyncTask(val eventDAOEvent: EventDAO) :
        AsyncTask<Event, Void, Boolean>() {
        override fun doInBackground(vararg params: Event?): Boolean {
            return try {
                eventDAOEvent.delete(params[0]!!)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

}

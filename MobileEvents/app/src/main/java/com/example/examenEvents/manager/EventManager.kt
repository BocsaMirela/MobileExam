package com.example.examenEvents.manager

import com.example.examenEvents.POJO.Event
import com.example.examenEvents.api.API
import com.example.examenEvents.api.RetrofitFactory
import org.json.JSONObject
import retrofit2.Call

class EventManager {
    private val api = RetrofitFactory()
        .getRetrofitInstance().create(API::class.java)

    fun getAllEventsServer(): Call<List<Event>> {
        return api.getEvents()
    }

    fun getAllEventsServerUpdated(m: Long,nrPage:Int): Call<Event> {
        return api.getEventsPaginated(m,nrPage)
    }

    fun addEventServer(event: Event): Call<Event> {
        return api.addEvent(event)
    }

    fun deleteEventServer(id: Int): Call<Event> {
        return api.deleteEvent(id)
    }

    fun updateEventServer(event: Event): Call<Event> {
        return api.updateEvent(event.id, event)
    }

}
package com.example.examenEvents.viewModel

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.widget.Toast
import com.example.examenEvents.POJO.Event
import com.example.examenEvents.dao.EventDatabase
import com.example.examenEvents.dao.EventRepository
import com.example.examenEvents.manager.EventManager

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EventsViewModel(application: Application) : ViewModel() {

    private val eventManager = EventManager()
    private val eventRepository: EventRepository =
        EventRepository.getInstance(EventDatabase.getAppDatabase(application).eventDAO())

    var items: MutableLiveData<List<Event>> = MutableLiveData()

//    fun getEventsFromServerUpdatedAndPaginated(m: Long, nrPage: Int): Call<Event> {
//        return eventManager.getAllEventsServerUpdated(m, nrPage)
//    }

    fun getAllEventsFromServer(): Call<List<Event>> {
        return eventManager.getAllEventsServer()
    }

    fun addEventServer(event: Event): Call<Event> {
        return eventManager.addEventServer(event)
    }

    fun deleteEventServer(id: Int, context: Context) {
        eventManager.deleteEventServer(id)
            .enqueue(object : Callback<Event> {
                override fun onFailure(call: Call<Event>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Something went wrong or no connection! Delete from local data",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(call: Call<Event>, response: Response<Event>) {
                    Toast.makeText(context, "Delete done", Toast.LENGTH_LONG).show()
                }

            })
    }

    fun updateEventServer(event: Event): Call<Event> {
        return eventManager.updateEventServer(event)
    }

//    fun updateEventServer(context: Context, event: Event) {
//        eventManager.updateEventServer(event).enqueue(object : Callback<Event> {
//            override fun onFailure(call: Call<Event>, t: Throwable) {
//                Toast.makeText(
//                    context,
//                    "Something went wrong or no connection! Update from local data",
//                    Toast.LENGTH_LONG
//                ).show()
//                val listOfItems = items.value as ArrayList<Event>
//                listOfItems.remove(event)
//                listOfItems.add(event)
//                items.value = listOfItems
//            }
//
//            override fun onResponse(call: Call<Event>, response: Response<Event>) {
//                Toast.makeText(context, "Update done", Toast.LENGTH_LONG).show()
//                val listOfItems = items.value as ArrayList<Event>
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
            return EventsViewModel(application) as T
        }
    }

    fun getAllEvents(): List<Event> {
        return eventRepository.getAllEvents()
    }

    fun addEvent(event: Event): Long {
        return eventRepository.addEvent(event)
    }

    fun deleteEvent(event: Event) {
        return eventRepository.deleteEvent(event)
    }

    fun deleteAllEvents() {
        return eventRepository.deleteAll()
    }
    fun updateEvent(event: Event) {
        eventRepository.updateEvent(event)
    }
}

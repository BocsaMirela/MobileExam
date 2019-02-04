package com.example.examenEvents

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.example.examenEvents.adapters.EventAdapter
import com.example.examenEvents.viewModel.EventsViewModel
import android.os.Build
import android.support.v7.app.AlertDialog
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.examenEvents.POJO.Event
import com.example.examenEvents.Utils.OnClickInterface
import com.example.examenEvents.api.API
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker
import com.treebo.internetavailabilitychecker.InternetConnectivityListener
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import java.net.URISyntaxException
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), InternetConnectivityListener, OnClickInterface {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var adapter: EventAdapter
    private lateinit var eventViewModel: EventsViewModel
    private lateinit var loadingTextView: TextView
    private lateinit var netStatusTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var mWebSocketClient: WebSocketClient? = null
    private var maxM: Long = 0
    private var more: Boolean = true
    private var currentPage: Int = 1
    private lateinit var mInternetAvailabilityChecker: InternetAvailabilityChecker
    private val ADDED = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        InternetAvailabilityChecker.init(this)
        initialize()
        connectWebSocket()
        Log.e(" on create ", "Main activity was created")
    }

    private fun initialize() {
        eventViewModel = ViewModelProviders.of(this, EventsViewModel.Factory(application))
            .get(EventsViewModel::class.java)

        recyclerView = this.findViewById(R.id.recyclerViewId)
        viewManager = GridLayoutManager(this@MainActivity, 1)
        recyclerView.layoutManager = viewManager
        recyclerView.setHasFixedSize(true)

        adapter = EventAdapter()
        recyclerView.adapter = adapter
        adapter.setOnClickListener(this)

        loadingTextView = findViewById(R.id.loading)
        netStatusTextView = findViewById(R.id.netStatus)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        netStatusTextView.visibility = View.GONE

        observerForLiveData()

        populateListFromLocal()

        internetConnectionStatus()

        getDataFromServer()

        addListenerForConnectionChanged()

    }


    private fun populateListFromLocal() {
        val items = eventViewModel.getAllEvents()
        if (items.isNotEmpty()) {
            adapter.setEventsList(items)
            adapter.notifyDataSetChanged()
        }
    }

    private fun addListenerForConnectionChanged() {
        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance()
        mInternetAvailabilityChecker.addInternetConnectivityListener(this)
    }

    private fun internetConnectionStatus() {
        if (checkIfIsConnected()) {
            netStatusTextView.visibility = View.VISIBLE
            netStatusTextView.text = getString(R.string.online)
        } else {
            netStatusTextView.visibility = View.VISIBLE
            netStatusTextView.text = getString(R.string.offline)
        }
        loadingTextView.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

    }

    private fun observerForLiveData() {
        eventViewModel.items.observe(this, Observer { events ->
            Log.e(" obs bun ", events?.size.toString())
            events?.also {
                adapter.setEventsList(events)
                adapter.notifyDataSetChanged()
                Log.e(" obs bun", " invisible true ")
            }

        })
    }

    private fun getDataFromServer() {
        eventViewModel.getAllEventsFromServer().enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                response.body()?.also {

                    it.forEach { event ->
                        eventViewModel.addEvent(event)
                    }

                    eventViewModel.items.value = it

                    progressBar.visibility = View.GONE
                    loadingTextView.visibility = View.GONE

                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Toast.makeText(applicationContext, "Cannot load data from server", Toast.LENGTH_LONG).show()
//                loadingTextView.text = getString(R.string.failed)
                loadingTextView.visibility = View.GONE
                progressBar.visibility = View.GONE

            }
        })
    }

    private fun createDialogErrorMessage(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        builder.create()
        builder.show()
    }


    fun retry(v: View?) {
        loadingTextView.visibility = View.VISIBLE
        loadingTextView.text = getString(R.string.loading)

        progressBar.visibility = View.VISIBLE

        getDataFromServer()
    }

    private fun connectWebSocket() {
        val uri: URI
        try {
            uri = URI(API.IP)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return
        }

        mWebSocketClient = object : WebSocketClient(uri) {
            override fun onOpen(serverHandshake: ServerHandshake) {
                Log.i("Websocket", "Opened")
                mWebSocketClient?.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL)
            }

            override fun onMessage(s: String) {
                val gson = Gson()
                val groupListType = object : TypeToken<Event>() {}.type
                val event = gson.fromJson<Event>(s, groupListType)

                eventViewModel.addEvent(event)


                val items = adapter.getEventsList() as ArrayList
                items.add(event)

                eventViewModel.items.value = items
            }


            override fun onClose(i: Int, s: String, b: Boolean) {
                Log.i("Websocket", "Closed $s")
            }

            override fun onError(e: Exception) {
                Log.i("Websocket", "Error " + e.message)
            }
        }
        (mWebSocketClient as WebSocketClient).connect()
    }


    override fun onInternetConnectivityChanged(isConnected: Boolean) {
        if (!isConnected) {
            netStatusTextView.text = getString(R.string.offline)
            loadingTextView.visibility = View.GONE
            progressBar.visibility = View.GONE
        } else {
            netStatusTextView.text = getString(R.string.online)
//            retry(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        mInternetAvailabilityChecker
//            .removeInternetConnectivityChangeListener(this)
        Log.e(" on create ", "Main activity was created")

    }

    private fun checkIfIsConnected(): Boolean {
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    override fun onClick(view: View, position: Int) {
//        val intent = Intent(this, DetailsActivity::class.java)
//        val event = adapter.getEventsList()[position]
//
//        intent.putExtra("item", event)
//        startActivityForResult(intent, ADDED)
    }

    fun onAdd(v: View) {
        if (!checkIfIsConnected()) {
            createDialogErrorMessage("You are not connected! \n Connect to internet and try again")
        } else {
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("id", 0)
            startActivityForResult(intent, ADDED)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ADDED) {
            if (resultCode == Activity.RESULT_OK) {
                data?.apply {
                    val event = data.getParcelableExtra<Event>("item")

                    val txtView = findViewById<TextView>(R.id.uploading)
                    txtView.visibility = View.VISIBLE
                    txtView.text = getString(R.string.uploading)

                    addEvent(event)

                }
            }
        }

    }

    private fun addEvent(event: Event) {
        eventViewModel.addEventServer(event).enqueue(object : Callback<Event> {
            override fun onFailure(call: Call<Event>, t: Throwable) {
                val txtView = findViewById<TextView>(R.id.uploading)
                txtView.visibility = View.GONE
                createDialogErrorMessage("Saving failed ${t.message}")
            }

            override fun onResponse(call: Call<Event>, response: Response<Event>) {
                val txtView = findViewById<TextView>(R.id.uploading)
                txtView.visibility = View.GONE

                val eventR = response.body()!!
                val items = adapter.getEventsList() as ArrayList
                val ind1 = items.indexOf(eventR)
                items.add(ind1, eventR)
                eventViewModel.items.value = items

                eventViewModel.addEvent(eventR)

            }
        })
    }

    override fun onStart() {
        super.onStart()
        Log.e(" on start ", "Main activity on start")

    }

    override fun onResume() {
        super.onResume()
        Log.e(" on resume ", "Main activity on resume")

    }

    override fun onPause() {
        super.onPause()
        Log.e(" on pause ", "Main activity on pause")

    }

    override fun onStop() {
        super.onStop()
        Log.e(" on stop ", "Main activity on stop")

    }
}

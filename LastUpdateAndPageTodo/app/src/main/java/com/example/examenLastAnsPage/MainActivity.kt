package com.example.examenTodos

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.example.examenTodos.POJO.Todo
import com.example.examenTodos.adapters.TodoAdapter
import com.example.examenTodos.viewModel.TodosViewModel
import android.os.Build
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.examenTodos.POJO.ServerResponse
import com.example.examenTodos.Utils.OnClickInterface
import com.example.examenTodos.api.API
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker
import com.treebo.internetavailabilitychecker.InternetConnectivityListener
import kotlinx.android.synthetic.main.activity_main.*
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), InternetConnectivityListener, OnClickInterface {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var adapter: TodoAdapter
    private lateinit var todoViewModel: TodosViewModel
    private lateinit var loadingTextView: TextView
    private lateinit var netStatusTextView: TextView
    private lateinit var progressBar: ProgressBar
    private var mWebSocketClient: WebSocketClient? = null
    private var maxM: Long = 0
    private var more: Boolean = true
    private var currentPage: Int = 1
    private lateinit var mInternetAvailabilityChecker: InternetAvailabilityChecker
    private val UPDATED = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        InternetAvailabilityChecker.init(this)
        initialize()
        connectWebSocket()
    }

    private fun initialize() {
        todoViewModel = ViewModelProviders.of(this, TodosViewModel.Factory(application))
            .get(TodosViewModel::class.java)

        recyclerView = this.findViewById(R.id.recyclerViewId)
        viewManager = GridLayoutManager(this@MainActivity, 1)
        recyclerView.layoutManager = viewManager
        recyclerView.setHasFixedSize(true)

        adapter = TodoAdapter()
        recyclerView.adapter = adapter
        adapter.setOnClickListener(this)

        loadingTextView = findViewById(R.id.loading)
        netStatusTextView = findViewById(R.id.netStatus)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        netStatusTextView.visibility = View.GONE

        observerForLiveData()

        populateListFromLocal()

//        internetConnectionStatus()

        getDataFromServer()

//        addListenerForConnectionChanged()

    }


    private fun populateListFromLocal() {
        val items = todoViewModel.getAllTodos()
        if (items.isNotEmpty()) {
            adapter.setTodosList(items)
            adapter.notifyDataSetChanged()
            maxM = items[0].updated
        } else maxM = 0
    }

    private fun addListenerForConnectionChanged() {
        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance()
        mInternetAvailabilityChecker.addInternetConnectivityListener(this)
    }

    private fun internetConnectionStatus() {
        if (checkIfIsConnected()) {
            netStatusTextView.visibility = View.VISIBLE
            netStatusTextView.text = getString(R.string.online)

            loadingTextView.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE

            getDataFromServer()
        } else {
            netStatusTextView.visibility = View.VISIBLE
            netStatusTextView.text = getString(R.string.offline)

            loadingTextView.visibility = View.GONE
            progressBar.visibility = View.GONE
        }
    }

    private fun observerForLiveData() {
        todoViewModel.items.observe(this, Observer { events ->
            Log.e(" obs bun ", events?.size.toString())
            events?.also {
                adapter.setTodosList(events)
                adapter.notifyDataSetChanged()
                Log.e(" obs bun", " invisible true ")
            }

        })
    }

    private fun getDataFromServer() {
        todoViewModel.getTodosFromServerUpdatedAndPaginated(maxM, currentPage)
            .enqueue(object : Callback<ServerResponse> {
                override fun onResponse(call: Call<ServerResponse>, response: Response<ServerResponse>) {
                    response.body()?.also {
                        more = it.more
                        currentPage = it.page
                        val items = it.items
//                        val oldItems = adapter.getTodosList() as ArrayList
                        val oldItems = ArrayList(adapter.getTodosList())

                        //if last updated is missing just add all to local and adapter
                        items.forEach { todo ->
                            run {
                                todoViewModel.addTodo(todo)

                                if (oldItems.contains(todo)) {
                                    oldItems.remove(todo)
                                    oldItems.add(todo)
                                } else {
                                    oldItems.add(todo)
                                }
                            }
                        }

                        todoViewModel.items.value = oldItems

                        currentPage++
                        if (!more) {
                            progressBar.visibility = View.GONE
                            loadingTextView.visibility = View.GONE

                        } else getDataFromServer()
                    }


                }

                override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Cannot load data from server", Toast.LENGTH_LONG).show()

                    loadingTextView.text = getString(R.string.failed)
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
//        if (!isConnected) {
//            netStatusTextView.text = getString(R.string.offline)
//            loadingTextView.visibility = View.GONE
//            progressBar.visibility = View.GONE
//        } else {
//            netStatusTextView.text = getString(R.string.online)
//            retry(null)
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        mInternetAvailabilityChecker
//            .removeInternetConnectivityChangeListener(this)
    }

    private fun checkIfIsConnected(): Boolean {
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    override fun onClick(view: View, position: Int) {
        val intent = Intent(this, DetailsActivity::class.java)
        val todo = adapter.getTodosList()[position]

        intent.putExtra("item", todo)
        startActivityForResult(intent, UPDATED)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UPDATED) {
            if (resultCode == Activity.RESULT_OK) {
                data?.apply {
                    val todo = data.getParcelableExtra<Todo>("item")

                    val txtView = findViewById<TextView>(R.id.uploading)
                    txtView.visibility = View.VISIBLE
                    txtView.text = getString(R.string.uploading)

                    updateTodo(todo)

                }
            }
        }

    }

    private fun updateTodo(todo: Todo) {
        todoViewModel.updateTodoServer(todo).enqueue(object : Callback<Todo> {
            override fun onFailure(call: Call<Todo>, t: Throwable) {
                val txtView = findViewById<TextView>(R.id.uploading)
                txtView.visibility = View.GONE
                createDialogErrorMessage("Uploading failed ${t.message}")
            }

            override fun onResponse(call: Call<Todo>, response: Response<Todo>) {
                val txtView = findViewById<TextView>(R.id.uploading)
                txtView.visibility = View.GONE

                val todoR = response.body()!!
                val items = adapter.getTodosList() as ArrayList
                val ind1 = items.indexOf(todoR)
                items.remove(todoR)
                items.add(ind1, todoR)
                todoViewModel.items.value = items

            }
        })
    }

}

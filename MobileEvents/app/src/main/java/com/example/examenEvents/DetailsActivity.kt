package com.example.examenEvents

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.example.examenEvents.POJO.Event
import com.example.examenEvents.viewModel.EventsViewModel
import java.util.*

class DetailsActivity : AppCompatActivity() {
    private lateinit var event: Event


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_layout)
        val item = intent.getParcelableExtra<Event>("item")
        intentUI(item)

    }


    private fun intentUI(item: Event?) {
        item?.also {
            findViewById<TextView>(R.id.textViewId).text = item.id.toString()
            findViewById<TextView>(R.id.textViewText).text = item.text
            findViewById<TextView>(R.id.textViewTextData).text = item.date
            event = item


        }

    }

    fun onUpdate(v: View) {
        val text = findViewById<EditText>(R.id.textViewText).text.toString()
        event.text = text
        val resultIntent = Intent()
        resultIntent.putExtra("item", event)
        setResult(Activity.RESULT_OK, resultIntent)
        this@DetailsActivity.finish()
    }
}
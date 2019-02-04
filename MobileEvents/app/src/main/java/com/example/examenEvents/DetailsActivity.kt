package com.example.examenEvents

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.text.format.Time
import android.view.View
import android.widget.*
import com.example.examenEvents.POJO.Event
import com.example.examenEvents.Utils.DatePickerFragment
import com.example.examenEvents.viewModel.EventsViewModel
import java.util.*
import java.text.SimpleDateFormat


class DetailsActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {
    private var id: Int = 0
    private var date: Date = Date()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_layout)
        id = intent.getIntExtra("id", 0)
    }


    private fun intentUI(item: Event?) {
        item?.also {
            findViewById<TextView>(R.id.textViewId).text = item.id.toString()
            findViewById<TextView>(R.id.textViewText).text = item.text
//            findViewById<TextView>(R.id.textViewTextData).text = item.date


        }

    }

    fun onUpdate(v: View) {
        val text = findViewById<EditText>(R.id.textViewText).text.toString()
        val event = Event(id, text, date)
        val resultIntent = Intent()
        resultIntent.putExtra("item", event)
        setResult(Activity.RESULT_OK, resultIntent)
        this@DetailsActivity.finish()
    }

    fun showDatePickerDialog(v: View) {
        val newFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val dateText = findViewById<TextView>(R.id.textViewTextData)
        val dateString = "$dayOfMonth-$month-$year"
        dateText.text = dateString

        date =SimpleDateFormat("dd-MM-yyyy",Locale.ENGLISH).parse(dateString)
    }

}
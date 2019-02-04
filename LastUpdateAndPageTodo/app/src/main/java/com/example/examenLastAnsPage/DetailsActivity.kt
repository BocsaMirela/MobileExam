package com.example.examenTodos

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.example.examenTodos.POJO.Todo
import com.example.examenTodos.viewModel.TodosViewModel
import java.util.*

class DetailsActivity : AppCompatActivity() {
    private lateinit var todo: Todo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.details_layout)
        val item = intent.getParcelableExtra<Todo>("item")
        intentUI(item)

    }


    private fun intentUI(item: Todo?) {
        item?.also {
            findViewById<TextView>(R.id.textViewId).text = item.id.toString()
            findViewById<TextView>(R.id.textViewText).text = item.text
            findViewById<TextView>(R.id.textViewTextData).text = Date(item.updated).toString()
            findViewById<TextView>(R.id.textViewStatusValue).text = item.status
            todo = item


        }

    }

    fun onUpdate(v: View) {
        val text = findViewById<EditText>(R.id.textViewText).text.toString()
        todo.text = text
        val resultIntent = Intent()
        resultIntent.putExtra("item", todo)
        setResult(Activity.RESULT_OK, resultIntent)
        this@DetailsActivity.finish()
    }
}
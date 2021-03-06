package com.example.examenTodos.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.examenTodos.POJO.Todo
import com.example.examenTodos.R
import com.example.examenTodos.Utils.OnClickInterface
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TodoAdapter : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    private var todosList: List<Todo> = ArrayList()
    private val TITLE = 0
    private val LOAD_MORE = 1
    var clickListener: OnClickInterface? = null


    fun setTodosList(list: List<Todo>) {
        todosList = list
    }

    fun getTodosList(): List<Todo> {
        return todosList
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val view = layoutInflater.inflate(R.layout.row_layout, viewGroup, false)
        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return todosList.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, p: Int) {
        val event = todosList[p]
        viewHolder.txtDescription.text = event.text
        viewHolder.txtData.text = formatDate(Date(event.updated))
        viewHolder.txtStatus.text = "Status: " + event.status

    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {


        var txtDescription: TextView = view.findViewById(R.id.chocolateDescription)
        var txtData: TextView = view.findViewById(R.id.chocolateData)
        var txtStatus: TextView = view.findViewById(R.id.chocolateStatus)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            clickListener?.onClick(view, adapterPosition); }

    }

    private fun formatDate(date: Date): String {
        val fmt = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
//        return fmt.parse(string)
        return fmt.format(date)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < itemCount) {
            TITLE
        } else {
            LOAD_MORE
        }
    }

    fun setOnClickListener(clickInterface: OnClickInterface) {
        clickListener = clickInterface
    }

}
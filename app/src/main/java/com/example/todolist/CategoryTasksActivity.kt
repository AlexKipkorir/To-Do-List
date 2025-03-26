package com.example.todolist

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CategoryTasksActivity : AppCompatActivity() {
    private lateinit var taskListView: ListView
    private lateinit var categoryTitle: TextView
    private lateinit var category: String
    private val tasks = mapOf(
        "Work" to listOf("Finish report", "Email boss", "Prepare slides"),
        "Personal" to listOf("Buy groceries", "Call mom"),
        "Shopping" to listOf("Order phone case", "Buy laptop stand"),
        "Health" to listOf("Go for a run")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_tasks)

        categoryTitle = findViewById(R.id.categoryTitle)
        taskListView = findViewById(R.id.taskListView)

        category = intent.getStringExtra("category") ?: "Unknown"
        categoryTitle.text = "$category Tasks"

        val taskList = tasks[category] ?: emptyList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)
        taskListView.adapter = adapter
    }
}

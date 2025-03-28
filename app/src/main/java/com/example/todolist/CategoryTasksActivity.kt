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

        // Get category from intent
        category = intent.getStringExtra("category")?.trim() ?: "Unknown"
        println("Received category: $category") // Debug log

        // Fix category lookup
        val formattedCategory = category.replaceFirstChar { it.uppercaseChar() }
        val taskList = tasks[formattedCategory] ?: emptyList()

        // Update UI
        categoryTitle.text = "$formattedCategory Tasks"

        // Set up ListView adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, taskList)
        taskListView.adapter = adapter
        adapter.notifyDataSetChanged() // Ensure UI updates
    }
}


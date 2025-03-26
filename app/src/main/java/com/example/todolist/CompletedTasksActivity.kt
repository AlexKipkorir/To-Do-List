package com.example.todolist

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class CompletedTasksActivity : AppCompatActivity() {
    private lateinit var completedTaskListView: ListView
    private val completedTasks = listOf(
        "Submit assignment", "Pay electricity bill", "Finish book"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_tasks)

        completedTaskListView = findViewById(R.id.completedTaskListView)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, completedTasks)
        completedTaskListView.adapter = adapter
    }
}

package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class TaskCategoriesActivity : AppCompatActivity() {
    private lateinit var categoryListView: ListView
    private lateinit var completedTasksButton: Button
    private val categories = listOf("Work", "Personal", "Shopping", "Health")
    private val taskCounts = mapOf(
        "Work" to 5,
        "Personal" to 2,
        "Shopping" to 3,
        "Health" to 1
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_categories)

        categoryListView = findViewById(R.id.categoryListView)
        completedTasksButton = findViewById(R.id.completedTasksButton)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)


        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        categoryListView.adapter = adapter

        // Click to open selected category's tasks
        categoryListView.setOnItemClickListener { _, _, position, _ ->
            val category = categories[position]
            val intent = Intent(this, CategoryTasksActivity::class.java).apply {
                putExtra("category", category)
            }
            startActivity(intent)
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddTaskActivity::class.java))
                    true
                }
                R.id.nav_tasks -> true

                else -> false
            }
        }

        // Open Completed Tasks screen
        completedTasksButton.setOnClickListener {
            startActivity(Intent(this, CompletedTasksActivity::class.java))
        }



    }
}
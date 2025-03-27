package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.data.data.models.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TaskCategoriesActivity : AppCompatActivity() {
    private lateinit var categoryListView: ListView
    private lateinit var completedTasksButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_categories)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        categoryListView = findViewById(R.id.categoryListView)
        completedTasksButton = findViewById(R.id.completedTasksButton)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Load tasks and categorize them automatically
        loadTasks()

        // Handle category selection
        categoryListView.setOnItemClickListener { _, _, position, _ ->
            val category = categoryListView.adapter.getItem(position).toString().split(" (")[0]
            val intent = Intent(this, CategoryTasksActivity::class.java).apply {
                putExtra("category", category)
            }
            startActivity(intent)
        }

        // Bottom navigation
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

    /**
     * Load tasks from Firestore, categorize them dynamically, and update the UI.
     */
    private fun loadTasks() {
        auth.currentUser?.let { user ->
            firestore.collection("tasks")
                .whereEqualTo("userId", user.uid)
                .get()
                .addOnSuccessListener { documents ->
                    val categorizedTasks = mutableMapOf<String, MutableList<Task>>()

                    for (document in documents) {
                        val task = document.toObject(Task::class.java).copy(id = document.id)
                        val category = categorizeTask(task)
                        categorizedTasks.getOrPut(category) { mutableListOf() }.add(task)
                    }

                    // Update the UI with categorized tasks
                    updateCategoryListView(categorizedTasks)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load tasks", Toast.LENGTH_SHORT).show()
                }
        }
    }

    /**
     * Automatically categorize a task based on keywords in its title.
     */
    private fun categorizeTask(task: Task): String {
        val title = task.title.lowercase()
        return when {
            title.contains("meeting") || title.contains("project") || title.contains("deadline") -> "Work"
            title.contains("gym") || title.contains("doctor") || title.contains("exercise") -> "Health"
            title.contains("buy") || title.contains("order") || title.contains("shopping") -> "Shopping"
            title.contains("birthday") || title.contains("family") || title.contains("call") -> "Personal"
            else -> "Uncategorized"  // Default category if no match is found
        }
    }

    /**
     * Update the ListView with categorized tasks and their counts.
     */
    private fun updateCategoryListView(categorizedTasks: Map<String, List<Task>>) {
        val categoriesWithCounts = categorizedTasks.map { (category, tasks) ->
            "$category (${tasks.size})"
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categoriesWithCounts)
        categoryListView.adapter = adapter
    }
}

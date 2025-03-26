package com.example.todolist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.adapters.TaskAdapter
import com.example.todolist.models.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var emptyMessage: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        recyclerView = findViewById(R.id.recyclerViewTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
        emptyMessage = findViewById(R.id.tvEmptyMessage)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        fabAddTask.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        loadTasks()

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Stay on Dashboard
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, AddTaskActivity::class.java))
                    true
                }
                R.id.nav_tasks -> {
                    startActivity(Intent(this, TaskCategoriesActivity::class.java))
                    true
                }
                else -> false
            }


        }
    }

    private fun loadTasks() {
        firestore.collection("tasks")
            .whereEqualTo("userId", auth.currentUser?.uid)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    recyclerView.visibility = View.GONE
                    emptyMessage.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    emptyMessage.visibility = View.GONE

                    val taskList = mutableListOf<Task>()
                    for (document in documents) {
                        val task = document.toObject(Task::class.java).copy(id = document.id)
                        taskList.add(task)
                    }

                    recyclerView.adapter = TaskAdapter(taskList)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load tasks", Toast.LENGTH_SHORT).show()
            }
    }


    fun logout(view: View) {
        auth.signOut()
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}



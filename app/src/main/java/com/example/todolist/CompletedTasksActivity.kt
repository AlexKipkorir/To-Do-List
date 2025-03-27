package com.example.todolist

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.adapters.TaskAdapter
import com.example.todolist.data.data.models.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CompletedTasksActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var completedTaskAdapter: TaskAdapter
    private val completedTaskList = mutableListOf<Task>()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed_tasks)

        recyclerView = findViewById(R.id.recyclerViewCompletedTasks)
        recyclerView.layoutManager = LinearLayoutManager(this)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        fun markTaskAsCompleted(taskId: String, isCompleted: Boolean) {
            Toast.makeText(this, "Task marked as completed", Toast.LENGTH_SHORT).show()
        }

        fun deleteTask(task: Task) {
            Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
        }

        completedTaskAdapter = TaskAdapter(completedTaskList,
            { task -> deleteTask(task) },
            { task -> markTaskAsCompleted(task.id) }
        )

        recyclerView.adapter = completedTaskAdapter

        loadCompletedTasks()
    }

    private fun loadCompletedTasks() {
        auth.currentUser?.let { user ->
            firestore.collection("completed_tasks")
                .whereEqualTo("userId", user.uid)
                .get()
                .addOnSuccessListener { documents ->
                    completedTaskList.clear()
                    for (document in documents) {
                        val task = document.toObject(Task::class.java)
                        task.id = document.id  // Fix: Assign document ID
                        completedTaskList.add(task)
                    }
                    completedTaskAdapter.notifyItemRangeInserted(0, completedTaskList.size)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load completed tasks", Toast.LENGTH_SHORT).show()
                }
        }
    }

}




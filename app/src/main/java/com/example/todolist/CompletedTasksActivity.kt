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

        completedTaskAdapter = TaskAdapter(
            completedTaskList,
            { task -> deleteTask(task) },  // Task deletion
            { taskId -> markTaskAsCompleted(taskId, true) }, // Task completed (String -> Unit)
            { taskId, isCompleted -> markTaskAsCompleted(taskId, isCompleted) } // Task update (String, Boolean) -> Unit
        )



        recyclerView.adapter = completedTaskAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadCompletedTasks()
    }

    private fun loadCompletedTasks() {
        auth.currentUser?.let { user ->
            firestore.collection("completed_tasks")
                .whereEqualTo("userId", user.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(this, "No completed tasks found", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    completedTaskList.clear()
                    for (document in documents) {
                        val task = document.toObject(Task::class.java)
                        task.id = document.id  // Assign document ID
                        completedTaskList.add(task)
                        println("Task loaded: ${task.title}") // Debugging log
                    }
                    completedTaskAdapter.notifyDataSetChanged() // Ensure RecyclerView updates
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


}




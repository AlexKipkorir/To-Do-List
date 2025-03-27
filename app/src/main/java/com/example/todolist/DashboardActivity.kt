package com.example.todolist

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.adapters.TaskAdapter
import com.example.todolist.data.data.models.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var emptyMessage: TextView
    private lateinit var btnLogout: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var taskAdapter: TaskAdapter
    private var taskList: MutableList<Task> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        recyclerView = findViewById(R.id.recyclerViewTasks)
        fabAddTask = findViewById(R.id.fabAddTask)
        emptyMessage = findViewById(R.id.tvEmptyMessage)
        btnLogout = findViewById(R.id.btnLogout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Welcome ${currentUser.email}", Toast.LENGTH_SHORT).show()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(
            taskList,
            { task -> deleteTaskFromFirestore(task) },
            { taskId: String, isCompleted: Boolean ->
                updateTaskCompletion(taskId, isCompleted)
            }
        )

        recyclerView.adapter = taskAdapter

        fabAddTask.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        btnLogout.setOnClickListener {
            logout()
        }

        loadTasks()
        setupSwipeToDelete()

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
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
    //Notification Channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "task_notifications",
                "Task Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for task events"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
    private fun showNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(this, "task_notifications")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random.nextInt(), builder.build())
    }



    private fun loadTasks() {
        auth.currentUser?.let { user ->
            Log.d("FirestoreDebug", "Fetching tasks for userId: ${user.uid}")

            firestore.collection("tasks")
                .whereEqualTo("userId", user.uid)
                .get()
                .addOnSuccessListener { documents ->
                    taskList.clear()
                    if (documents.isEmpty) {
                        Log.d("FirestoreDebug", "No tasks found for user: ${user.uid}")
                        recyclerView.visibility = View.GONE
                        emptyMessage.visibility = View.VISIBLE
                    } else {
                        Log.d("FirestoreDebug", "Tasks retrieved: ${documents.size()}")
                        recyclerView.visibility = View.VISIBLE
                        emptyMessage.visibility = View.GONE

                        for (document in documents) {
                            val task = document.toObject(Task::class.java).copy(id = document.id)
                            taskList.add(task)
                            Log.d("FirestoreDebug", "Task loaded: ${task.title}, ID: ${task.id}")
                        }
                        taskAdapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreDebug", "Error fetching tasks", e)
                    Toast.makeText(this, "Failed to load tasks", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Log.e("FirestoreDebug", "User not authenticated!")
            Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show()
        }
    }
    fun markTaskAsCompleted(taskId: String) {
        val db = FirebaseFirestore.getInstance()
        val tasksCollection = db.collection("tasks")
        val completedTasksCollection = db.collection("completed_tasks")

        // Get task details before moving
        tasksCollection.document(taskId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val taskData = document.data
                    if (taskData != null) {
                        // Move task to completed_tasks
                        completedTasksCollection.document(taskId).set(taskData)
                            .addOnSuccessListener {
                                // Delete task from tasks collection
                                tasksCollection.document(taskId).delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Task marked as completed!", Toast.LENGTH_SHORT).show()
                                        Log.d("TaskUpdate", "Task moved to completed_tasks")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("TaskUpdate", "Failed to delete task from tasks", e)
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.e("TaskUpdate", "Failed to move task to completed_tasks", e)
                            }
                    }
                } else {
                    Toast.makeText(this, "Task does not exist!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("TaskUpdate", "Error retrieving task", e)
            }
    }



    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val task = taskList[position]

                deleteTaskFromFirestore(taskList[position])
                taskList.removeAt(position)
                taskAdapter.notifyItemRemoved(position)

                markTaskAsCompleted(task.id)
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    private fun deleteTaskFromFirestore(task: Task) {
        firestore.collection("tasks").document(task.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete task", Toast.LENGTH_SHORT).show()
            }
    }

    fun updateTaskCompletion(taskId: String, isCompleted: Boolean) {
        val db = FirebaseFirestore.getInstance()
        val taskRef = db.collection("tasks").document(taskId)

        taskRef.update("completed", isCompleted)
            .addOnSuccessListener {
                Toast.makeText(this, "Task updated successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update task: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("TaskUpdate", "Error updating task", e)
            }
    }


    private fun logout() {
        auth.signOut()
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


}



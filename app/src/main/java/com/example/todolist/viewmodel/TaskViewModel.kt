package com.example.todolist.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.database.AppDatabase
import com.example.todolist.data.data.models.Task
import com.example.todolist.data.data.repository.TaskRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    private val allTasks: LiveData<List<Task>>

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.getAllTasks()
    }

    fun insert(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }

    fun markCompleted(taskId: Int, isCompleted: Boolean) = viewModelScope.launch {
        repository.markTaskCompleted(taskId.toString(), isCompleted)
    }
    fun getTasks() {
        val db = FirebaseFirestore.getInstance()
        db.collection("tasks")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val taskId = document.id  // Firestore auto-generated ID
                    val title = document.getString("title") ?: "No Title"
                    val description = document.getString("description") ?: "No Description"
                    val completed = document.getBoolean("completed") ?: false

                    Log.d("TaskFetch", "Task ID: $taskId, Title: $title, Completed: $completed")
                }
            }
            .addOnFailureListener { e ->
                Log.e("TaskFetch", "Error getting tasks", e)
            }
    }
    fun getCompletedTasks() {
        val db = FirebaseFirestore.getInstance()
        db.collection("completed_tasks")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val taskId = document.id
                    val title = document.getString("title") ?: "No Title"
                    val description = document.getString("description") ?: "No Description"

                    Log.d("CompletedTasks", "Task ID: $taskId, Title: $title")
                }
            }
            .addOnFailureListener { e ->
                Log.e("CompletedTasks", "Error getting completed tasks", e)
            }
    }


}



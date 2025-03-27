package com.example.todolist.data.data.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.example.todolist.data.data.database.TaskDao
import com.example.todolist.data.data.models.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository(private val taskDao: TaskDao) {
    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun markTaskCompleted(taskId: String, isCompleted: Boolean) {
        taskDao.markTaskCompleted(taskId, isCompleted)
    }

    fun getAllTasks(): LiveData<List<Task>> = taskDao.getAllTasks()

    fun createTask(context: android.content.Context, title: String, description: String) {
        val db = FirebaseFirestore.getInstance()
        val tasksCollection = db.collection("tasks")

        val newTask = hashMapOf(
            "title" to title,
            "description" to description,
            "completed" to false,  // Default: Not completed
            "createdAt" to FieldValue.serverTimestamp()
        )

        tasksCollection.add(newTask)
            .addOnSuccessListener { documentReference ->
                Log.d("TaskCreate", "Task added with ID: ${documentReference.id}")
                Toast.makeText(context, "Task added!", Toast.LENGTH_SHORT).show() // ✅ Now context is passed
            }
            .addOnFailureListener { e ->
                Log.e("TaskCreate", "Error adding task", e)
                Toast.makeText(context, "Failed to add task: ${e.message}", Toast.LENGTH_SHORT).show() // ✅ Fix
            }
    }

}

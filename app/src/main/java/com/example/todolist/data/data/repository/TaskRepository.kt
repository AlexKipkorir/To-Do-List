package com.example.todolist.data.data.repository

import androidx.lifecycle.LiveData
import com.example.todolist.data.data.database.TaskDao
import com.example.todolist.data.data.models.Task

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
}

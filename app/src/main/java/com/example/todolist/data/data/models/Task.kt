package com.example.todolist.data.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: String = 0.toString(),
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val timestamp: Long = 0L,
    val completed: Boolean = false
)
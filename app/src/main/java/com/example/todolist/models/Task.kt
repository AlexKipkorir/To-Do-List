package com.example.todolist.models

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val timestamp: Long = 0L,
    val completed: Boolean = false
)
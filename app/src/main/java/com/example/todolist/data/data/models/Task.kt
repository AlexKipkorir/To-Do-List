package com.example.todolist.data.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    var localId: Int = 0,

    var id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val timestamp: Long = 0L,
    val completed: Boolean = false
)
    {
        // Firestore needs a no-argument constructor
        constructor() : this(0, "", "", "", "", 0L, false)
    }

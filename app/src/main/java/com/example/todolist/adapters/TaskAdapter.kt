package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.models.Task
import com.google.firebase.firestore.FirebaseFirestore

class TaskAdapter(private var taskList: List<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitle: TextView = view.findViewById(R.id.tvTaskTitle)
        val checkBox: CheckBox = view.findViewById(R.id.cbTaskCompleted)
        val deleteButton: Button = view.findViewById(R.id.btnDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskTitle.text = task.title
        holder.checkBox.isChecked = task.completed

        // Handle checkbox toggle
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            updateTaskCompletion(task.id, isChecked)
        }

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            deleteTask(task.id)
        }
    }

    override fun getItemCount(): Int = taskList.size

    private fun updateTaskCompletion(taskId: String, isCompleted: Boolean) {
        firestore.collection("tasks").document(taskId)
            .update("completed", isCompleted)
    }

    private fun deleteTask(taskId: String) {
        firestore.collection("tasks").document(taskId)
            .delete()
    }
}


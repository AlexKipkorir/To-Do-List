package com.example.todolist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.data.data.models.Task
import com.google.firebase.firestore.FirebaseFirestore

class TaskAdapter(
    private var taskList: MutableList<Task>, // Changed to MutableList
    private val onTaskDeleted: (Task) -> Unit, // Callback for swipe-to-delete
    private val onTaskCompleted: (Task, Boolean) -> Unit // Callback for checkbox toggle
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskTitle: TextView = view.findViewById(R.id.tvTaskTitle)
        val checkBox: CheckBox = view.findViewById(R.id.cbTaskCompleted)
        val deleteButton: ImageButton = view.findViewById(R.id.btnDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskTitle.text = task.title

        // Prevents unintended trigger when RecyclerView reuses views
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = task.completed

        // Handle checkbox toggle
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onTaskCompleted(task, isChecked)
        }

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            onTaskDeleted(task)
        }
    }

    override fun getItemCount(): Int = taskList.size

    fun setTasks(newTasks: List<Task>) {
        this.taskList.clear()
        this.taskList.addAll(newTasks)
        notifyDataSetChanged()
    }

    // Function to remove task when swiped
    fun removeTask(position: Int) {
        val task = taskList[position]
        onTaskDeleted(task) // Call deletion function
        taskList.removeAt(position)
        notifyItemRemoved(position)
    }
}



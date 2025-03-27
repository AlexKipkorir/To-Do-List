package com.example.todolist

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import com.example.todolist.utils.NotificationHelper

class AddTaskActivity : AppCompatActivity() {

    private lateinit var etTaskTitle: EditText
    private lateinit var etTaskDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnSelectDueDate: Button
    private lateinit var tvSelectedDueDate: TextView
    private lateinit var btnSaveTask: Button
    private lateinit var btnCancel: Button

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var selectedDueDate: Long = 0L // Store selected date in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        etTaskTitle = findViewById(R.id.etTaskTitle)
        etTaskDescription = findViewById(R.id.etTaskDescription)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnSelectDueDate = findViewById(R.id.btnSelectDueDate)
        tvSelectedDueDate = findViewById(R.id.tvSelectedDueDate)
        btnSaveTask = findViewById(R.id.btnSaveTask)
        btnCancel = findViewById(R.id.btnCancel)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupCategorySpinner()
        setupDatePicker()

        btnSaveTask.setOnClickListener { saveTaskToFirestore() }
        btnCancel.setOnClickListener { finish() } // Close activity

        // Initialize bottomNav properly
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.nav_add -> {
                   // Stay on AddTaskActivity
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


    private fun setupCategorySpinner() {
        val categories = arrayOf("Work", "Personal", "Shopping", "Health", "Others")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spinnerCategory.adapter = adapter
    }

    private fun setupDatePicker() {
        btnSelectDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    selectedDueDate = calendar.timeInMillis
                    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    tvSelectedDueDate.text = dateFormat.format(calendar.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    private fun saveTaskToFirestore() {
        val title = etTaskTitle.text.toString().trim()
        val description = etTaskDescription.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()
        val userId = auth.currentUser?.uid ?: return

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Title and description are required!", Toast.LENGTH_SHORT).show()
            return
        }

        val task = hashMapOf(
            "userId" to userId,
            "title" to title,
            "description" to description,
            "category" to category,
            "dueDate" to selectedDueDate,
            "isCompleted" to false
        )

        db.collection("tasks")
            .add(task)
            .addOnSuccessListener {
                Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show()
                NotificationHelper.showNotification(this, "New Task Added", "Task: $title in $category")
                finish() // Go back to Dashboard
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add task!", Toast.LENGTH_SHORT).show()
            }
    }
}

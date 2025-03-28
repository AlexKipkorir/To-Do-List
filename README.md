# To-Do List App Documentation

## Overview
The To-Do List App is a task management application that allows users to add, complete, and organize their tasks efficiently. It integrates Firebase Firestore for cloud storage and Firebase Authentication for user management. The app features task categorization, real-time updates, and a user-friendly interface for seamless task tracking.

## Features
1. **User Authentication**
   - Users can sign up and log in securely using Firebase Authentication.
   - Each user’s tasks are stored uniquely under their account.

2. **Task Management**
   - Users can add new tasks with a title and a completion status.
   - Tasks are saved to Firebase Firestore.
   - Tasks are retrieved upon login and displayed in a RecyclerView.

3. **Task Completion & Deletion**
   - Users can mark tasks as completed.
   - Completed tasks are moved to a separate collection (`completed_tasks`).
   - Users can delete tasks from both active and completed lists.

4. **Task Categorization**
   - Tasks can be grouped into predefined categories (e.g., Work, Personal, Shopping, Health).
   - Each category has its own list of tasks.

5. **Data Persistence**
   - Tasks remain saved in Firestore even after the app is closed and reopened.
   - Each task is associated with a `userId` to prevent data overlap between users.

6. **User-Friendly Interface**
   - Simple and intuitive UI with ListView and RecyclerView for task display.
   - Toast messages for feedback (e.g., "Task added", "No tasks found").

## Technical Implementation
### **Main Components**
#### **1. Authentication (Firebase Authentication)**
- Handled in `LoginActivity.kt` and `SignupActivity.kt`.
- Ensures only authenticated users can add and retrieve tasks.

#### **2. Task Storage (Firebase Firestore)**
- Collection: `tasks` (stores active tasks).
- Collection: `completed_tasks` (stores completed tasks).
- Each task document includes:
  ```json
  {
    "title": "Task Name",
    "isCompleted": false,
    "createdAt": 1700000000000,
    "userId": "UserUniqueID"
  }
  ```

#### **3. Task Management (RecyclerView & ListView)**
- **RecyclerView in `CompletedTasksActivity.kt`** retrieves completed tasks from Firestore.
- **ListView in `CategoryTasksActivity.kt`** displays tasks based on categories.

#### **4. Functions for Firestore Integration**
- **Adding a Task:**
  ```kotlin
  private fun addTaskToFirestore(taskTitle: String) {
      val userId = auth.currentUser?.uid ?: return
      val task = hashMapOf(
          "title" to taskTitle,
          "isCompleted" to false,
          "createdAt" to System.currentTimeMillis(),
          "userId" to userId
      )
      firestore.collection("tasks").add(task)
  }
  ```
- **Marking a Task as Completed:**
  ```kotlin
  private fun markTaskAsCompleted(taskId: String) {
      firestore.collection("tasks").document(taskId)
          .update("isCompleted", true)
  }
  ```
- **Retrieving Completed Tasks:**
  ```kotlin
  private fun loadCompletedTasks() {
      auth.currentUser?.let { user ->
          firestore.collection("completed_tasks")
              .whereEqualTo("userId", user.uid)
              .get()
              .addOnSuccessListener { documents ->
                  if (documents.isEmpty) {
                      Toast.makeText(this, "No completed tasks found", Toast.LENGTH_SHORT).show()
                      return@addOnSuccessListener
                  }
                  completedTaskList.clear()
                  for (document in documents) {
                      val task = document.toObject(Task::class.java)
                      task.id = document.id
                      completedTaskList.add(task)
                  }
                  completedTaskAdapter.notifyDataSetChanged()
              }
      }
  }
  ```

## Installation & Setup
1. Clone the repository:
   ```sh
   git clone <GitHub_Repo_URL>
   ```
2. Open the project in Android Studio.
3. Add your Firebase configuration file (`google-services.json`).
4. Sync and build the project.
5. Run the app on an emulator or a physical device.

## Possible Improvements
- Add due dates and reminders for tasks.
- Implement a calendar view for better task scheduling.
- Enhance UI with animations and better design elements.
- Improve task filtering and searching features.

## GitHub Repository
[GitHub Link Here]

This document provides a complete overview of the To-Do List app, helping developers understand its structure, features, and implementation.


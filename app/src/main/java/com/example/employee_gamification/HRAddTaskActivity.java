package com.example.employee_gamification;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.yourpackage.name.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HRAddTaskActivity extends AppCompatActivity {

    private TextView projectTitle, projectDescription, projectCreatedAt, projectDeadline, projectStatus;
    private LinearLayout assignedEmployeesContainer, dialogassigntaskContainer;
    private FirebaseFirestore db;
    private String projectId;
    private Calendar deadlineCalendar;

    private RecyclerView recyclerView;

  private   String name , assignedTo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hradd_task);


//        Toast.makeText(AddTaskActivity.this, "Project not found!", Toast.LENGTH_SHORT).show();
        db = FirebaseFirestore.getInstance();
        deadlineCalendar = Calendar.getInstance();

        // Initialize UI elements
        projectTitle = findViewById(R.id.projectTitle);
        projectDescription = findViewById(R.id.projectDescription);
        projectCreatedAt = findViewById(R.id.projectCreatedAt);
        projectDeadline = findViewById(R.id.projectDeadline);
        projectStatus = findViewById(R.id.projectStatus);

//         Get project ID from intent (Ensure ID is passed correctly)
//        projectId = getIntent().getStringExtra("PROJECT_ID");
        projectId = getIntent().getStringExtra("projectId");
        if (projectId == null || projectId.isEmpty()) {
            Toast.makeText(this, "Invalid Project ID", Toast.LENGTH_SHORT).show();
            Log.e("ProjectDetails", "Received NULL or EMPTY Project ID");
            finish();
            return;
        }
        Log.d("ProjectDetails", "Received Project ID: " + projectId);


//        if (projectId == null || projectId.isEmpty()) {
//            Toast.makeText(this, "Invalid Project ID", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
        loadProjectDetails();
        loadTasks(projectId);
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch tasks from Firestore
//        fetchTasksFromFirestore();
    }
//
//    private void fetchTasksFromFirestore() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//

    /// / Replace "01" with the dynamic project ID if needed
//        String projectId = "01";
//
//        db.collection("alltasks").document(projectId)
//                .collection("tasks")
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    List<TaskModel> taskList = new ArrayList<>();
//
//                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
//                        TaskModel task = doc.toObject(TaskModel.class);
//                        taskList.add(task);
//                    }
//
//                    if (!taskList.isEmpty()) {
//                        TaskAdapter adapter = new TaskAdapter(this, taskList);
//                        recyclerView.setAdapter(adapter);
//                    } else {
//                        Log.d("Firestore", "No tasks found for project: " + projectId);
//                    }
//                })
//                .addOnFailureListener(e -> Log.e("Firestore Error", "Error fetching tasks", e));
//    }
    private void loadProjectDetails() {


        db.collection("projects")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot) {
//                        Toast.makeText(AddTaskActivity.this, "Document ID: " + doc.getId() + " Exists: " + doc.exists() + " Data: " + doc.getData(), Toast.LENGTH_LONG).show();

                        Log.d("Firestore", "Document ID: " + doc.getId() + " Exists: " + doc.exists() + " Data: " + doc.getData());
                    }
                });


        db.collection("projects").document(projectId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        projectTitle.setText(documentSnapshot.getString("projectname"));
//            projectDescription.setText(documentSnapshot.getString("description"));
                        // Fetch timestamps
                        Timestamp createdAtTimestamp = documentSnapshot.getTimestamp("createdAt");
                        Timestamp deadlineTimestamp = documentSnapshot.getTimestamp("deadline");


//                        projectCreatedAt.setText(documentSnapshot.getString("createdAt"));
//                        projectDeadline.setText(documentSnapshot.getString("deadline"));

                        projectStatus.setText(documentSnapshot.getString("status"));

//                         Ensure list of employees exists
                        Object employeesObj = documentSnapshot.get("assignedemployees");
                        if (employeesObj instanceof List<?>) {
                            List<?> tempList = (List<?>) employeesObj;
                            List<String> assignedemployees = new ArrayList<>();
                            for (Object item : tempList) {
                                if (item instanceof String) {
                                    assignedemployees.add((String) item);
                                }
                            }
                            loadEmployeeNames(assignedemployees);
                        }

                    } else {
                        Toast.makeText(HRAddTaskActivity.this, "Project not found!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }

    private void loadEmployeeNames(List<String> employeeIds) {
        for (String employeeId : employeeIds) {
            db.collection("users").document(employeeId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String employeeName = documentSnapshot.getString("name");
                            addEmployeeView(employeeName, employeeId);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error fetching user details", e));
        }
    }

    private void addEmployeeView(String employeeName, String employeeId) {
        // Find the container where the views should be added
        LinearLayout employeesContainer = findViewById(R.id.employeesContainer);

        if (employeesContainer == null) {
            Log.e("NullCheck", "employeesContainer is null");
            return;
        }

        // Inflate the employee item layout and add it to the container
        View employeeView = LayoutInflater.from(this).inflate(R.layout.item_employee_task, employeesContainer, false);

        if (employeeView == null) {
            Log.e("NullCheck", "Inflated view is null");
            return;
        }


        TextView employeeNameView = employeeView.findViewById(R.id.employeeNameText);
        ImageButton assignTaskButton = employeeView.findViewById(R.id.addButton);

        if (employeeNameView == null || assignTaskButton == null) {
            Log.e("NullCheck", "Views inside item_employee_task.xml are null");
            return;
        }

        // Set employee name
        Log.d("Debug", "Setting employee name: " + employeeName);
        employeeNameView.setText(employeeName);

        // Set click listener for the Assign Task button
        assignTaskButton.setOnClickListener(v -> {
            Log.d("Debug", "Button clicked for: " + employeeName);
            showTaskPopup(employeeId, employeeName);
        });

        // Add the view dynamically to the container
        employeesContainer.addView(employeeView);
    }

    private Timestamp selectedDeadlineTimestamp = null;

    private void showTaskPopup(String userId, String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_assign_task, null, false);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText taskInput = dialogView.findViewById(R.id.taskInput);
        EditText deadlineInput = dialogView.findViewById(R.id.deadlineInput);
        Button sendTaskButton = dialogView.findViewById(R.id.sendTaskButton);

        deadlineInput.setOnClickListener(v -> showDateTimePicker(deadlineInput));

        sendTaskButton.setOnClickListener(v -> {
            String task = taskInput.getText().toString().trim();

            if (TextUtils.isEmpty(task) || selectedDeadlineTimestamp == null) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            saveTaskToFirestore(projectId, userId, name, task, selectedDeadlineTimestamp);
//            saveNotificationToFirestore(userId, task,taskId, projectId);
            dialog.dismiss();
            loadTasks(projectId);

        });

        dialog.show();
    }

    private void showDateTimePicker(EditText deadlineInput) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                            (view1, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);

                                Date selectedDate = calendar.getTime();
                                selectedDeadlineTimestamp = new Timestamp(selectedDate);

                                // Only for showing to user
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                deadlineInput.setText(sdf.format(selectedDate));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }


    private void saveTaskToFirestore(String projectId, String userId, String name, String task, Timestamp deadlineTimestamp) {
        // 🔹 Reference to the "alltask" collection for the specific project
        CollectionReference tasksCollection = db.collection("alltask")
                .document(projectId)
                .collection("tasks");

        // 🔹 Get the current count of tasks in "alltask"
        tasksCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int newTaskId = queryDocumentSnapshots.size() + 1; // Generate new task ID
            String taskId = String.format("%02d", newTaskId); // Ensure two-digit format (01, 02, 03...)

            // 🔹 Prepare Task Data
            Map<String, Object> taskData = new HashMap<>();
            taskData.put("assignedTo", userId);
            taskData.put("employeeName", name);
            taskData.put("task", task);
            taskData.put("deadline", deadlineTimestamp); // Keeping deadline as string format
            taskData.put("status", "pending");
            taskData.put("createdAt", Timestamp.now()); // ✅

            // 🔹 Save Task to "alltask"
            tasksCollection.document(taskId).set(taskData)
                    .addOnSuccessListener(aVoid -> {
                        // 🔹 Now, Save Task to "usertaskhistory"
                        saveToUserTaskHistory(userId, projectId, taskId, task, deadlineTimestamp);

                        saveNotificationToFirestore(userId ,task,taskId,projectId);
                        loadTasks(projectId);

                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error saving task to alltask", Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> Log.e("Firestore", "Error fetching task count", e));
    }

    // 🔹 Function to Save Task in "usertaskhistory"
    private void saveToUserTaskHistory(String userId, String projectId, String taskId, String task, Timestamp deadlineTimestamp) {
        // 🔹 Reference to user-specific project task collection (/usertaskhistory/{userId}/{projectId}/)
        CollectionReference userTaskCollection = db.collection("usertaskhistory")
                .document(userId)
                .collection(projectId);

        // 🔹 Get current count of tasks for user in this project
        userTaskCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
//            int newUserTaskId = queryDocumentSnapshots.size() + 1;
//            String userTaskId = String.format("%02d", newUserTaskId);

            String userTaskId = taskId;

            // 🔹 Prepare Task Data for usertaskhistory
            Map<String, Object> userTaskData = new HashMap<>();
            userTaskData.put("task", task);
            userTaskData.put("deadline", deadlineTimestamp);
            userTaskData.put("status", "pending");
            userTaskData.put("createdAt", Timestamp.now()); // ✅


            // 🔹 Save Task with Generated ID
            userTaskCollection.document(userTaskId).set(userTaskData)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Task assigned in usertaskhistory!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error saving task to usertaskhistory", Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> Log.e("Firestore", "Error fetching user task count", e));
    }

    private void saveNotificationToFirestore(String userId, String task,String taskId,String projectId ) {
        CollectionReference notificationsCollection = db.collection("notifications");

        // 🔹 Get the current count of notifications to generate sequential ID
        notificationsCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int newNotificationId = queryDocumentSnapshots.size() + 1; // Increment count
            String notificationId = String.format("%02d", newNotificationId); // Format as 01, 02, 03...

            // 🔹 Prepare Notification Data
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("userid", userId);
            notificationData.put("message", "New task assigned: " + task);
            notificationData.put("createdAt", new Timestamp(new Date()));
            notificationData.put("seen", false);
            notificationData.put("taskId", taskId);
            notificationData.put("projectId",projectId);

            // 🔹 Save Notification with the generated ID
            notificationsCollection.document(notificationId).set(notificationData)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Notification sent"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error saving notification", e));
        }).addOnFailureListener(e -> Log.e("Firestore", "Error fetching notification count", e));
    }

    private void loadTasks(String projectId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference taskRef = db.collection("alltask").document(projectId).collection("tasks");

        taskRef.get().addOnCompleteListener(taskTask -> {
            if (taskTask.isSuccessful() && taskTask.getResult() != null) {
                List<HRTaskModel> taskList = new ArrayList<>();

                for (QueryDocumentSnapshot doc : taskTask.getResult()) {
                    try {
                        String id = doc.getId();
                         assignedTo = doc.getString("assignedTo");
                        String status = doc.getString("status");
                        String taskDetail = doc.getString("task"); // Task description
                        //to get employee name from their id
                                            db.collection("users").document("02").get()
                                                    .addOnSuccessListener(userDoc -> {
                                                        if (userDoc.exists()) {
                                                             name = userDoc.getString("name");

                                                        }
                                                    });
                        // ✅ Handling null or missing timestamp
                        Timestamp timestamp = doc.getTimestamp("deadline");
                        String deadline = "No Deadline"; // Default value if null

                        if (timestamp != null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
                            deadline = sdf.format(timestamp.toDate());
                        }


                        taskList.add(new HRTaskModel(assignedTo,id, taskDetail, deadline, status,name));

                    } catch (Exception e) {
                        Log.e("FirestoreError", "Error processing task document: " + doc.getId(), e);
                    }
                }

                // Sort: Active → Completed → Rejected
                Collections.sort(taskList, (a, b) -> {
                    List<String> order = Arrays.asList("active", "completed", "rejected");
                    return Integer.compare(order.indexOf(a.getStatus()), order.indexOf(b.getStatus()));
                });


                // Set Adapter
                HRTaskHistoryAdapter adapter = new HRTaskHistoryAdapter(taskList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(HRAddTaskActivity.this));
            } else {
                Log.e("Firestore", "Error fetching tasks", taskTask.getException());
            }
        });
    }
}



package com.example.employee_gamification;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.yourpackage.name.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateProjectActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText etProjectName, etProjectDescription, etProjectDeadline;
    private Button btnAddEmployee, btnSubmit;
    private TextView tvSelectedEmployees;

    private Calendar calendar;
    private Timestamp selectedTimestamp;

    private Spinner spinnerCreatedBy;
    private List<String> hrNamesList;
    private Map<String, String> hrMap;

    private List<String> employeeList = new ArrayList<>();
    private List<String> selectedEmployeeIds = new ArrayList<>();

    private String selectedHRId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        db = FirebaseFirestore.getInstance();

        etProjectName = findViewById(R.id.etProjectName);
        spinnerCreatedBy = findViewById(R.id.spinnerCreatedBy);
        hrNamesList = new ArrayList<>();
        hrMap = new HashMap<>();
        hrNamesList.add("Created By");
        fetchHRUsers();

        etProjectDescription = findViewById(R.id.etProjectDescription);
        etProjectDeadline = findViewById(R.id.etProjectDeadline);
        calendar = Calendar.getInstance();
        etProjectDeadline.setOnClickListener(v -> showDatePicker());

        btnAddEmployee = findViewById(R.id.btnAddEmployee);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvSelectedEmployees = findViewById(R.id.tvSelectedEmployees);

        btnAddEmployee.setOnClickListener(v -> showEmployeeDropdown());
        btnSubmit.setOnClickListener(v -> submitProject());
    }

    private void fetchHRUsers() {
        db.collection("users")
                .whereEqualTo("role", "HR")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String hrId = document.getId();
                        String hrName = document.getString("name");
                        if (hrName != null) {
                            String displayText = hrId + " - " + hrName;
                            hrNamesList.add(displayText);
                            hrMap.put(displayText, hrId);
                        }
                    }
                    bindDataToSpinner();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error fetching HR data", Toast.LENGTH_SHORT).show());
    }

    private void bindDataToSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hrNamesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCreatedBy.setAdapter(adapter);

        spinnerCreatedBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedHRName = hrNamesList.get(position);
                    selectedHRId = hrMap.get(selectedHRName);
                    Toast.makeText(CreateProjectActivity.this, "Selected: " + selectedHRName + " (ID: " + selectedHRId + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void showEmployeeDropdown() {
        db.collection("users")
                .whereEqualTo("role", "EMPLOYEE")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> employeeNames = new ArrayList<>();
                        List<String> employeeIds = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                            String employeeId = doc.getId();
                            String employeeName = doc.getString("name");
                            String displayName = employeeId + "-" + employeeName;
                            employeeNames.add(displayName);
                            employeeIds.add(employeeId);
                        }

                        if (!employeeNames.isEmpty()) {
                            showEmployeeDialog(employeeNames, employeeIds);
                        } else {
                            Toast.makeText(this, "No employees found!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showEmployeeDialog(List<String> employeeNames, List<String> employeeIds) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Employee");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, employeeNames);
        builder.setAdapter(adapter, (dialog, which) -> {
            String selectedEmployeeId = employeeIds.get(which);
            String selectedDisplay = employeeNames.get(which);
            if (!selectedEmployeeIds.contains(selectedEmployeeId)) {
                selectedEmployeeIds.add(selectedEmployeeId);
                employeeList.add(selectedDisplay);
                updateSelectedEmployeesText();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    showTimePicker();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);

                    selectedTimestamp = new Timestamp(calendar.getTime());

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    String formattedDate = sdf.format(calendar.getTime());
                    etProjectDeadline.setText(formattedDate);

                    Toast.makeText(this, "Timestamp set", Toast.LENGTH_SHORT).show();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateSelectedEmployeesText() {
        StringBuilder selectedText = new StringBuilder("Selected Employees:\n");
        for (String emp : employeeList) {
            selectedText.append(emp).append("\n");
        }
        tvSelectedEmployees.setText(selectedText.toString());
    }

    private void submitProject() {
        db.collection("projects").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int projectCount = task.getResult().size();
                String projectId = String.format("%02d", projectCount + 1);

                Map<String, Object> projectData = new HashMap<>();
                projectData.put("projectname", etProjectName.getText().toString());
                projectData.put("createdBy", selectedHRId);
                projectData.put("createdAt", Timestamp.now()); // ✅ Timestamp instead of formatted string
                projectData.put("description", etProjectDescription.getText().toString());
                projectData.put("deadline", selectedTimestamp); // ✅ Stored as Timestamp
                projectData.put("assignedemployees", selectedEmployeeIds);
                projectData.put("status", "active");

                db.collection("projects").document(projectId).set(projectData)
                        .addOnSuccessListener(aVoid -> {
                            db.collection("notrifications").get().addOnSuccessListener(querySnapshot -> {
                                int notificationCount = querySnapshot.size() + 1;

                                for (String employeeId : selectedEmployeeIds) {
                                    String notificationId = String.format("%02d", notificationCount++);
                                    Map<String, Object> notificationData = new HashMap<>();
                                    notificationData.put("message", "You are assigned to a new project: " + etProjectName.getText().toString());
                                    notificationData.put("createdAt", Timestamp.now()); // ✅ Use Timestamp
                                    notificationData.put("seen", false);
                                    notificationData.put("userid", employeeId);
                                    notificationData.put("deadline", selectedTimestamp); // ✅ Use Timestamp

                                    db.collection("notrifications").document(notificationId).set(notificationData);
                                }

                                Toast.makeText(CreateProjectActivity.this, "Project Created & Notifications Sent", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, HrActivity.class));
                                finish();

                            }).addOnFailureListener(e -> Toast.makeText(CreateProjectActivity.this, "Notification Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        });
            }
        });
    }
}

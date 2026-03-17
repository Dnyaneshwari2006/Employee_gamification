package com.example.employee_gamification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.yourpackage.name.R;

import java.util.ArrayList;
import java.util.List;

public class CeoHomeActivity extends AppCompatActivity {

    private RecyclerView topEmployeesRecyclerView, activeProjectsRecyclerView;
    private CEO_EmployeeAdapter employeeAdapter;
    private CEO_ProjectAdapter projectAdapter;
    private FirebaseFirestore db;
    private TextView welcomeText;

    private FirebaseAuth mAuth;

    private Button Leaderboard ,AddEmployee , btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ceo_home);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();


        Leaderboard = findViewById(R.id.btnLeaderboard);
       /* AddEmployee = findViewById(R.id.btnAddEmployee);*/

        btnLogout=findViewById(R.id.btnLogout);

        Leaderboard.setOnClickListener(v -> {
            Intent intent = new Intent(CeoHomeActivity.this, CEOLeaderboardActivity.class);
            startActivity(intent);
        });

     /*   AddEmployee.setOnClickListener(v -> {
            Intent intent = new Intent(CeoHomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });*/



        // Initialize views
        topEmployeesRecyclerView = findViewById(R.id.topEmployeesRecyclerView);
        activeProjectsRecyclerView = findViewById(R.id.activeProjectsRecyclerView);
        welcomeText = findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome CEO");

        // Set layout managers
        topEmployeesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        activeProjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapters with empty lists
        employeeAdapter = new CEO_EmployeeAdapter(new ArrayList<>());
        projectAdapter = new CEO_ProjectAdapter(new ArrayList<>(), this::onProjectClick);

        // Attach adapters
        topEmployeesRecyclerView.setAdapter(employeeAdapter);
        activeProjectsRecyclerView.setAdapter(projectAdapter);

        // Fetch data from Firestore
        fetchTopEmployees();
        fetchActiveProjects();

        setupLogout();

    }

    private void fetchTopEmployees() {
        db.collection("users")
                .orderBy("points", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CEO_Employee> employees = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        CEO_Employee employee = doc.toObject(CEO_Employee.class);
                        if (employee != null) {
                            employees.add(employee);
                        }
                    }
                    Log.d("FIREBASE", "Top Employees loaded: " + employees.size());
                    employeeAdapter.setEmployeeList(employees);
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Failed to fetch top employees", e);
                });
    }


    private void fetchActiveProjects() {
        db.collection("projects")
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CEO_Project> projects = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        CEO_Project project = doc.toObject(CEO_Project.class);
                        if (project != null) {
                            project.setId(doc.getId());  // Ensure ID is set from Firestore
                            projects.add(project);
                        }
                    }
                    Log.d("FIREBASE", "Active projects loaded: " + projects.size());
                    projectAdapter.setProjectList(projects);
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Failed to fetch projects", e);
                });
    }


    private void onProjectClick(CEO_Project project) {
        Intent intent = new Intent(this, CeoprojectdetailsActivity.class);
        intent.putExtra("projectId", project.getId());
        startActivity(intent);
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(CeoHomeActivity.this, LoginActivity.class); // or LoginActivity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Logout failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

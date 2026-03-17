package com.example.employee_gamification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yourpackage.name.R;

import java.util.ArrayList;
import java.util.List;

public class ProjectListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
//    private String currentUserId = "02"; // Replace with actual logged-in employee ID

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private List<ProjectModel> projectList;
     String hrEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectl_list); // make sure you have this layout

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        projectList = new ArrayList<>();
        adapter = new ProjectAdapter(this, projectList);
        recyclerView.setAdapter(adapter);


//        Intent intent = getIntent();
         hrEmail = getIntent().getStringExtra("Email");
        if (hrEmail == null) {
            Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
            return;
        }

//        Toast.makeText(this, hrEmail, Toast.LENGTH_LONG).show();

        // Or however you pass email from LoginActivity
        loadProjectsForHR(hrEmail);
    }

    private void loadProjectsForHR(String Email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
   //  Toast.makeText(this, Email, Toast.LENGTH_LONG).show();

        // Step 1: Get the HR userId from the users collection using the email
        db.collection("users")
                .whereEqualTo("email",Email)
                .get()
                .addOnSuccessListener(userQuery -> {
                    if (userQuery == null || userQuery.isEmpty()) {
                        Toast.makeText(this, "No HR found with this email", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // Get userId (document ID of the user)
                    String hrUserId = userQuery.getDocuments().get(0).getId();

                    // Step 2: Fetch projects where createdBY == hrUserId
                    db.collection("projects")
                            .whereEqualTo("createdBy", hrUserId)
                            .get()
                            .addOnSuccessListener(projectQuery -> {
                                if (projectQuery == null || projectQuery.isEmpty()) {
                                    Toast.makeText(this, "No projects found for this HR", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                projectList.clear();

                                for (DocumentSnapshot doc : projectQuery) {
                                    String projectId = doc.getId();
                                    String projectName = doc.getString("projectname");
                                    projectList.add(new ProjectModel(projectId, projectName));
                                }

                                adapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error loading projects: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching HR ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}

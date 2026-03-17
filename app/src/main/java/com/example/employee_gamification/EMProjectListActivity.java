package com.example.employee_gamification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.employee_gamification.ProjectAdapter;
import com.example.employee_gamification.ProjectModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yourpackage.name.R;

import java.util.ArrayList;
import java.util.List;

public class EMProjectListActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String currentUserId ; // Replace with actual logged-in employee ID

    private RecyclerView recyclerView;
    private EMProjectAdapter adapter;
    private List<EMprojectModel> projectList;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectl_list); // make sure you have this layout

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        projectList = new ArrayList<>();
        adapter = new EMProjectAdapter(this, projectList);
        recyclerView.setAdapter(adapter);



        // Safe handling of Intent extras
        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
             email = intent.getStringExtra("Email"); // or use getExtras().getString("email")
            getUserIdFromEmail(email);
            Log.d("IntentEmail", "Email received: " + email);
            // Do something with email
        } else {
            Log.e("IntentError", "Intent or extras are null!");
        }


        loadProjects();
    }


    private void getUserIdFromEmail(String useremail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("email", useremail)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        currentUserId = querySnapshot.getDocuments().get(0).getId();
                        Log.d("UserID", "User ID: " + currentUserId);
                        // Use the userId here as needed
                    } else {
                        Log.d("UserID", "No user found with this email.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserID", "Error getting user ID", e);
                });
    }

    private void loadProjects() {
        db.collection("projects")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No projects found in Firestore", Toast.LENGTH_LONG).show();
                        Log.e("Firestore", "No documents found in 'projects' collection.");
                        return;
                    }

                    projectList.clear();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        List<String> assignedEmployees = (List<String>) document.get("assignedemployees");

                        if (assignedEmployees != null && assignedEmployees.contains(currentUserId)) {
                            String projectId = document.getId();
                            String projectName = document.getString("projectname");
                            projectList.add(new EMprojectModel(projectId, projectName,currentUserId));
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error loading projects: " + e.getMessage());
                });
    }
}

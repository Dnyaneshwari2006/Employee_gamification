package com.example.employee_gamification;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.*;
import com.yourpackage.name.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HRSubmissionActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HRSubmissionAdapter adapter;
    private List<HRSubmissionModel> submissionList;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hrtasksubmission); // Make sure this layout exists

        recyclerView = findViewById(R.id.recyclerView);  // Should match ID in XML
        progressBar = findViewById(R.id.progressBar);    // Should match ID in XML

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        submissionList = new ArrayList<>();
        adapter = new HRSubmissionAdapter(this, submissionList);
        recyclerView.setAdapter(adapter);

        fetchCompletedTasks(); // Make sure this method exists in this class
    }

    private void fetchCompletedTasks() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("alltask")
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    for (QueryDocumentSnapshot projectDoc : querySnapshots) {
                        String projectId = projectDoc.getId(); // dynamic project ID

                        db.collection("alltask")
                                .document(projectId)
                                .collection("tasks")
                                .whereEqualTo("status", "completed")
                                .get()
                                .addOnSuccessListener(taskSnapshots -> {
                                    for (QueryDocumentSnapshot taskDoc : taskSnapshots) {
                                        String taskId = taskDoc.getId();
                                        String taskName = taskDoc.getString("task");
                                        fetchSubmissions(projectId, taskId, taskName); // pass dynamic projectId
                                    }
                                });
                    }
                });
    }

    private void fetchSubmissions(String projectId, String taskId, String taskName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("submissions")
                .document(projectId)
                .collection("tasksubmission")
                .document(taskId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String submittedById = documentSnapshot.getString("submittedBy");

                        if (submittedById != null) {
                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(submittedById)
                                    .get()
                                    .addOnSuccessListener(userDocument -> {
                                        String employeeName = userDocument.exists() ? userDocument.getString("name") : "Unknown";

                                        // Format timestamp
                                        Timestamp timestamp = documentSnapshot.getTimestamp("submissionTime");
                                        String submissionTime = "N/A";
                                        if (timestamp != null) {
                                            Date date = timestamp.toDate();
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                                            submissionTime = sdf.format(date);
                                        }

                                        String reply = documentSnapshot.getString("reply");
                                        String attachment = documentSnapshot.getString("attachment");

                                        HRSubmissionModel submission = new HRSubmissionModel(taskName, employeeName, submissionTime, reply, attachment);
                                        submissionList.add(submission);
                                        adapter.notifyDataSetChanged();
                                        progressBar.setVisibility(View.GONE);
                                    });
                        } else {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    }

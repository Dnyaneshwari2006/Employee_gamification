package com.example.employee_gamification;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.yourpackage.name.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EMNotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private final List<NotificationModel> notificationList = new ArrayList<>();
    private FirebaseFirestore db;
//    private final String userId = "03";  // Modify as needed

    private  String userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Ensure activity_emnotification.xml exists in res/layout
        setContentView(R.layout.activity_emnotification);

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(this, notificationList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();


        //Get email from LoginActivity
        userEmail = getIntent().getStringExtra("Email");

        if (userEmail != null) {
            getEmployeeID(userEmail);
        } else {
            Toast.makeText(this, "Email not received", Toast.LENGTH_SHORT).show();
        }

    }

    public void getEmployeeID(String userEmail) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String userId = queryDocumentSnapshots.getDocuments().get(0).getId();

                        fetchNotifications(userId);
                        // Do something with the userId here
                        Log.d("UserID", "User ID: " + userId);
//                        Toast.makeText(this, "User ID: " + userId, Toast.LENGTH_SHORT).show();
                        // You can also store it in a member variable if needed
                    } else {
                        Log.d("UserID", "No user found with this email.");
                        Toast.makeText(this, "No user found with this email.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserID", "Error getting user ID", e);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void fetchNotifications(String userId) {
        db.collection("notifications")
                .whereEqualTo("seen", false)
                .whereEqualTo("userid", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null || value.isEmpty()) {
                        return;
                    }

                    notificationList.clear();

                    for (QueryDocumentSnapshot document : value) {
                        NotificationModel notification = new NotificationModel();
                        notification.setId(document.getId());
                        notification.setMessage(document.getString("message"));

                        Timestamp createdAtTimestamp = document.getTimestamp("createdAt");
                        if (createdAtTimestamp != null) {
                            String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                    .format(createdAtTimestamp.toDate());
                            notification.setCreatedAt(formattedDate);
                        }

                        Timestamp deadlineTimestamp = document.getTimestamp("deadline");
                        if (deadlineTimestamp != null) {
                            String formattedDeadline = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                    .format(deadlineTimestamp.toDate());
                            notification.setDeadline(formattedDeadline);
                        }

                        String userid = document.getString("userid");
                        if (userid != null) {
                            notification.setuserid(userid);
                        }

                        String taskId = document.getString("taskId");
                        if (taskId != null) {
                            notification.settaskId(taskId);
                        }

                        String projectId = document.getString("projectId");
                        if (projectId != null) {
                            notification.setprojectId(projectId);
                        }



                        Boolean seenValue = document.getBoolean("seen");
                        notification.setSeen(seenValue != null && seenValue);



                        notificationList.add(notification);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}

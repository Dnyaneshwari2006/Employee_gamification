package com.example.employee_gamification;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yourpackage.name.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.UUID;

public class EmployeeDashboard extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton menuButton;
    ActionBarDrawerToggle toggle;

    private RecyclerView projectlistrecyclerView;
    private EMTaskHistoryAdapter taskAdapter;
    private List<EMTaskModel> taskList = new ArrayList<>();

    private RecyclerView rvCalendar, recentActivityRecycler;
    private FirebaseFirestore db;
//    private String userId = "03"; // You can set this dynamically based on login
    private String userEmail;

    private Dialog currentReplyDialog;
    private TextView selectedFileNameText;


    private ProjectAdapter adapter;
    private List<ProjectModel> projectList;

    private static final int REQUEST_CODE_PICK_IMAGE = 101;
    private static final int REQUEST_CODE_PICK_PDF = 102;
    private static final int REQUEST_CODE_PICK_ZIP = 103;


    private TextView tvHelloName;
    private ImageView ivUserIcon;

    private String userName , email;
    String userId;
    private String currentprojectid;

    private Uri selectedImageUri, selectedPdfUri, selectedZipUri, doneImageUri;
    private String projectID ; // dynamically set as needed
    private String taskId;    // dynamically set as needed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employee_dashboard);

        recentActivityRecycler = findViewById(R.id.nearestDeadlineRecycler); // make sure this ID matches your XML
        recentActivityRecycler.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new EMTaskHistoryAdapter(taskList, new EMTaskHistoryAdapter.OnAddClickListener() {
            @Override
            public void onAddClicked(String projectID, String taskID) {
                showReplyDialog(projectID,taskID); // this opens your dialog

            }
        }
            );
        recentActivityRecycler.setAdapter(taskAdapter);
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        tvHelloName = findViewById(R.id.employeeNameText);
        ivUserIcon = findViewById(R.id.userIcon);
        projectlistrecyclerView = findViewById(R.id.projectlistrecyclerView);
        rvCalendar = findViewById(R.id.rvEmployeeCalendar);
        recentActivityRecycler = findViewById(R.id.projectlistrecyclerView);

        // RecyclerView setup for projects
        projectList = new ArrayList<>();
        adapter = new ProjectAdapter(this, projectList);
        projectlistrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        projectlistrecyclerView.setAdapter(adapter);


        // Get email from LoginActivity
        userEmail = getIntent().getStringExtra("email");

        if (userEmail != null) {
            fetchEmployeeDetails(userEmail);
        } else {
            Toast.makeText(this, "Email not received", Toast.LENGTH_SHORT).show();
        }



    drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        menuButton = findViewById(R.id.menuButton);


        // Open drawer on 3-dot button click
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }

        });


//        db.collection("usertaskhistory").document("02")
//                .collection("03")
//                .document("01")
//                .get()
//                .addOnSuccessListener(taskDoc -> {
//                    String taskTitle = taskDoc.getString("task");
//                    Timestamp ts = taskDoc.getTimestamp("deadline");
//                    Toast.makeText(this, taskTitle+ts, Toast.LENGTH_LONG).show();
//
//
//                });




        // Get email from LoginActivity
        userEmail = getIntent().getStringExtra("email");

        if (userEmail != null) {
            fetchEmployeeDetails(userEmail);
            getUserInfoFromEmail(userEmail);
            email=userEmail;

        } else {
            Toast.makeText(this, "Email not received", Toast.LENGTH_SHORT).show();
        }

        // Setup horizontal calendar
        rvCalendar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<Date> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            dateList.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        EmployeeCalendarAdapter adapterCalendar = new EmployeeCalendarAdapter(this, dateList, date -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Log.d("Calendar", "Selected date: " + sdf.format(date));
        });

        rvCalendar.setAdapter(adapterCalendar);

        NavigationView navigationView = findViewById(R.id.navigationView);


        // Open drawer on 3-dot button click
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Intent intent = new Intent();
                if (id == R.id.nav_dashboard) {
                    //   startActivity(new Intent(EmployeeDashboard.this, EmployeeDashboard.class));
                    return true;
                } else if (id == R.id.nav_leaderboard) {
                    startActivity(new Intent(EmployeeDashboard.this, EMLeaderboardActivity.class));
                    intent.putExtra("Email",email);

                    return true;
                } else if (id == R.id.nav_notifications) {
                    Intent i = new Intent(EmployeeDashboard.this, EMNotificationActivity.class);
                    i.putExtra("Email", userEmail);
                    startActivity(i);

                    return true;
                }
                else if (id == R.id.nav_settings) {
                    Intent i = new Intent(EmployeeDashboard.this, ProfileActivity.class);
                    i.putExtra("Email", userEmail);
                    Toast.makeText(EmployeeDashboard.this, "Email"+userEmail, Toast.LENGTH_SHORT).show();
                    startActivity(i);

                    return true;
                }
                    else if (id == R.id.nav_Project) {
                    Intent i = new Intent(EmployeeDashboard.this ,EMProjectListActivity.class);
                    i.putExtra("Email",email);
                    startActivity(i);
//                    startActivity(new Intent(EmployeeDashboard.this, EMProjectListActivity.class));
//                    intent.putExtra("Email",email);


                    return true;
                } else if (id == R.id.nav_logout) {
                    startActivity(new Intent(EmployeeDashboard.this, LoginActivity.class));
                    finish();
                    return true;
                }

                return false;
            }
        });



        // Load task logic
    }


    private void fetchEmployeeDetails(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            userId = doc.getId();
                            userName = doc.getString("name");

                            tvHelloName.setText(userName);
                            //loadProjects();
                            break;
                        }
                    } else {
                        Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


//    private void loadProjects() {
//        db.collection("projects")
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    if (queryDocumentSnapshots == null || queryDocumentSnapshots.isEmpty()) {
//                        Toast.makeText(this, "No projects found", Toast.LENGTH_LONG).show();
//                        Log.e("Firestore", "No documents found in 'projects' collection.");
//                        return;
//                    }
//
//                    projectList.clear();
//                    for (DocumentSnapshot document : queryDocumentSnapshots) {
//                        List<String> assignedEmployees = (List<String>) document.get("assignedemployees");
//
//                        if (assignedEmployees != null && assignedEmployees.contains(userId)) {
//                            String projectId = document.getId();
//                            String projectName = document.getString("projectname");
//                            projectList.add(new ProjectModel(projectId, projectName));
//                        }
//                    }
//                    adapter.notifyDataSetChanged();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
//    }


    private void getUserInfoFromEmail(String email) {
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(querySnapshots -> {
                    if (!querySnapshots.isEmpty()) {
                        DocumentSnapshot userDoc = querySnapshots.getDocuments().get(0);
                        String userId = userDoc.getId();
                        String name = userDoc.getString("name");
                     //   Toast.makeText(this,userId+name , Toast.LENGTH_SHORT).show();

                        List<String> assignedProjects = (List<String>) userDoc.get("assigndprojects");

                        if (assignedProjects != null) {
                            getProjectNames(userId, assignedProjects);
                        }
                    }
                });
    }

    private void getProjectNames(String userId, List<String> projectIds) {
        for (String projectId : projectIds) {
            db.collection("projects").document(projectId)
                    .get()
                    .addOnSuccessListener(projectDoc -> {
                        String projectName = projectDoc.getString("projectname");
                   //     Toast.makeText(this,projectName , Toast.LENGTH_SHORT).show();

                        if (projectName != null) {
                            loadTasks(userId, projectId, projectName);
                        }
                    });
        }
    }

    private void loadTasks(String userId, String projectId, String projectName) {
        db.collection("usertaskhistory").document(userId)
                .collection(projectId)
                .get()
                .addOnSuccessListener(groupDocs -> {
                    for (DocumentSnapshot taskGroupDoc : groupDocs) {
                        String taskDocId = taskGroupDoc.getId();
                       // Toast.makeText(this, taskDocId, Toast.LENGTH_SHORT).show();

                        db.collection("usertaskhistory").document(userId)
                                .collection(projectId)
                                .document(taskDocId)
                                .get()
                                .addOnSuccessListener(taskDoc -> {
                                    String taskTitle = taskDoc.getString("task");
                                    String status = taskDoc.getString("status");
                                    Timestamp ts = taskDoc.getTimestamp("deadline");
                                    if (taskTitle != null && ts != null) {
                                        Date deadlineDate = ts.toDate();
                                        String formattedDate = formatDate(deadlineDate); // Converts to "dd/MM/yyyy" format

                                        taskList.add(new EMTaskModel(userId, taskDocId,taskTitle,formatDate(deadlineDate), status,projectName,projectId));

                                        // ✅ FIX: notify using adapter instance
                                        taskAdapter.notifyDataSetChanged();
                                    }
                                });



                    }
                });
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }


    private void showReplyDialog(String currprojectID , String currtaskID) {
        currentReplyDialog = new Dialog(this);
        currentReplyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        currentReplyDialog.setContentView(R.layout.reply_popup);

        View popupLayout = currentReplyDialog.findViewById(R.id.replyPopupLayout);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        popupLayout.startAnimation(slideUp);

        EditText editTextReply = currentReplyDialog.findViewById(R.id.editTextReply);
        ImageButton btnImage = currentReplyDialog.findViewById(R.id.btnImage);
        ImageButton btnPdf = currentReplyDialog.findViewById(R.id.btnPdf);
        ImageButton btnZip = currentReplyDialog.findViewById(R.id.btnZip);
        ImageButton btnDone = currentReplyDialog.findViewById(R.id.btnDoneImage);
        Button btnSubmit = currentReplyDialog.findViewById(R.id.btnSubmitReply);

        selectedFileNameText = currentReplyDialog.findViewById(R.id.selectedFileNameText);

       // Toast.makeText(this ,currprojectID + currtaskID, Toast.LENGTH_SHORT).show();


        btnImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
        });

        btnPdf.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select PDF"), REQUEST_CODE_PICK_PDF);
        });

        btnZip.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/zip");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select ZIP File"), REQUEST_CODE_PICK_ZIP);
        });

        btnDone.setOnClickListener(v -> {
            doneImageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    getResources().getResourcePackageName(R.drawable.ic_done_mark) + '/' +
                    getResources().getResourceTypeName(R.drawable.ic_done_mark) + '/' +
                    getResources().getResourceEntryName(R.drawable.ic_done_mark));
            selectedImageUri = null;
            selectedPdfUri = null;
            selectedZipUri = null;
            if (selectedFileNameText != null) {
                selectedFileNameText.setText("Selected: Done Image");
            }
            Toast.makeText(this, "Predefined Done Image Set", Toast.LENGTH_SHORT).show();
        });

        btnSubmit.setOnClickListener(v -> {
            String replyText = editTextReply.getText().toString().trim();

            if (replyText.isEmpty()) {
                Toast.makeText(this, "Reply cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri fileUri = selectedImageUri != null ? selectedImageUri :
                    selectedPdfUri != null ? selectedPdfUri :
                            selectedZipUri != null ? selectedZipUri :
                                    doneImageUri;

            if (fileUri != null) {
                uploadFileToStorage(fileUri, replyText, currentReplyDialog ,currprojectID , currtaskID);
            } else {
                saveReplyToFirestore(null, replyText, currentReplyDialog,currprojectID,currtaskID);
            }
        });

        currentReplyDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        currentReplyDialog.show();
    }

    private void uploadFileToStorage(Uri fileUri, String replyText, Dialog dialog , String currprojectID , String currtaskID ) {
        String fileName = "submissions/" + UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(fileName);

        storageRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            saveReplyToFirestore(downloadUrl, replyText, dialog,currprojectID , currtaskID);
                        }))
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to upload file", Toast.LENGTH_SHORT).show());
    }

    private void saveReplyToFirestore(String attachmentUrl, String replyText, Dialog dialog , String currprojectID , String currtaskID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String submittedBy = userId;

        Map<String, Object> submission = new HashMap<>();
        submission.put("reply", replyText);
        submission.put("submissionTime", FieldValue.serverTimestamp());
        submission.put("submittedBy", submittedBy);
        submission.put("attachment", attachmentUrl);

        db.collection("submissions")
                .document(currprojectID)
                .collection("tasksubmission")
                .document(currtaskID)
                .set(submission)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Reply submitted successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    db.collection("submissions")
                            .document(currprojectID)
                            .collection("tasksubmission")
                            .document(currtaskID)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                Timestamp completedAt = documentSnapshot.getTimestamp("submissionTime");
                                if (completedAt != null) {
                                    Map<String, Object> updateAllTask = new HashMap<>();
                                    updateAllTask.put("status", "completed");
                                    updateAllTask.put("completedAt", completedAt);

                                    db.collection("alltask")
                                            .document(currprojectID)
                                            .collection("tasks")
                                            .document(currtaskID)
                                            .update(updateAllTask);

                                    Map<String, Object> updateHistory = new HashMap<>();
                                    updateHistory.put("status", "completed");
                                    updateHistory.put("completedAt", completedAt);

                                    db.collection("usertaskhistory")
                                            .document(submittedBy)
                                            .collection(currprojectID)
                                            .document(currtaskID)
                                            .update(updateHistory);
                                }

                                db.collection("users").document(submittedBy).get().addOnSuccessListener(userSnapshot -> {
                                    Long currentPoints = userSnapshot.getLong("points");
                                    if (currentPoints != null) {
                                        db.collection("users").document(submittedBy).update("points", currentPoints + 50);
                                    }
                                });
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error submitting reply", Toast.LENGTH_SHORT).show());


        //after submission all list should be update

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            String fileName = getFileNameFromUri(fileUri);

            switch (requestCode) {
                case REQUEST_CODE_PICK_IMAGE:
                    selectedImageUri = fileUri;
                    selectedPdfUri = null;
                    selectedZipUri = null;
                    doneImageUri = null;
                    break;
                case REQUEST_CODE_PICK_PDF:
                    selectedPdfUri = fileUri;
                    selectedImageUri = null;
                    selectedZipUri = null;
                    doneImageUri = null;
                    break;
                case REQUEST_CODE_PICK_ZIP:
                    selectedZipUri = fileUri;
                    selectedImageUri = null;
                    selectedPdfUri = null;
                    doneImageUri = null;
                    break;
            }

            if (selectedFileNameText != null) {
                selectedFileNameText.setText("Selected: " + fileName);
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }


}
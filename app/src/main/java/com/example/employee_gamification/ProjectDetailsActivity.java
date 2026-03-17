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
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yourpackage.name.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ProjectDetailsActivity extends AppCompatActivity {
    FirebaseFirestore db;
    TextView projectName, createdBy, deadline, status;

    private RecyclerView recyclerView;

    private static final int REQUEST_CODE_PICK_IMAGE = 101;
    private static final int REQUEST_CODE_PICK_PDF = 102;
    private static final int REQUEST_CODE_PICK_ZIP = 103;


    private Uri selectedImageUri, selectedPdfUri, selectedZipUri, doneImageUri;
    private Dialog currentReplyDialog;
    private TextView selectedFileNameText;
    //mically set as needed
   // private String taskId = "03";    // dynamically set as needed
 private String projectId;

 String userId;
 private String projectname;
    private Dialog replyDialog; // Keep this globally accessible
    //Get email from LoginActivity

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        db = FirebaseFirestore.getInstance();
        projectName = findViewById(R.id.projectName);
        createdBy = findViewById(R.id.createdBy);
        deadline = findViewById(R.id.deadline);
        status = findViewById(R.id.status);
//
//        projectId = getIntent().getStringExtra("projectId");
//
//          Intent intent = new Intent();
//        userId = intent.getExtras().getString("currentUserId");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

            //        projectId = getIntent().getStringExtra("projectId");

        // Use the intent that started this activity
        Intent intent = getIntent();

           // Get extras safely
        userId = intent.getStringExtra("currentUserId");
         projectId = intent.getStringExtra("projectId");

        if (projectId != null && userId != null) {
            Log.d("Firestore", "Received Project ID: " + projectId);
            Log.d("Firestore", "Received User ID: " + userId);

            loadProjectDetails();
            fetchEmployeeTasks(userId, projectId);

        } else {
            Toast.makeText(this, "Invalid Project or User ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadProjectDetails() {
        db.collection("projects").document(projectId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d("Firestore", "Project Data: " + documentSnapshot.getData());

                        projectName.setText(documentSnapshot.getString("projectname"));
                        projectname = documentSnapshot.getString("projectname");
                        createdBy.setText("Created By: " + documentSnapshot.getString("createdBy"));
//                        deadline.setText("Deadline: " + documentSnapshot.getString("deadline"));
                        Timestamp deadlineTimestamp = documentSnapshot.getTimestamp("deadline");
                        if (deadlineTimestamp != null) {
                            Date deadlineDate = deadlineTimestamp.toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                            String formattedDeadline = sdf.format(deadlineDate);
                            deadline.setText("Deadline: " + formattedDeadline);
                        } else {
                            deadline.setText("Deadline: N/A");
                        }


                        status.setText("Current Status: " + documentSnapshot.getString("status"));
                    } else {
                        Toast.makeText(this, "Project Not Found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Failed to load project details", e);
                });

    }




    private void fetchEmployeeTasks(String userId, String projectId) {

//        Toast.makeText(this, userId, Toast.LENGTH_LONG).show();

//        Toast.makeText(this, projectId, Toast.LENGTH_LONG).show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<EMTaskModel> taskList = new ArrayList<>();

        Log.d("FirestoreDebug", "Fetching tasks for userId: " + userId + ", projectId: " + projectId);

        db.collection("usertaskhistory").document(userId).collection(projectId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("FirestoreDebug", "No tasks found for this user and project.");
                        return; // Stop execution if no tasks
                    }

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String taskId = document.getId();
                        String taskName = document.getString("task");
                        String status = document.getString("status");

                        // Convert deadline timestamp
                        Timestamp deadlineTimestamp = document.getTimestamp("deadline");
                        String deadlineFormatted = "N/A";
                        if (deadlineTimestamp != null) {
                            Date deadlineDate = deadlineTimestamp.toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                            deadlineFormatted = sdf.format(deadlineDate);
                        }

                        // Create and add to the list
                        EMTaskModel task = new EMTaskModel(userId ,taskId, taskName, deadlineFormatted, status,projectname, projectId);
                        taskList.add(task);

                        Log.d("FirestoreDebug", "Task added: " + taskName + " - " + status);
                    }

//                     Sort tasks
                    Collections.sort(taskList, (a, b) -> {
                        List<String> order = Arrays.asList("active", "completed", "rejected");
                        return Integer.compare(order.indexOf(a.getStatus()), order.indexOf(b.getStatus()));
                    });

//                     Ensure RecyclerView is initialized
                    if (recyclerView == null) {
                        Log.e("RecyclerViewDebug", "RecyclerView is null!");
                        return;
                    }

                    // Set adapter
                    EMTaskHistoryAdapter2 adapter = new EMTaskHistoryAdapter2(taskList, new EMTaskHistoryAdapter2.OnAddClickListener() {
                        @Override
                        public void onAddClicked(String projectID, String taskID) {
                            showReplyDialog(projectID,taskID); // this opens your dialog

                        }

                    });
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ProjectDetailsActivity.this));
                })
                .addOnFailureListener(e -> Log.e("FirestoreDebug", "Error fetching tasks", e));

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

        Toast.makeText(this ,currprojectID + currtaskID, Toast.LENGTH_SHORT).show();


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

                                db.collection("usertaskhistory")
                                        .document(submittedBy)
                                        .collection(currprojectID)
                                        .document(currtaskID)
                                        .get()
                                        .addOnSuccessListener(documentSnapshot1 -> {
                                            if (documentSnapshot1.exists()) {
                                                // Get deadline and completedAt timestamps
                                                Timestamp deadline = documentSnapshot1.getTimestamp("deadline");
                                                Timestamp completedAt1 = documentSnapshot1.getTimestamp("completedAt");

                                                if (deadline != null && completedAt1 != null) {
                                                    // Fetch current user points
                                                    db.collection("users").document(submittedBy).get()
                                                            .addOnSuccessListener(userSnapshot -> {
                                                                Long currentPoints = userSnapshot.getLong("points");
                                                                if (currentPoints != null) {
                                                                    long updatedPoints;
                                                                    if (completedAt1.compareTo(deadline) <= 0) {
                                                                        // Submitted on time: +50
                                                                        updatedPoints = currentPoints + 50;
                                                                    } else {
                                                                        // Submitted late: -50
                                                                        updatedPoints = currentPoints - 50;
                                                                    }

                                                                    db.collection("users").document(submittedBy)
                                                                            .update("points", updatedPoints);
                                                                }
                                                            });
                                                } else {
                                                    Log.w("POINTS", "Deadline or CompletedAt is null");
                                                }
                                            } else {
                                                Log.w("FIRESTORE", "No task history found for the user/task");
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("FIRESTORE", "Error fetching task history", e);
                                        });



                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error submitting reply", Toast.LENGTH_SHORT).show());
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



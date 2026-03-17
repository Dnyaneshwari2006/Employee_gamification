package com.example.employee_gamification;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yourpackage.name.R;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private ImageView imgProfile;
    private TextView tvName, tvEmail, tvRole, tvPoints;
    private EditText etNewPassword;
    private Button btnChangePassword, btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private Uri imageUri;
    private String docId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(com.yourpackage.name.R.layout.activity_profile);
//
//        //ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        setContentView(R.layout.activity_profile);


        // Init views
        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        tvPoints = findViewById(R.id.tvPoints);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

      //  Intent intent = new Intent();
     //   String user = intent.getExtras().getString("Email");

        String user = getIntent().getStringExtra("Email"); // ✅ dynamic from intent
        Toast.makeText(this, "passed email:"+user, Toast.LENGTH_SHORT).show();
        //String user="vaibhavhakke01@gmail.com";
        loadProfile(user);
        setupPasswordChange();
        setupLogout();

        imgProfile.setOnClickListener(v -> openImagePicker());
    }



    private void loadProfile(String user) {
        try {
            if (user != null && !user.isEmpty()) {
                db.collection("users")
                        .whereEqualTo("email", user)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                                docId = snapshot.getId();

                                tvName.setText("Name: " + snapshot.getString("name"));
                                tvEmail.setText("Email: " + snapshot.getString("email"));
                                tvRole.setText("Role: " + snapshot.getString("role"));
                                tvPoints.setText("Points: " + snapshot.getLong("points"));

                                String profileUrl = snapshot.getString("profileUrl");
                                if (profileUrl != null && !profileUrl.isEmpty()) {
                                    Glide.with(this)
                                            .load(profileUrl)
                                            .into(imgProfile);
                                }
                            } else {
                                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Email is null or empty", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void openImagePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        } catch (Exception e) {
            Toast.makeText(this, "Error opening gallery: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
                imageUri = data.getData();
                imgProfile.setImageURI(imageUri); // Preview
                uploadImageToFirebase();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Image selection failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebase() {
        try {
            if (docId == null || imageUri == null) return;

            StorageReference profileRef = storage.getReference()
                    .child("profile_images/" + docId + ".jpg");

            profileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> profileRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                db.collection("users").document(docId)
                                        .update("profileUrl", uri.toString());
                                Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show();
                            }))
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Toast.makeText(this, "Upload error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupPasswordChange() {
        btnChangePassword.setOnClickListener(v -> {
            try {
                String newPassword = etNewPassword.getText().toString().trim();

                if (newPassword.length() < 6) {
                    etNewPassword.setError("Min 6 characters");
                    return;
                }

                if (docId != null && !docId.isEmpty()) {
                    // Update password in Firestore
                    db.collection("users").document(docId)
                            .update("password", newPassword)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show();
                                etNewPassword.setText("");
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Password change error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class); // or LoginActivity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Logout failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

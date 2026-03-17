package com.example.employee_gamification;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.yourpackage.name.R;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button loginButton;
    private ImageButton btnToggle;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();

        usernameEditText = findViewById(R.id.etUsername);
        passwordEditText = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);
      //  btnToggle = findViewById(R.id.btnToggle);

        loginButton.setOnClickListener(v -> performLogin());


//        btnToggle.setOnClickListener(v -> {
//            if (usernameEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
//                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                btnToggle.setImageResource(R.drawable.ic_eye_off); // Add this icon too
//            } else {
//                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                btnToggle.setImageResource(R.drawable.ic_eye); // Normal eye
//            }
//            passwordEditText.setSelection(passwordEditText.getText().length()); // Cursor at end
//        });


    }

    private void performLogin() {
        String email = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean found = false;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String storedEmail = doc.getString("email");
                        String storedPassword = doc.getString("password");
                        String role = doc.getString("role");

                        if (email.equals(storedEmail) && password.equals(storedPassword)) {
                            found = true;
                            openRoleBasedActivity(role,email);
                            break;
                        }
                    }
                    if (!found) {
                        Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


    }

    private void openRoleBasedActivity(String role , String email) {
        Intent intent;
//        intent = new Intent(this, EMNotification.class);
        switch (role.toUpperCase()) {
            case "EMPLOYEE":
                intent = new Intent(this, EmployeeDashboard.class);
                intent.putExtra("email", email);
                break;
            case "HR":
                intent = new Intent(this, HrActivity.class);
                intent.putExtra("email", email);
                break;
            case "CEO":
                intent = new Intent(this, CeoHomeActivity.class);
        intent.putExtra("email", email);
                break;
            default:
                Toast.makeText(this, "Unknown role", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);


        finish();
    }




}

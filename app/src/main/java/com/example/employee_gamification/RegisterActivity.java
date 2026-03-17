package com.example.employee_gamification;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.yourpackage.name.R;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etFirstName, etLastName, etEmail, etPassword;
    Spinner spinnerRole;
    Button btnRegister;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize views
        etFirstName = findViewById(R.id.fname);
        etLastName = findViewById(R.id.lname);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnRegister = findViewById(R.id.btnRegister);

        // Firestore instance
        db = FirebaseFirestore.getInstance();

        // Spinner setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"EMPLOYEE", "HR","CEO"});
        spinnerRole.setAdapter(adapter);

        // Register button click listener
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullName = firstName + " " + lastName;

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullName);
        userMap.put("email", email);
        userMap.put("password", password);  // In real apps, use hashed passwords
        userMap.put("role", role);
        userMap.put("points", 0); // Default points

        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int maxIndex = 0;

                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        try {
                            int currentIndex = Integer.parseInt(snapshot.getId());
                            if (currentIndex > maxIndex) {
                                maxIndex = currentIndex;
                            }
                        } catch (NumberFormatException ignored) {
                            // skip if ID is not a number
                        }
                    }

                    String nextId = String.format("%02d", maxIndex + 1);

                    db.collection("users")
                            .document(nextId)
                            .set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();

                                // Reset the form after success
                                etFirstName.setText("");
                                etLastName.setText("");
                                etEmail.setText("");
                                etPassword.setText("");
                                spinnerRole.setSelection(0); // Reset the role to the first option (EMPLOYEE)
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error checking IDs: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}

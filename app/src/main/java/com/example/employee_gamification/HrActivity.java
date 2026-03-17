package com.example.employee_gamification;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yourpackage.name.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HrActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton menuButton;
    ActionBarDrawerToggle toggle;

    LinearLayout btnAddTask, btnReports, projectSummaryContainer;
    RecyclerView recentActivityRecycler;
    TextView TV1;
   // String Email;
    private String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        menuButton = findViewById(R.id.menuButton);

        initViews();
        bindQuickActions();
        loadProjectSummary();
        loadRecentActivity();

        TV1 = findViewById(R.id.TV1);


        Email = getIntent().getStringExtra("email");
        if (Email == null) {
            Toast.makeText(this, "Email not found in intent", Toast.LENGTH_SHORT).show();
            return;
        }

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

        // Handle menu item clicks
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle menu item actions here
                int id = item.getItemId();
                if (id == R.id.hr_dashboard) {
                  //  startActivity(new Intent(HrActivity.this, HrActivity.class));
                    return true;
                }
                else if (id == R.id.hr_leaderboard) {
                    startActivity(new Intent(HrActivity.this, HrLeaderboardActivity.class));
                    return true;
                }
                else if (id == R.id.hr_adddproject) {
                    startActivity(new Intent(HrActivity.this, CreateProjectActivity.class));
                    Intent intent = new Intent();
                    intent.putExtra("Email",Email);
                    return true;
                }
                else if (id == R.id.hr_addemployee) {
                    startActivity(new Intent(HrActivity.this, RegisterActivity.class));
                    Intent intent = new Intent();
                    intent.putExtra("Email",Email);
                    return true;
                }
                else if (id == R.id.hr_addtask) {
                    Intent i = new Intent(HrActivity.this, ProjectListActivity.class);
                    i.putExtra("Email", Email);
                    startActivity(i);

                    return true;
                }
                else if (id == R.id.hr_notification) {
                    startActivity(new Intent(HrActivity.this, HRSubmissionActivity.class));
                    return true;
                }


                else if (id == R.id.hr_logout) {
                    startActivity(new Intent(HrActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

    }
        private void initViews() {
            btnAddTask = findViewById(R.id.btnAddTask);
            btnReports = findViewById(R.id.btnReports);
            projectSummaryContainer = findViewById(R.id.projectSummaryContainer);
            recentActivityRecycler = findViewById(R.id.recentActivityRecycler);
        }

    private void bindQuickActions() {
        btnAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(HrActivity.this, CreateProjectActivity.class);
            startActivity(intent);
        });

        btnReports.setOnClickListener(v -> {
            Intent i = new Intent(HrActivity.this, ProjectListActivity.class);
            i.putExtra("Email", Email);
            startActivity(i);

        });
    }

    private void loadProjectSummary() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            final int[] completed = {0};
            final int[] active = {0};

            // Count Total Projects

            db.collection("projects").get().addOnSuccessListener(projectsSnapshot -> {
                int total = projectsSnapshot.size();

                for (DocumentSnapshot doc : projectsSnapshot) {
                    String status = doc.getString("status");
                    if ("completed".equalsIgnoreCase(status)) {
                        completed[0]++;
                    } else if ("active".equalsIgnoreCase(status)) {
                        active[0]++;
                    }
                }

                // Now get assigned employees
                db.collection("users").whereEqualTo("role", "EMPLOYEE")
                        .get().addOnSuccessListener(usersSnapshot -> {
                            int employeeCount = usersSnapshot.size();

                            // Now call the display function
                            showProjectSummary(total, completed[0], active[0], employeeCount);
                        });
            });
        }

        private void showProjectSummary(int total, int completed, int active, int employees) {
            GridLayout summaryGrid = findViewById(R.id.summaryGrid);
            summaryGrid.removeAllViews();

            addSummaryCard("Total Projects", total, R.drawable.ic_projects);
            addSummaryCard("Completed Projects", completed, R.drawable.ic_done);
            addSummaryCard("Ongoing Projects", active, R.drawable.ic_inprogress);
            addSummaryCard("Employees", employees, R.drawable.ic_profile);
        }

        private void addSummaryCard(String label, int value, int iconRes) {
            CardView card = new CardView(this);
            card.setRadius(20f);
            card.setCardElevation(6f);
            card.setUseCompatPadding(true);

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(32, 32, 32, 32);
            layout.setGravity(Gravity.CENTER);

            ImageView icon = new ImageView(this);
            icon.setImageResource(iconRes);
            icon.setLayoutParams(new LinearLayout.LayoutParams(72, 72));

            TextView title = new TextView(this);
            title.setText(label);
            title.setTextSize(16f);
            title.setTextColor(Color.BLACK);
            title.setGravity(Gravity.CENTER);

            TextView count = new TextView(this);
            count.setText(String.valueOf(value));
            count.setTextSize(22f);
            count.setTypeface(null, Typeface.BOLD);
            count.setGravity(Gravity.CENTER);

            layout.addView(icon);
            layout.addView(count);
            layout.addView(title);
            card.addView(layout);

            GridLayout grid = findViewById(R.id.summaryGrid);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            // FIXED: Set a consistent height for all cards (e.g., 180dp)
            int fixedHeight = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics());

            params.width = 0;
            params.height = fixedHeight;  // 👈 FIXED HEIGHT here
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(16, 16, 16, 16);
            card.setLayoutParams(params);

            card.setLayoutParams(params);

            grid.addView(card);
        }

        private void loadRecentActivity() {
            List<RecentActivityModel> activityList = new ArrayList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("projects")
                    .get()
                    .addOnSuccessListener(projects -> {
                        for (DocumentSnapshot project : projects) {
                            String projectId = project.getId();

                            // ➕ Add Project Created activity
                            Timestamp createdTime = project.getTimestamp("createdAt");
                            String createdBy = project.getString("createdBy"); // HR userId

                            if (createdTime != null && createdBy != null) {
                                db.collection("users").document(createdBy)
                                        .get()
                                        .addOnSuccessListener(userDoc -> {
                                            String hrName = userDoc.getString("name");
                                            if (hrName == null) hrName = "HR";
                                            String title = project.getString("projectname");
                                            String desc = "New Project \"" + title + "\" is created";
                                            activityList.add(new RecentActivityModel("project_created", desc, createdTime, createdBy, "HR " + hrName));
                                        });
                            }

                            // ✅ Fetch completed tasks with employee info
                            db.collection("alltask").document(projectId)
                                    .collection("tasks")
                                    .whereNotEqualTo("completedAt", null)
                                    .get()
                                    .addOnSuccessListener(tasks -> {
                                        for (DocumentSnapshot task : tasks) {
                                            Timestamp time = task.getTimestamp("completedAt");
                                            String assignedTo = task.getString("assignedTo");

                                            if (time != null && assignedTo != null) {
                                                db.collection("users").document(assignedTo)
                                                        .get()
                                                        .addOnSuccessListener(userDoc -> {
                                                            String name = userDoc.getString("name");
                                                            String desc = name + " completed a task";
                                                            activityList.add(new RecentActivityModel("task_completed", desc, time, assignedTo,name));
                                                        });
                                            }
                                        }

                                        // ✅ Fetch submissions with employee info
                                        db.collection("submissions").document(projectId)
                                                .collection("tasksubmission")
                                                .get()
                                                .addOnSuccessListener(submissions -> {
                                                    for (DocumentSnapshot submission : submissions) {
                                                        Timestamp time = submission.getTimestamp("submissiontime");
                                                        String submittedBy = submission.getString("submittedBy");

                                                        if (time != null && submittedBy != null) {
                                                            db.collection("users").document(submittedBy)
                                                                    .get()
                                                                    .addOnSuccessListener(userDoc -> {
                                                                        String name = userDoc.getString("name");
                                                                        String desc = name + " submitted a task";
                                                                        activityList.add(new RecentActivityModel("submission", desc, time,submittedBy, name));
                                                                    });
                                                        }
                                                    }

                                                    // ⏳ Delay display to let all inner fetches complete (quick workaround)
                                                    new Handler().postDelayed(() -> {
                                                        Collections.sort(activityList, (a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
                                                        List<RecentActivityModel> topFive = activityList.size() > 5 ? activityList.subList(0, 5) : activityList;

                                                        RecyclerView recycler = findViewById(R.id.recentActivityRecycler);
                                                        recycler.setLayoutManager(new LinearLayoutManager(this));
                                                        recycler.setAdapter(new RecentActivityAdapter(topFive));
                                                    }, 1000); // wait 1 sec for user fetches
                                                });
                                    });
                        }
                    });



    }
}

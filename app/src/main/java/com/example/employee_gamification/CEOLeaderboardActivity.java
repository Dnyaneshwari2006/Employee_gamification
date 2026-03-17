package com.example.employee_gamification;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yourpackage.name.R;

import java.util.ArrayList;
import java.util.List;

public class CEOLeaderboardActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private CEOLeaderboardAdapter adapter;
    private List<CEOLeaderboardModel> leaderboardList;

    // TextViews for top 3 players
    private TextView tvFirstName, tvFirstPoints;
    private TextView tvSecondName, tvSecondPoints;
    private TextView tvThirdName, tvThirdPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceoleaderboard);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find and assign views for top 3 leaderboard
        tvFirstName = findViewById(R.id.tvFirstName);
        tvFirstPoints = findViewById(R.id.tvFirstPoints);
        tvSecondName = findViewById(R.id.tvSecondName);
        tvSecondPoints = findViewById(R.id.tvSecondPoints);
        tvThirdName = findViewById(R.id.tvThirdName);
        tvThirdPoints = findViewById(R.id.tvThirdPoints);

        // RecyclerView setup for other players
        recyclerView = findViewById(R.id.rvLeaderboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        leaderboardList = new ArrayList<>();
        adapter = new CEOLeaderboardAdapter(leaderboardList);
        recyclerView.setAdapter(adapter);

        // Fetch leaderboard data
        fetchLeaderboardData();
    }

    private void fetchLeaderboardData() {
        CollectionReference usersRef = db.collection("users");

        usersRef.orderBy("points", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore Error", error.getMessage());
                            return;
                        }

                        leaderboardList.clear();
                        List<CEOLeaderboardModel> tempList = new ArrayList<>();

                        int rank = 1;
                        int index = 0;

                        for (QueryDocumentSnapshot doc : value) {
                            String name = doc.getString("name");
                            Object pointsObj = doc.get("points");
                            long points = 0;

                            if (pointsObj instanceof Number) {
                                points = ((Number) pointsObj).longValue();
                            } else if (pointsObj instanceof String) {
                                try {
                                    points = Long.parseLong((String) pointsObj);
                                } catch (NumberFormatException e) {
                                    Log.e("Firestore Error", "Invalid number format in Firestore: " + pointsObj);
                                    continue;
                                }
                            }

                            if (name != null) {
                                if (index == 0) { // First place
                                    tvFirstName.setText(name);
                                    tvFirstPoints.setText(String.valueOf(points));
                                } else if (index == 1) { // Second place
                                    tvSecondName.setText(name);
                                    tvSecondPoints.setText(String.valueOf(points));
                                } else if (index == 2) { // Third place
                                    tvThirdName.setText(name);
                                    tvThirdPoints.setText(String.valueOf(points));
                                } else {
                                    // Add remaining players to the list
                                    tempList.add(new CEOLeaderboardModel(name, points, rank));
                                }
                                rank++;
                                index++;
                            }
                        }

                        leaderboardList.addAll(tempList);
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}

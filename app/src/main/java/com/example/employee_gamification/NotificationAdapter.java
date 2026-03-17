package com.example.employee_gamification;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yourpackage.name.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context context;
    private List<NotificationModel> notificationList;
    private FirebaseFirestore db;
    private int expandedPosition = -1; // Track expanded item

    public NotificationAdapter(Context context, List<NotificationModel> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel notification = notificationList.get(position);

        holder.taskTitle.setText(notification.getMessage());
        holder.deadline.setText(notification.getDeadline() != null ? "Deadline: " + notification.getDeadline() : "No Deadline");
        holder.createdAt.setText(notification.getCreatedAt() != null ? "Created: " + notification.getCreatedAt() : "Created: N/A");

        // Maintain rejection reason state when scrolling
        if (holder.etRejectReason.getTag() != null) {
            holder.etRejectReason.setText(holder.etRejectReason.getTag().toString());
        }

        // Expand/collapse rejection layout
        holder.rejectLayout.setVisibility(position == expandedPosition ? View.VISIBLE : View.GONE);
        // Handle Accept Button
        holder.btnAccept.setOnClickListener(v -> {
            Toast.makeText(context, "Task Accepted", Toast.LENGTH_SHORT).show();

            // Disable the button after acceptance
            holder.btnAccept.setEnabled(false);
            holder.btnReject.setEnabled(false);

            // Ensure the notification has a valid ID
            // Ensure the notification has a valid ID
            if (notification.getId() != null && !notification.getId().isEmpty()) {
                // Update Firestore: Set 'seen' field to true
                db.collection("notifications").document(notification.getId())
                        .update("seen", true, "status", "active")   // ✅ Update the 'seen' field
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show();

                            // ✅ Update 'alltask/{projectId}/tasks/{taskId}' status
                            if (notification.getprojectId() != null && notification.gettaskId() != null) {
                                db.collection("alltask")
                                        .document(notification.getprojectId())
                                        .collection("tasks")
                                        .document(notification.gettaskId())
                                        .update("status", "active");
                            }

                            // ✅ Update 'usertaskhistory/{userId}/{projectId}/{taskId}' status
                            if (notification.getuserid() != null && notification.getprojectId() != null && notification.gettaskId() != null) {
                                db.collection("usertaskhistory")
                                        .document(notification.getuserid())
                                        .collection(notification.getprojectId())
                                        .document(notification.gettaskId())
                                        .update("status", "active");
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            holder.btnAccept.setEnabled(true); // Re-enable button if update fails
                        });
            } else {
                Toast.makeText(context, "Error: Notification ID is null", Toast.LENGTH_SHORT).show();
            }

        // Handle Reject Button (Toggle Visibility)
        holder.btnReject.setOnClickListener(v2 -> {
            expandedPosition = (expandedPosition == position) ? -1 : position;
            notifyItemChanged(position);

        });

        // Handle Submit Rejection
        holder.btnSubmitReject.setOnClickListener(v1 -> {
            String reason = holder.etRejectReason.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(context, "Please enter a rejection reason!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Store reason locally to prevent loss on scroll
            holder.etRejectReason.setTag(reason);

            // Ensure notification has an ID before updating Firestore
            if (notification.getId() == null || notification.getId().isEmpty()) {
                Toast.makeText(context, "Error: Notification ID missing!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Firestore Update
            db.collection("notifications").document(notification.getId())
                    .update("rejectedreason", reason, "seen", true, "status", "rejected")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Rejection submitted", Toast.LENGTH_SHORT).show();
                        expandedPosition = -1; // Collapse layout after submission
                        notifyItemChanged(position);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        String userId = notification.getuserid();
        if (userId != null && !userId.isEmpty()) {
            db.collection("users").document(userId).get().addOnSuccessListener(userSnapshot -> {
                Long currentPoints = userSnapshot.getLong("points");
                if (currentPoints != null) {
                    db.collection("users").document(userId).update("points", currentPoints - 20);
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to fetch user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            Toast.makeText(context, "User ID is null or empty", Toast.LENGTH_SHORT).show();
        }

        });
    }


    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle, deadline, createdAt;
        Button btnAccept, btnReject, btnSubmitReject;
        EditText etRejectReason;
        LinearLayout rejectLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.tvTask);
            deadline = itemView.findViewById(R.id.tvDeadline);
            createdAt = itemView.findViewById(R.id.createdAt);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
            btnSubmitReject = itemView.findViewById(R.id.btnSubmitReject);
            etRejectReason = itemView.findViewById(R.id.etRejectReason);
            rejectLayout = itemView.findViewById(R.id.rejectLayout);
        }
    }
}

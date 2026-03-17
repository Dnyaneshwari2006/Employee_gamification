package com.example.employee_gamification;


import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.yourpackage.name.R;

import java.util.List;

public class CeoProjectDetailsAdapter extends RecyclerView.Adapter<CeoProjectDetailsAdapter.TaskViewHolder> {

    private List<CEO_projectdetails> taskList;

    public CeoProjectDetailsAdapter(List<CEO_projectdetails> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ceo_projectdetails_item, parent, false);
        return new TaskViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        CEO_projectdetails task = taskList.get(position);

        // Ensure Views are not Null Before Setting Text
        if (task != null) {
            if (holder.taskId != null)
                holder.taskId.setText("Task ID: " + task.getTaskId());

            if (holder.assignedTo != null)
                holder.assignedTo.setText("Assigned To: " + task.getassignedTo() + task.getname());

            if (holder.status != null)
                holder.status.setText("Status: " + task.getStatus());

            if (holder.taskDetail != null)
                holder.taskDetail.setText("Task: " + (task.getTaskName() != null ? task.getTaskName() : "No Task"));

            if (holder.deadline != null)
                holder.deadline.setText("Deadline: " + (task.getDeadline() != null ? task.getDeadline() : "No Deadline"));
        }

        // 🔹 Define Colors for Different Statuses
        int statusColor;
        switch (task.getStatus().toLowerCase()) {
            case "completed":
                statusColor = Color.parseColor("#4CAF50"); // Green
                break;
            case "rejected":
                statusColor = Color.parseColor("#F44336"); // Red
                break;
            case "active":
                statusColor = Color.parseColor("#FF9800"); // Orange (matches well with Red & Green)
                break;
            default:
                statusColor = Color.parseColor("#607D8B"); // Default Grey
                break;
        }

        // 🔹 Apply Background Color to CardView or List Item
        holder.cardView.setCardBackgroundColor(statusColor);

        // Expand/Collapse Card
        if (task.isExpanded()) {
            holder.detailsLayout.setVisibility(View.VISIBLE);
            holder.arrowIcon.setRotation(180); // Arrow Down

        } else {
            holder.detailsLayout.setVisibility(View.GONE);
            holder.arrowIcon.setRotation(0); // Arrow Up
        }

        // Handle Click for Expand/Collapse
        holder.itemView.setOnClickListener(v -> {
            task.setExpanded(!task.isExpanded());
            notifyItemChanged(position);
        });

    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    // ✅ Correct ViewHolder Class with Proper ID Assignments
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public boolean isExpanded;
        TextView taskId, assignedTo, status, taskDetail, deadline;
        ImageView arrowIcon;
        CardView cardView;
        LinearLayout detailsLayout;

        LinearLayout addSubmissionLayout;
        Button plusIcon;
        TextView addSubmissionText;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            // Make Sure These IDs Exist in `em_task_item.xml`
            cardView = itemView.findViewById(R.id.taskCard);
            taskId = itemView.findViewById(R.id.hr_taskId);
            assignedTo = itemView.findViewById(R.id.hr_assignedTo);
            status = itemView.findViewById(R.id.hr_status);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);
            taskDetail = itemView.findViewById(R.id.hr_taskDetail);
            deadline = itemView.findViewById(R.id.hr_deadline);
            detailsLayout = itemView.findViewById(R.id.detailsLayout);


            // 🔹 Ensure View Elements Exist
            if (taskId == null) Log.e("ViewHolder", "taskId is NULL!");
            if (assignedTo == null) Log.e("ViewHolder", "assignedTo is NULL!");
            if (status == null) Log.e("ViewHolder", "status is NULL!");
            if (taskDetail == null) Log.e("ViewHolder", "taskDetail is NULL!");
            if (deadline == null) Log.e("ViewHolder", "deadline is NULL!");
        }
    }
}

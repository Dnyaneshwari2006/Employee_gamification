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

import com.example.employee_gamification.EMTaskModel;
import com.yourpackage.name.R;

import java.util.List;public class EMTaskHistoryAdapter2 extends RecyclerView.Adapter<EMTaskHistoryAdapter2.TaskViewHolder> {

    private List<EMTaskModel> taskList;

    private OnAddClickListener listener;

    public interface OnAddClickListener {
        void onAddClicked(String projectID , String taskID); // this will be triggered from Activity
    }

    public EMTaskHistoryAdapter2(List<EMTaskModel> taskList , OnAddClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.em_task_item2, parent, false);
        return new TaskViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        EMTaskModel task = taskList.get(position);

        if (task != null) {
//            if (holder.projectname != null)
//                holder.projectname.setText(task.getprojectName());


            if (holder.taskId != null)
                holder.taskId.setText("Task ID: "+ task.getTaskId());

            if (holder.assignedTo != null)
                holder.assignedTo.setText("Assigned To " + task.getUserId());

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
                statusColor = Color.parseColor("#FF9800"); // Orange
                break;
            default:
                statusColor = Color.parseColor("#607D8B"); // Grey
                break;
        }

        holder.cardView.setCardBackgroundColor(statusColor);

        // 🔹 Handle Expand/Collapse Card
        boolean isActive = task.getStatus().equalsIgnoreCase("active");

        if (task.isExpanded()) {
            holder.detailsLayout.setVisibility(View.VISIBLE);
            holder.arrowIcon.setRotation(180); // Arrow Down

            // 🔹 Only show submission layout if task is active
            holder.addSubmissionLayout.setVisibility(isActive ? View.VISIBLE : View.GONE);
        } else {
            holder.detailsLayout.setVisibility(View.GONE);
            holder.arrowIcon.setRotation(0); // Arrow Up
            holder.addSubmissionLayout.setVisibility(View.GONE); // Always hide when collapsed
        }

        // 🔹 Expand/Collapse Toggle
        holder.itemView.setOnClickListener(v -> {
            task.setExpanded(!task.isExpanded());
            notifyItemChanged(position);
        });

        // 🔹 Handle "Add Submission" click
        holder.addSubmissionLayout.setOnClickListener(v -> {
            Log.d("EMTaskAdapter", "Add Submission clicked for Task ID: " + task.getTaskId());
        });

        holder.plusIcon.setOnClickListener(v -> {
            String projectID = task.getprojectID();
            String taskID = task.getTaskId();
            listener.onAddClicked(projectID ,taskID); // Calls showReplyDialog() from Activity
        });
    }


    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    // ✅ Correct ViewHolder Class with Proper ID Assignments
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public boolean isExpanded;
        TextView taskId, assignedTo, status, taskDetail, deadline,projectname;
        TextView userId;
        ImageView arrowIcon;
        CardView cardView;
        LinearLayout detailsLayout;


        LinearLayout addSubmissionLayout;
        Button plusIcon;
        TextView addSubmissionText;


        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            // Make Sure These IDs Exist in `task_item.xml`
            cardView = itemView.findViewById(R.id.taskCard);
            taskId = itemView.findViewById(R.id.hr_taskId);
            assignedTo = itemView.findViewById(R.id.hr_assignedTo);
            status = itemView.findViewById(R.id.hr_status);
            arrowIcon = itemView.findViewById(R.id.arrowIcon);
            taskDetail = itemView.findViewById(R.id.hr_taskDetail);
            deadline = itemView.findViewById(R.id.hr_deadline);
            detailsLayout = itemView.findViewById(R.id.detailsLayout);

            projectname = itemView.findViewById(R.id.hr_projectname);

            addSubmissionLayout = itemView.findViewById(R.id.addSubmissionLayout);
            plusIcon = itemView.findViewById(R.id.btnAddSubmission);
            addSubmissionText = itemView.findViewById(R.id.addSubmissionText);





            // 🔹 Ensure View Elements Exist
            if (taskId == null) Log.e("ViewHolder", "taskId is NULL!");
            if (assignedTo == null) Log.e("ViewHolder", "assignedTo is NULL!");
            if (status == null) Log.e("ViewHolder", "status is NULL!");
            if (taskDetail == null) Log.e("ViewHolder", "taskDetail is NULL!");
            if (deadline == null) Log.e("ViewHolder", "deadline is NULL!");
            if (projectname == null) Log.e("ViewHolder", "projectname is NULL!");

        }
    }
}



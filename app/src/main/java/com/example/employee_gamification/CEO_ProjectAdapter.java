package com.example.employee_gamification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yourpackage.name.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CEO_ProjectAdapter extends RecyclerView.Adapter<CEO_ProjectAdapter.ViewHolder> {

    private List<CEO_Project> projectList;
    private final OnProjectClickListener listener;

    public interface OnProjectClickListener {
        void onProjectClick(CEO_Project project);
    }

    public CEO_ProjectAdapter(List<CEO_Project> list, OnProjectClickListener listener) {
        this.projectList = list;
        this.listener = listener;
    }

    public void setProjectList(List<CEO_Project> list) {
        this.projectList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ceo_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CEO_Project project = projectList.get(position);

        holder.projectName.setText(project.getProjectname());

        if (project.getDeadline() != null) {
            String formattedDeadline = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(project.getDeadline().toDate());
            holder.deadline.setText("Deadline: " + formattedDeadline);
            holder.deadline.setVisibility(View.VISIBLE);
        } else {
            holder.deadline.setText("Deadline: N/A");
            holder.deadline.setVisibility(View.VISIBLE); // Or GONE, if you prefer hiding it
        }

        if (project.getStatus() != null && !project.getStatus().isEmpty()) {
            holder.status.setText("Status: " + project.getStatus());
            holder.status.setVisibility(View.VISIBLE);
        } else {
            holder.status.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onProjectClick(project));
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView projectName, deadline, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            projectName = itemView.findViewById(R.id.projectName);
            deadline = itemView.findViewById(R.id.projectDeadline);
            status = itemView.findViewById(R.id.projectStatus); // Make sure this exists in XML
        }
    }
}

package com.example.employee_gamification;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yourpackage.name.R;

import java.util.List;

    public class EMProjectAdapter extends RecyclerView.Adapter<com.example.employee_gamification.ProjectAdapter.ViewHolder> {
        private List<EMprojectModel> projectList;
        private Context context;

        public EMProjectAdapter(Context context, List<EMprojectModel> projectList) {
            this.context = context;
            this.projectList = projectList;
        }

        @NonNull
        @Override
        public com.example.employee_gamification.ProjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project, parent, false);
            return new com.example.employee_gamification.ProjectAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull com.example.employee_gamification.ProjectAdapter.ViewHolder holder, int position) {
            EMprojectModel project = projectList.get(position);
            holder.projectNameTextView.setText(project.getProjectName());

            // Handle click event
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProjectDetailsActivity.class);
                intent.putExtra("projectId", project.getProjectId());
                intent.putExtra("currentUserId", project.getcurrentUserId());
                //we have to add userid from privious java file.
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return projectList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView projectNameTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                projectNameTextView = itemView.findViewById(R.id.projectName);
            }
        }
    }



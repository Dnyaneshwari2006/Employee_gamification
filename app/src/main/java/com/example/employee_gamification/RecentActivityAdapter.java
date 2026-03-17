package com.example.employee_gamification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yourpackage.name.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ViewHolder> {

    private List<RecentActivityModel> activityList;

    public RecentActivityAdapter(List<RecentActivityModel> activityList) {
        this.activityList = activityList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileIcon;
        TextView employeeName, activityMessage, activityTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileIcon = itemView.findViewById(R.id.profileIcon);
            employeeName = itemView.findViewById(R.id.employeeName);
            activityMessage = itemView.findViewById(R.id.activityMessage);
            activityTime = itemView.findViewById(R.id.activityTime);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentActivityModel model = activityList.get(position);

        String name = model.getEmployeeName() != null ? model.getEmployeeName() : "HR";
        holder.employeeName.setText(name);
        holder.activityMessage.setText(model.getDescription());

        // Format timestamp
        if (model.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            holder.activityTime.setText(sdf.format(model.getTimestamp().toDate()));
        } else {
            holder.activityTime.setText("Unknown time");
        }

        // Set default icon for profile
        holder.profileIcon.setImageResource(R.drawable.ic_person);
    }

    @Override
    public int getItemCount() {
        return activityList != null ? activityList.size() : 0;
    }
}

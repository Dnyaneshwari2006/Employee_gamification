package com.example.employee_gamification;

import android.content.Context;
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

public class RecentTaskAdapter extends RecyclerView.Adapter<RecentTaskAdapter.ViewHolder> {

    private Context context;
    private List<RecentTaskModel> taskList;

    public RecentTaskAdapter(Context context, List<RecentTaskModel> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentTaskModel model = taskList.get(position);
        holder.taskText.setText(model.getTask());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        String deadlineFormatted = sdf.format(model.getDeadline().toDate());
        holder.deadlineText.setText(deadlineFormatted);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskText, deadlineText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskText = itemView.findViewById(R.id.taskName);
            deadlineText = itemView.findViewById(R.id.deadline);
        }
    }
}

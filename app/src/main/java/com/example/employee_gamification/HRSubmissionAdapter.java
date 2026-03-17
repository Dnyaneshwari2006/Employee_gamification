package com.example.employee_gamification;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.employee_gamification.HRSubmissionModel;
import com.yourpackage.name.R;

import java.time.Instant;
import java.util.List;

public class HRSubmissionAdapter extends RecyclerView.Adapter<HRSubmissionAdapter.ViewHolder> {
    private Context context;
    private List<HRSubmissionModel> submissionList;

    public HRSubmissionAdapter(Context context, List<HRSubmissionModel> submissionList) {
        this.context = context;
        this.submissionList = submissionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_submission, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HRSubmissionModel submission = submissionList.get(position);
        holder.tvName.setText(submission.getSubmittedBy());
        holder.tvTask.setText("Task: " + submission.getTaskName());
        holder.tvReply.setText("Reply: " + submission.getReply());
        holder.tvSubmissionTime.setText("Submission Time: " + submission.getSubmissionTime());

        String attachmentUrl = submission.getAttachmentUrl();

        if (attachmentUrl != null && !attachmentUrl.isEmpty()) {
            holder.ivAttachment.setVisibility(View.VISIBLE);

            // Set a placeholder image if you have one
            holder.ivAttachment.setImageResource(R.drawable.ic_file_placeholder);

            // Set click listener on the image
            holder.ivAttachment.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(attachmentUrl), "*/*"); // safer for general files
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    context.startActivity(Intent.createChooser(intent, "Open with"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            holder.ivAttachment.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return submissionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvTask, tvReply, tvSubmissionTime;
        ImageView ivAttachment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvTask = itemView.findViewById(R.id.tvTask);
            tvReply = itemView.findViewById(R.id.tvReply);
            tvSubmissionTime = itemView.findViewById(R.id.tvSubmissionTime);
            ivAttachment = itemView.findViewById(R.id.ivAttachment);
        }
    }
}



package com.example.employee_gamification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.yourpackage.name.R;

import java.util.List;

public class CEO_EmployeeAdapter extends RecyclerView.Adapter<CEO_EmployeeAdapter.ViewHolder> {
    private List<CEO_Employee> employeeList;

    public CEO_EmployeeAdapter(List<CEO_Employee> list) {
        this.employeeList = list;
    }

    public void setEmployeeList(List<CEO_Employee> list) {
        this.employeeList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ceo_employee, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CEO_Employee emp = employeeList.get(position);
//        holder.name.setText(emp.getName());
//        holder.points.setText("Points: " + emp.getPoints());
        holder.employeeName.setText(emp.getName());
        holder.employeePoints.setText(emp.getPoints() + " pts");

        Glide.with(holder.itemView.getContext())
                .load(emp.getImageUrl())
                .placeholder(R.drawable.ic_profile)
                .circleCrop()
                .into(holder.profileImage);

        // Show crown for the top employee
        if (position == 0) {
            holder.crownImage.setVisibility(View.VISIBLE);
        } else {
            holder.crownImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView employeeName, employeePoints;
        ImageView profileImage, crownImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.nameTextView);
            employeePoints = itemView.findViewById(R.id.pointsTextView);
            profileImage = itemView.findViewById(R.id.imageView);
            crownImage = itemView.findViewById(R.id.crownImage);
        }
    }
}

package com.example.employee_gamification;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.yourpackage.name.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EmployeeCalendarAdapter extends RecyclerView.Adapter<EmployeeCalendarAdapter.CalendarViewHolder> {
    private final List<Date> dateList;
    private final Context context;
    private final OnDateClickListener listener;
    private final SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    private final SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEE");
    private final int todayPosition;
    private int selectedPosition;

    public interface OnDateClickListener {
        void onDateClick(Date date);
    }

    public EmployeeCalendarAdapter(Context context, List<Date> dateList, OnDateClickListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.listener = listener;
        this.todayPosition = getTodayPosition();
        this.selectedPosition = todayPosition;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_date, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        Date date = dateList.get(position);
        holder.tvDay.setText(dayFormat.format(date));
        holder.tvDayOfWeek.setText(dayOfWeekFormat.format(date));

        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.bg_date_selected);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);
            listener.onDateClick(date);
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvDayOfWeek;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tvDay);
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
        }
    }

    private int getTodayPosition() {
        Calendar today = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        for (int i = 0; i < dateList.size(); i++) {
            if (sdf.format(dateList.get(i)).equals(sdf.format(today.getTime()))) {
                return i;
            }
        }
        return 0;
    }
}
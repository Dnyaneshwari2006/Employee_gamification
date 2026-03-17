package com.example.employee_gamification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yourpackage.name.R;

import java.util.List;

public class CEOLeaderboardAdapter extends RecyclerView.Adapter<CEOLeaderboardAdapter.ViewHolder> {

    private List<com.example.employee_gamification.CEOLeaderboardModel> leaderboardList;

    public CEOLeaderboardAdapter(List<com.example.employee_gamification.CEOLeaderboardModel> leaderboardList) {
        this.leaderboardList = leaderboardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        com.example.employee_gamification.CEOLeaderboardModel model = leaderboardList.get(position);

        // Set Rank (starting from 4)
        holder.tvPlayerRank.setText(String.valueOf(position + 4) + ".");

        // Set Player Name
        holder.tvPlayerName.setText(model.getPlayerName());

        // Set Score with "+" sign
        holder.tvPlayerScore.setText("+" + model.getPoints());
    }

    @Override
    public int getItemCount() {
        return leaderboardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlayerRank, tvPlayerName, tvPlayerScore;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPlayerRank = itemView.findViewById(R.id.tvPlayerrank); // Fix: Added missing ID
            tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
            tvPlayerScore = itemView.findViewById(R.id.tvPlayerScore);
        }
    }
}

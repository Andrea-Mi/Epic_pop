package com.example.epic_pop;

import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StreakCalendarAdapter extends RecyclerView.Adapter<StreakCalendarAdapter.DayViewHolder> {

    public interface OnDayClickListener { void onDayClick(StreakDay day); }

    private List<StreakDay> days;
    private OnDayClickListener listener;

    public StreakCalendarAdapter(List<StreakDay> days, OnDayClickListener listener) {
        this.days = days;
        this.listener = listener;
    }

    public void updateDays(List<StreakDay> newDays) {
        this.days = newDays;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_day_streak, parent, false);
        return new DayViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        StreakDay day = days.get(position);
        if (day.dayOfMonth <= 0 || !day.inCurrentMonth) {
            holder.tvNumber.setText("");
            holder.star.setVisibility(View.GONE);
            holder.itemView.setAlpha(0f);
            holder.itemView.setOnClickListener(null);
            return;
        }
        holder.itemView.setAlpha(1f);
        holder.tvNumber.setText(String.valueOf(day.dayOfMonth));
        if (day.type == StreakDay.Type.NONE) {
            holder.star.setVisibility(View.GONE);
        } else {
            holder.star.setVisibility(View.VISIBLE);
            int color = day.type == StreakDay.Type.ON_TIME ? R.color.star_on_time : R.color.star_catch_up;
            holder.star.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), color), PorterDuff.Mode.SRC_IN);
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && day.dayOfMonth > 0) listener.onDayClick(day);
        });
    }

    @Override
    public int getItemCount() { return days.size(); }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumber; ImageView star;
        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumber = itemView.findViewById(R.id.tv_day_number);
            star = itemView.findViewById(R.id.iv_star);
        }
    }
}


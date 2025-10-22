package com.example.epic_pop;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.epic_pop.models.Logro;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementViewHolder> {

    private List<Logro> achievements;
    private SimpleDateFormat dateFormat;

    public AchievementsAdapter(List<Logro> achievements) {
        this.achievements = achievements;
        this.dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_achievement, parent, false);
        return new AchievementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        Logro achievement = achievements.get(position);

        holder.tvTitle.setText(achievement.getTitle());
        holder.tvDescription.setText(achievement.getDescription());

        // Formatear fecha
        try {
            Date date = new Date(Long.parseLong(achievement.getDateEarned()));
            holder.tvDate.setText(dateFormat.format(date));
        } catch (Exception e) {
            holder.tvDate.setText("-");
        }

        setIconForAchievement(holder.ivIcon, achievement.getAchievementType());

        // Formato de puntos usando recursos
        if (achievement.getPointsAwarded() > 0) {
            holder.tvPoints.setText(holder.itemView.getContext().getString(R.string.achievement_points_format, achievement.getPointsAwarded()));
        } else {
            holder.tvPoints.setText("");
        }

        // Resaltar logros de hito o recién añadidos (primeros elementos)
        boolean isMilestone = achievement.getAchievementType() != null && achievement.getAchievementType().startsWith("MILESTONE_");
        if (isMilestone || position == 0) {
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(24f);
            int start = ContextCompat.getColor(holder.itemView.getContext(), R.color.primary);
            bg.setColor(start);
            bg.setAlpha(30);
            holder.itemView.setBackground(bg);
        } else {
            holder.itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public void updateAchievements(List<Logro> newAchievements) {
        this.achievements = newAchievements;
        notifyDataSetChanged();
    }

    private void setIconForAchievement(ImageView imageView, String achievementType) {
        switch (achievementType) {
            case "PRIMERA_RACHA":
            case "RACHA_SEMANAL":
            case "RACHA_MENSUAL":
                imageView.setImageResource(R.drawable.ic_calendar);
                break;
            case "CIEN_ESTRELLAS":
                imageView.setImageResource(R.drawable.ic_star);
                break;
            case "RETO_DIFICIL":
                imageView.setImageResource(R.drawable.ic_sports);
                break;
            case "DEPORTISTA":
                imageView.setImageResource(R.drawable.ic_sports);
                break;
            case "ESTUDIANTE":
                imageView.setImageResource(R.drawable.ic_book);
                break;
            case "ECOLOGISTA":
                imageView.setImageResource(R.drawable.ic_eco);
                break;
            case "SALUDABLE":
                imageView.setImageResource(R.drawable.ic_health);
                break;
            case "CREATIVO":
                imageView.setImageResource(R.drawable.ic_creative);
                break;
            default:
                imageView.setImageResource(R.drawable.ic_star);
                break;
        }
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvDescription, tvDate, tvPoints;

        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_achievement_icon);
            tvTitle = itemView.findViewById(R.id.tv_achievement_title);
            tvDescription = itemView.findViewById(R.id.tv_achievement_description);
            tvDate = itemView.findViewById(R.id.tv_achievement_date);
            tvPoints = itemView.findViewById(R.id.tv_achievement_points);
        }
    }
}

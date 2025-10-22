package com.example.epic_pop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.epic_pop.models.Reto;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChallengeListAdapter extends RecyclerView.Adapter<ChallengeListAdapter.ChallengeViewHolder> {

    private List<Reto> challenges;
    private Context context;
    private SimpleDateFormat dateFormat;

    public ChallengeListAdapter(List<Reto> challenges, Context context) {
        this.challenges = challenges;
        this.context = context;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_challenge_list, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        Reto reto = challenges.get(position);

        holder.tvTitle.setText(reto.getTitle());
        holder.tvDescription.setText(reto.getDescription());
        holder.tvCategory.setText(reto.getCategory());
        holder.tvDifficulty.setText(reto.getDifficulty());
        holder.tvStatus.setText(reto.getStatus());
        holder.tvProgress.setText(reto.getProgress() + "%");
        holder.progressBar.setProgress(reto.getProgress());

        // Configurar fecha de creación
        try {
            long timestamp = Long.parseLong(reto.getCreatedDate());
            Date date = new Date(timestamp);
            holder.tvCreatedDate.setText("Creado: " + dateFormat.format(date));
        } catch (Exception e) {
            holder.tvCreatedDate.setText("Fecha: No disponible");
        }

        // Configurar icono según categoría
        setIconForCategory(holder.ivIcon, reto.getCategory());

        // Configurar color según categoría
        setCategoryColor(holder.tvCategory, reto.getCategory());
        setProgressColor(holder.progressBar, reto.getCategory());

        // Configurar color del estado
        setStatusColor(holder.tvStatus, reto.getStatus());

        // Click listener para editar/ver detalles del reto
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditChallengeActivity.class);
            intent.putExtra("reto_id", reto.getId());
            context.startActivity(intent);
        });

        // Long click para acciones adicionales
        holder.itemView.setOnLongClickListener(v -> {
            // TODO: Mostrar menú contextual con opciones (completar, eliminar, etc.)
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    public void updateChallenges(List<Reto> newChallenges) {
        if (newChallenges != null) {
            this.challenges = newChallenges;
            notifyDataSetChanged();
            android.util.Log.d("EpicPop", "Lista de retos actualizada en ChallengeListAdapter: " + newChallenges.size());
        }
    }

    private void setIconForCategory(ImageView imageView, String category) {
        switch (category) {
            case "Deportes":
                imageView.setImageResource(R.drawable.ic_sports);
                break;
            case "Estudio":
                imageView.setImageResource(R.drawable.ic_book);
                break;
            case "Ecología":
                imageView.setImageResource(R.drawable.ic_eco);
                break;
            case "Salud":
                imageView.setImageResource(R.drawable.ic_health);
                break;
            case "Creatividad":
                imageView.setImageResource(R.drawable.ic_creative);
                break;
            default:
                imageView.setImageResource(R.drawable.ic_default);
                break;
        }
    }

    private void setCategoryColor(TextView textView, String category) {
        int color;
        switch (category) {
            case "Deportes":
                color = Color.parseColor("#6200EE");
                break;
            case "Estudio":
                color = Color.parseColor("#FF9800");
                break;
            case "Ecología":
                color = Color.parseColor("#4CAF50");
                break;
            case "Salud":
                color = Color.parseColor("#F44336");
                break;
            case "Creatividad":
                color = Color.parseColor("#E91E63");
                break;
            default:
                color = Color.parseColor("#6200EE");
                break;
        }
        textView.setTextColor(color);
    }

    private void setProgressColor(ProgressBar progressBar, String category) {
        int color;
        switch (category) {
            case "Deportes":
                color = Color.parseColor("#6200EE");
                break;
            case "Estudio":
                color = Color.parseColor("#FF9800");
                break;
            case "Ecología":
                color = Color.parseColor("#4CAF50");
                break;
            case "Salud":
                color = Color.parseColor("#F44336");
                break;
            case "Creatividad":
                color = Color.parseColor("#E91E63");
                break;
            default:
                color = Color.parseColor("#6200EE");
                break;
        }
        progressBar.getProgressDrawable().setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void setStatusColor(TextView textView, String status) {
        int color;
        switch (status) {
            case "Pendiente":
                color = Color.parseColor("#FF9800"); // Naranja
                break;
            case "En Progreso":
                color = Color.parseColor("#03DAC6"); // Cian
                break;
            case "Completado":
                color = Color.parseColor("#4CAF50"); // Verde
                break;
            default:
                color = Color.parseColor("#666666"); // Gris
                break;
        }
        textView.setTextColor(color);
    }

    static class ChallengeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvDescription, tvCategory, tvDifficulty, tvStatus, tvProgress, tvCreatedDate;
        ProgressBar progressBar;

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_challenge_icon);
            tvTitle = itemView.findViewById(R.id.tv_challenge_title);
            tvDescription = itemView.findViewById(R.id.tv_challenge_description);
            tvCategory = itemView.findViewById(R.id.tv_challenge_category);
            tvDifficulty = itemView.findViewById(R.id.tv_challenge_difficulty);
            tvStatus = itemView.findViewById(R.id.tv_challenge_status);
            tvProgress = itemView.findViewById(R.id.tv_challenge_progress);
            tvCreatedDate = itemView.findViewById(R.id.tv_challenge_created_date);
            progressBar = itemView.findViewById(R.id.progress_challenge);
        }
    }
}

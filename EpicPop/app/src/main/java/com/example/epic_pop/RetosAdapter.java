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
import java.util.List;

public class RetosAdapter extends RecyclerView.Adapter<RetosAdapter.RetoViewHolder> {

    private List<Reto> retos;
    private Context context;

    public RetosAdapter(List<Reto> retos, Context context) {
        this.retos = retos;
        this.context = context;
    }

    @NonNull
    @Override
    public RetoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reto, parent, false);
        return new RetoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RetoViewHolder holder, int position) {
        Reto reto = retos.get(position);

        holder.tvTitle.setText(reto.getTitle());
        holder.tvProgress.setText(reto.getProgress() + "%");
        holder.progressBar.setProgress(reto.getProgress());

        // Configurar icono según categoría
        setIconForCategory(holder.ivIcon, reto.getCategory());

        // Configurar color según categoría
        setColorForCategory(holder.progressBar, reto.getCategory());

        // Click listener para editar reto
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditChallengeActivity.class);
            intent.putExtra("reto_id", reto.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return retos.size();
    }

    public void updateRetos(List<Reto> newRetos) {
        // Verificar si realmente hay cambios antes de actualizar
        if (newRetos != null && !newRetos.equals(this.retos)) {
            this.retos = newRetos;
            notifyDataSetChanged();
            android.util.Log.d("EpicPop", "Lista de retos actualizada: " + newRetos.size() + " retos");
        }
    }

    /**
     * Método para agregar un nuevo reto a la lista existente
     */
    public void addReto(Reto newReto) {
        if (newReto != null) {
            this.retos.add(0, newReto); // Agregar al inicio de la lista
            notifyItemInserted(0);
            android.util.Log.d("EpicPop", "Nuevo reto agregado: " + newReto.getTitle());
        }
    }

    /**
     * Método para actualizar un reto específico en la lista
     */
    public void updateReto(Reto updatedReto) {
        if (updatedReto != null) {
            for (int i = 0; i < retos.size(); i++) {
                if (retos.get(i).getId() == updatedReto.getId()) {
                    retos.set(i, updatedReto);
                    notifyItemChanged(i);
                    android.util.Log.d("EpicPop", "Reto actualizado: " + updatedReto.getTitle());
                    break;
                }
            }
        }
    }

    /**
     * Método para eliminar un reto de la lista
     */
    public void removeReto(int retoId) {
        for (int i = 0; i < retos.size(); i++) {
            if (retos.get(i).getId() == retoId) {
                retos.remove(i);
                notifyItemRemoved(i);
                android.util.Log.d("EpicPop", "Reto eliminado con ID: " + retoId);
                break;
            }
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

    private void setColorForCategory(ProgressBar progressBar, String category) {
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

    static class RetoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvProgress;
        ProgressBar progressBar;

        public RetoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_reto_icon);
            tvTitle = itemView.findViewById(R.id.tv_reto_title);
            tvProgress = itemView.findViewById(R.id.tv_reto_progress);
            progressBar = itemView.findViewById(R.id.progress_reto);
        }
    }
}

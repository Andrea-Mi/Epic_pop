package com.example.epic_pop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epic_pop.models.FAQItem;

import java.util.List;

public class FAQAdapter extends RecyclerView.Adapter<FAQAdapter.FAQViewHolder> {
    private final List<FAQItem> faqItems;

    public FAQAdapter(List<FAQItem> faqItems) {
        this.faqItems = faqItems;
    }

    @NonNull
    @Override
    public FAQViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq, parent, false);
        return new FAQViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQViewHolder holder, int position) {
        FAQItem item = faqItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return faqItems.size();
    }

    static class FAQViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvQuestion;
        private final TextView tvAnswer;
        private final ImageView ivExpand;
        private final View layoutAnswer;

        public FAQViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestion = itemView.findViewById(R.id.tv_question);
            tvAnswer = itemView.findViewById(R.id.tv_answer);
            ivExpand = itemView.findViewById(R.id.iv_expand);
            layoutAnswer = itemView.findViewById(R.id.layout_answer);
        }

        public void bind(FAQItem item) {
            tvQuestion.setText(item.getQuestion());
            tvAnswer.setText(item.getAnswer());

            // Configurar estado inicial
            if (item.isExpanded()) {
                layoutAnswer.setVisibility(View.VISIBLE);
                ivExpand.setRotation(180f);
            } else {
                layoutAnswer.setVisibility(View.GONE);
                ivExpand.setRotation(0f);
            }

            // Click listener para expandir/contraer
            itemView.setOnClickListener(v -> {
                item.setExpanded(!item.isExpanded());

                if (item.isExpanded()) {
                    // Expandir
                    layoutAnswer.setVisibility(View.VISIBLE);
                    ivExpand.animate()
                            .rotation(180f)
                            .setDuration(200)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                } else {
                    // Contraer
                    layoutAnswer.setVisibility(View.GONE);
                    ivExpand.animate()
                            .rotation(0f)
                            .setDuration(200)
                            .setInterpolator(new AccelerateDecelerateInterpolator())
                            .start();
                }
            });
        }
    }
}

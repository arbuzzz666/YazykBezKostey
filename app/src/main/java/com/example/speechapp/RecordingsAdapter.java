package com.example.speechapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.ViewHolder> {

    private List<RecordingItem> recordings;
    private OnPlayClickListener playListener;
    private OnDeleteClickListener deleteListener;

    public interface OnPlayClickListener {
        void onPlayClick(String filePath);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public RecordingsAdapter(List<RecordingItem> recordings,
                             OnPlayClickListener playListener,
                             OnDeleteClickListener deleteListener) {
        this.recordings = recordings;
        this.playListener = playListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recording, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecordingItem item = recordings.get(position);

        // Форматируем дату
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        String dateStr = sdf.format(new Date(item.getTimestamp()));

        holder.tvDate.setText(dateStr);
        holder.tvRecognizedText.setText(item.getRecognizedText());
        holder.tvExerciseName.setText(item.getExerciseName());

        holder.btnPlay.setOnClickListener(v -> {
            if (playListener != null) {
                playListener.onPlayClick(item.getFilePath());
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvRecognizedText, tvExerciseName;
        Button btnPlay, btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_recording_date);
            tvRecognizedText = itemView.findViewById(R.id.tv_recording_recognized_text);
            tvExerciseName = itemView.findViewById(R.id.tv_recording_exercise_name);
            btnPlay = itemView.findViewById(R.id.btn_play_recording);
            btnDelete = itemView.findViewById(R.id.btn_delete_recording);
        }
    }
}
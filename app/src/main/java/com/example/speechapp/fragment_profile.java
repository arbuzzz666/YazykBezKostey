package com.example.speechapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class fragment_profile extends Fragment {

    private TextView tvLessonsCompleted, tvTasksDone, tvWordsNamed, tvRecordingsDone;
    private Button btnResetStats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        tvLessonsCompleted = rootView.findViewById(R.id.tv_lessons_completed);
        tvTasksDone = rootView.findViewById(R.id.tv_tasks_done);
        tvWordsNamed = rootView.findViewById(R.id.tv_words_named);
        tvRecordingsDone = rootView.findViewById(R.id.tv_recordings_done);
        btnResetStats = rootView.findViewById(R.id.btn_reset_stats);

        btnResetStats.setOnClickListener(v -> showResetConfirmationDialog());

        loadStatistics();
        return rootView;
    }

    private void showResetConfirmationDialog() {
        if (getActivity() == null) return;

        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.reset_dialog_title)
                .setMessage(R.string.reset_dialog_message)
                .setPositiveButton(R.string.reset_confirm, (dialog, which) -> {
                    AppDatabase db = ((MainActivity) requireActivity()).getAppDatabase();
                    db.resetAllStatistics();
                    loadStatistics();
                    Toast.makeText(getActivity(), R.string.reset_success, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.reset_cancel, (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void loadStatistics() {
        if (getActivity() == null) return;
        AppDatabase db = ((MainActivity) getActivity()).getAppDatabase();

        tvLessonsCompleted.setText(String.valueOf(db.getLessonsCompleted()));
        tvTasksDone.setText(String.valueOf(db.getTasksDone()));
        tvWordsNamed.setText(String.valueOf(db.getWordsNamed()));
        tvRecordingsDone.setText(String.valueOf(db.getLessonsCompleted()));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
    }
}
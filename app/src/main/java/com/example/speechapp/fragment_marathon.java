package com.example.speechapp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class fragment_marathon extends Fragment {

    private TextView tvMarathonTask, tvMarathonTimer, tvWordCounter, tvMarathonStatus;
    private Button btnStartMarathon, btnWordSaid, btnStopMarathon, btnBackMarathon;

    private List<String> marathonTasks;          // Все задания
    private List<String> shuffledTasks;           // Перемешанные задания
    private int currentTaskIndex = 0;
    private int wordCount = 0;
    private boolean isTimerRunning = false;
    private CountDownTimer countDownTimer;

    private void showHintIfNeeded(String prefKey, int titleRes, int messageRes) {
        android.content.SharedPreferences prefs = requireActivity().getSharedPreferences("hints_prefs", android.content.Context.MODE_PRIVATE);
        if (!prefs.getBoolean(prefKey, false)) {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(titleRes)
                    .setMessage(messageRes)
                    .setPositiveButton(R.string.hint_ok, (dialog, which) -> {
                        prefs.edit().putBoolean(prefKey, true).apply();
                        dialog.dismiss();
                    })
                    .show();
        }
    }
    private long timeLeftInMillis = 60000; // 1 минута

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_marathon, container, false);
        showHintIfNeeded("hint_marathon_shown", R.string.hint_marathon_title, R.string.hint_marathon);

        tvMarathonTask = rootView.findViewById(R.id.tv_marathon_task);
        tvMarathonTimer = rootView.findViewById(R.id.tv_marathon_timer);
        tvWordCounter = rootView.findViewById(R.id.tv_word_counter);
        tvMarathonStatus = rootView.findViewById(R.id.tv_marathon_status);
        btnStartMarathon = rootView.findViewById(R.id.btn_start_marathon);
        btnWordSaid = rootView.findViewById(R.id.btn_word_said);
        btnStopMarathon = rootView.findViewById(R.id.btn_stop_marathon);
        btnBackMarathon = rootView.findViewById(R.id.btn_back_marathon);

        // Загружаем задания и сразу перемешиваем
        marathonTasks = SpeechDatabase.getLexicalMarathonTasks();
        shuffleTasks();
        showCurrentTask();

        // Кнопка Начать
        btnStartMarathon.setOnClickListener(v -> startMarathon());

        // Кнопка Назвал слово
        btnWordSaid.setOnClickListener(v -> {
            wordCount++;
            tvWordCounter.setText(getString(R.string.marathon_words_counter, wordCount));
        });

        // Кнопка Стоп
        btnStopMarathon.setOnClickListener(v -> stopMarathon());

        // Кнопка Назад
        btnBackMarathon.setOnClickListener(v -> {
            if (isTimerRunning) {
                countDownTimer.cancel();
            }
            getParentFragmentManager().popBackStack();
        });

        return rootView;
    }

    /**
     * Перемешивает задания в случайном порядке.
     */
    private void shuffleTasks() {
        shuffledTasks = new ArrayList<>(marathonTasks);
        Collections.shuffle(shuffledTasks);
    }

    private void showCurrentTask() {
        if (currentTaskIndex < shuffledTasks.size()) {
            tvMarathonTask.setText(getString(R.string.marathon_task_format, shuffledTasks.get(currentTaskIndex)));
            tvMarathonStatus.setText(R.string.marathon_status_start);
        } else {
            tvMarathonTask.setText(R.string.marathon_finished_short);
            tvMarathonStatus.setText(R.string.marathon_all_done_status);
            btnStartMarathon.setEnabled(false);
        }
    }

    private void startMarathon() {
        if (isTimerRunning) return;

        isTimerRunning = true;
        wordCount = 0;
        timeLeftInMillis = 60000;
        tvWordCounter.setText(getString(R.string.marathon_words_counter, 0));
        tvMarathonTimer.setText("01:00");

        btnStartMarathon.setEnabled(false);
        btnWordSaid.setEnabled(true);
        btnWordSaid.setAlpha(1.0f);
        btnStopMarathon.setEnabled(true);
        btnStopMarathon.setAlpha(1.0f);
        tvMarathonStatus.setText(R.string.marathon_status_speak);

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int minutes = (int) (timeLeftInMillis / 1000) / 60;
                int seconds = (int) (timeLeftInMillis / 1000) % 60;
                tvMarathonTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                stopMarathon();
                tvMarathonStatus.setText(R.string.marathon_status_time_up);
            }
        }.start();
    }

    private void stopMarathon() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;

        btnStartMarathon.setEnabled(true);
        btnWordSaid.setEnabled(false);
        btnWordSaid.setAlpha(0.5f);
        btnStopMarathon.setEnabled(false);
        btnStopMarathon.setAlpha(0.5f);

        tvMarathonTimer.setText("01:00");
        tvMarathonStatus.setText(getString(R.string.marathon_task_done_format, wordCount));

        // Сохраняем результат
        if (getActivity() != null) {
            ((MainActivity) getActivity()).getAppDatabase().addWordsNamed(wordCount);
            ((MainActivity) getActivity()).getAppDatabase().incrementTasksDone();
        }

        // Переход к следующему заданию
        currentTaskIndex++;
        if (currentTaskIndex < shuffledTasks.size()) {
            Toast.makeText(getActivity(), R.string.marathon_next_loaded, Toast.LENGTH_SHORT).show();
            showCurrentTask();
        } else {
            tvMarathonTask.setText(R.string.marathon_all_completed);
            btnStartMarathon.setEnabled(false);
            if (getActivity() != null) {
                ((MainActivity) getActivity()).getAppDatabase().incrementLessonsCompleted();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
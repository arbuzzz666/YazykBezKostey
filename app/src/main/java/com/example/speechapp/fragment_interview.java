package com.example.speechapp;

import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class fragment_interview extends Fragment {

    private TextView tvInterviewQuestion, tvQuestionCounter;
    private Button btnNextQuestion, btnFinishInterview, btnRestartInterview, btnBackInterview;

    private List<String> interviewQuestions;
    private List<String> selectedQuestions;
    private int currentQuestionIndex = 0;
    private boolean isStarted = false;
    private boolean isFinished = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_interview, container, false);

        tvInterviewQuestion = rootView.findViewById(R.id.tv_interview_question);
        tvQuestionCounter = rootView.findViewById(R.id.tv_question_counter);
        btnNextQuestion = rootView.findViewById(R.id.btn_next_question);
        btnFinishInterview = rootView.findViewById(R.id.btn_finish_interview);
        btnRestartInterview = rootView.findViewById(R.id.btn_restart_interview);
        btnBackInterview = rootView.findViewById(R.id.btn_back_interview);

        interviewQuestions = SpeechDatabase.getInterviewQuestions();

        btnNextQuestion.setOnClickListener(v -> {
            if (!isStarted) {
                startInterview();
            } else if (!isFinished) {
                nextQuestion();
            }
        });

        btnFinishInterview.setOnClickListener(v -> finishInterview());

        btnRestartInterview.setOnClickListener(v -> restartInterview());

        btnBackInterview.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        // Подсказка при первом входе
        showHintIfNeeded("hint_interview_shown", R.string.hint_interview_title, R.string.hint_interview);

        return rootView;
    }

    private void showHintIfNeeded(String prefKey, int titleRes, int messageRes) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("hints_prefs", android.content.Context.MODE_PRIVATE);
        if (!prefs.getBoolean(prefKey, false)) {
            new MaterialAlertDialogBuilder(requireActivity())
                    .setTitle(titleRes)
                    .setMessage(messageRes)
                    .setPositiveButton(R.string.hint_ok, (dialog, which) -> {
                        prefs.edit().putBoolean(prefKey, true).apply();
                        dialog.dismiss();
                    })
                    .show();
        }
    }

    private void startInterview() {
        if (interviewQuestions.size() < 5) {
            Toast.makeText(getActivity(), R.string.interview_not_enough, Toast.LENGTH_SHORT).show();
            return;
        }

        Collections.shuffle(interviewQuestions);
        selectedQuestions = new ArrayList<>(interviewQuestions.subList(0, 5));
        currentQuestionIndex = 0;
        isStarted = true;
        isFinished = false;

        // Показываем кнопки
        btnNextQuestion.setText(R.string.interview_next_button);
        btnNextQuestion.setEnabled(true);
        btnFinishInterview.setVisibility(View.VISIBLE);
        btnRestartInterview.setVisibility(View.GONE);

        showCurrentQuestion();
    }

    private void showCurrentQuestion() {
        if (currentQuestionIndex < selectedQuestions.size()) {
            tvInterviewQuestion.setText(selectedQuestions.get(currentQuestionIndex));
            tvQuestionCounter.setText(getString(R.string.interview_question_counter, currentQuestionIndex + 1));
        }
    }

    private void nextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < selectedQuestions.size()) {
            showCurrentQuestion();
        } else {
            // Все 5 вопросов пройдены
            finishInterview();
        }
    }

    private void finishInterview() {
        isFinished = true;

        tvInterviewQuestion.setText(R.string.interview_finished_text);
        tvQuestionCounter.setText(R.string.interview_finished_label);

        // Скрываем кнопки управления
        btnNextQuestion.setEnabled(false);
        btnNextQuestion.setVisibility(View.GONE);
        btnFinishInterview.setVisibility(View.GONE);

        // Показываем кнопку "Ещё раз"
        btnRestartInterview.setVisibility(View.VISIBLE);

        Toast.makeText(getActivity(), R.string.interview_all_done, Toast.LENGTH_SHORT).show();

        if (getActivity() != null) {
            ((MainActivity) getActivity()).getAppDatabase().incrementTasksDone();
            ((MainActivity) getActivity()).getAppDatabase().incrementLessonsCompleted();
        }
    }

    private void restartInterview() {
        // Сбрасываем всё и начинаем заново
        isStarted = false;
        isFinished = false;
        currentQuestionIndex = 0;

        // Возвращаем начальный вид
        tvInterviewQuestion.setText("Нажмите «Начать» для получения вопросов");
        tvQuestionCounter.setText("Вопрос 0/5");

        btnNextQuestion.setText("🎤 Начать");
        btnNextQuestion.setEnabled(true);
        btnNextQuestion.setVisibility(View.VISIBLE);
        btnFinishInterview.setVisibility(View.GONE);
        btnRestartInterview.setVisibility(View.GONE);

        Toast.makeText(getActivity(), "Можете пройти собеседование снова", Toast.LENGTH_SHORT).show();
    }
}
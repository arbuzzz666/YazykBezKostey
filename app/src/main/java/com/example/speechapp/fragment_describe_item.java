package com.example.speechapp;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class fragment_describe_item extends Fragment {

    private static final int REQUEST_RECORD_AUDIO = 600;

    // UI
    private TextView tvItemToDescribe, tvTimer, tvItemCounter;
    private TextView tvDescribeStatus;
    private TextView tvResultItems, tvResultWords;
    private Button btnStartDescribe, btnNextItem, btnStopDescribe, btnBackDescribe;
    private LinearLayout layoutResult;

    // Распознавание речи
    private SpeechRecognizer speechRecognizer;
    private android.content.Intent recognizerIntent;

    // Данные
    private List<String> allItems;
    private List<String> remainingItems;
    private String currentItem;
    private int totalWordsCount = 0;
    private int itemsDescribed = 0;

    // Таймер
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 180000;
    private boolean isExerciseActive = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_describe_item, container, false);

        tvItemToDescribe = rootView.findViewById(R.id.tv_item_to_describe);
        tvTimer = rootView.findViewById(R.id.tv_timer);
        tvItemCounter = rootView.findViewById(R.id.tv_item_counter);
        tvDescribeStatus = rootView.findViewById(R.id.tv_describe_status);
        tvResultItems = rootView.findViewById(R.id.tv_result_items);
        tvResultWords = rootView.findViewById(R.id.tv_result_words);
        layoutResult = rootView.findViewById(R.id.layout_result);

        btnStartDescribe = rootView.findViewById(R.id.btn_start_describe);
        btnNextItem = rootView.findViewById(R.id.btn_next_item);
        btnStopDescribe = rootView.findViewById(R.id.btn_stop_describe);
        btnBackDescribe = rootView.findViewById(R.id.btn_back_describe);

        allItems = SpeechDatabase.getItemsToDescribe();
        resetItems();

        initSpeechRecognizer();

        btnStartDescribe.setOnClickListener(v -> startExercise());
        btnNextItem.setOnClickListener(v -> nextItem());
        btnStopDescribe.setOnClickListener(v -> finishExercise());
        btnBackDescribe.setOnClickListener(v -> {
            destroyRecognizer();
            getParentFragmentManager().popBackStack();
        });

        showHintIfNeeded("hint_describe_shown", R.string.hint_describe_title, R.string.hint_describe);

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

    private void initSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(requireActivity())) return;

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireActivity());
        recognizerIntent = new android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(Bundle params) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {}

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    countWords(matches.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    countWords(matches.get(0));
                }
            }

            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void countWords(String text) {
        String[] words = text.toLowerCase().trim().split("\\s+");
        for (String word : words) {
            if (word.length() >= 2 && word.matches(".*[а-яё].*")) {
                totalWordsCount++;
            }
        }
    }

    private void resetItems() {
        remainingItems = new ArrayList<>(allItems);
        Collections.shuffle(remainingItems);
    }

    private void startExercise() {
        if (isExerciseActive) {
            Toast.makeText(getActivity(), "Упражнение уже идёт", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
            return;
        }

        totalWordsCount = 0;
        itemsDescribed = 0;
        resetItems();
        layoutResult.setVisibility(View.GONE);

        nextItemInternal();

        isExerciseActive = true;
        timeLeftInMillis = 180000;
        tvTimer.setText("03:00");

        btnStartDescribe.setEnabled(false);
        btnStartDescribe.setAlpha(0.5f);
        btnNextItem.setEnabled(true);
        btnNextItem.setAlpha(1.0f);
        btnStopDescribe.setEnabled(true);
        btnStopDescribe.setAlpha(1.0f);

        tvDescribeStatus.setText("🔴 Говорите! Микрофон работает 3 минуты");
        tvDescribeStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

        startTimer();
        startListening();
    }

    private void startListening() {
        if (speechRecognizer != null && isExerciseActive) {
            speechRecognizer.startListening(recognizerIntent);
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                int minutes = (int) (timeLeftInMillis / 1000) / 60;
                int seconds = (int) (timeLeftInMillis / 1000) % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                finishExercise();
            }
        }.start();
    }

    private void nextItem() {
        if (!isExerciseActive) return;
        itemsDescribed++;
        nextItemInternal();
        updateItemCounter();
    }

    private void nextItemInternal() {
        if (remainingItems.isEmpty()) resetItems();
        int index = new Random().nextInt(remainingItems.size());
        currentItem = remainingItems.remove(index);
        tvItemToDescribe.setText(currentItem);
    }

    private void finishExercise() {
        if (countDownTimer != null) countDownTimer.cancel();
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
        }
        isExerciseActive = false;

        btnStartDescribe.setEnabled(true);
        btnStartDescribe.setAlpha(1.0f);
        btnNextItem.setEnabled(false);
        btnNextItem.setAlpha(0.5f);
        btnStopDescribe.setEnabled(false);
        btnStopDescribe.setAlpha(0.5f);

        tvTimer.setText("00:00");
        tvDescribeStatus.setText("✅ Упражнение завершено!");
        tvDescribeStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

        layoutResult.setVisibility(View.VISIBLE);
        tvResultItems.setText("Описано предметов: " + itemsDescribed);
        tvResultWords.setText("Названо слов: " + totalWordsCount);

        if (getActivity() != null) {
            AppDatabase db = ((MainActivity) getActivity()).getAppDatabase();
            db.addWordsNamed(totalWordsCount);
            db.incrementTasksDone();
        }

        Toast.makeText(getActivity(),
                "Результат: " + itemsDescribed + " предметов, " + totalWordsCount + " слов",
                Toast.LENGTH_LONG).show();
    }

    private void updateItemCounter() {
        tvItemCounter.setText("Предметов описано: " + itemsDescribed);
    }

    private void destroyRecognizer() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startExercise();
            } else {
                Toast.makeText(getActivity(), "Нужно разрешение микрофона", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isExerciseActive = false;
        destroyRecognizer();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
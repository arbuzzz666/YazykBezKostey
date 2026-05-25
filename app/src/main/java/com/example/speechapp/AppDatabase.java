package com.example.speechapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Класс для управления статистикой пользователя через SharedPreferences.
 * Позволяет сохранять и получать данные между сессиями.
 */
public class AppDatabase {
    private static final String PREFS_NAME = "SpeechAppPrefs";
    private static final String KEY_LESSONS_COMPLETED = "lessons_completed";
    private static final String KEY_TASKS_DONE = "tasks_done";
    private static final String KEY_WORDS_NAMED = "words_named";

    private SharedPreferences sharedPreferences;

    public AppDatabase(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public int getLessonsCompleted() {
        return sharedPreferences.getInt(KEY_LESSONS_COMPLETED, 0);
    }

    public void incrementLessonsCompleted() {
        int currentValue = getLessonsCompleted();
        sharedPreferences.edit().putInt(KEY_LESSONS_COMPLETED, currentValue + 1).apply();
    }

    public int getTasksDone() {
        return sharedPreferences.getInt(KEY_TASKS_DONE, 0);
    }

    public void incrementTasksDone() {
        int currentValue = getTasksDone();
        sharedPreferences.edit().putInt(KEY_TASKS_DONE, currentValue + 1).apply();
    }

    public int getWordsNamed() {
        return sharedPreferences.getInt(KEY_WORDS_NAMED, 0);
    }

    public void addWordsNamed(int wordsCount) {
        int currentValue = getWordsNamed();
        sharedPreferences.edit().putInt(KEY_WORDS_NAMED, currentValue + wordsCount).apply();
    }

    /**
     * Сбрасывает всю статистику пользователя в ноль.
     * Этот метод можно вызвать из любого фрагмента через MainActivity.
     */
    public void resetAllStatistics() {
        sharedPreferences.edit()
                .putInt(KEY_LESSONS_COMPLETED, 0)
                .putInt(KEY_TASKS_DONE, 0)
                .putInt(KEY_WORDS_NAMED, 0)
                .apply();
    }

    /**
     * Сбрасывает только количество пройденных уроков.
     */
    public void resetLessonsCompleted() {
        sharedPreferences.edit().putInt(KEY_LESSONS_COMPLETED, 0).apply();
    }

    /**
     * Сбрасывает только количество выполненных заданий.
     */
    public void resetTasksDone() {
        sharedPreferences.edit().putInt(KEY_TASKS_DONE, 0).apply();
    }

    /**
     * Сбрасывает только количество названных слов.
     */
    public void resetWordsNamed() {
        sharedPreferences.edit().putInt(KEY_WORDS_NAMED, 0).apply();
    }
}
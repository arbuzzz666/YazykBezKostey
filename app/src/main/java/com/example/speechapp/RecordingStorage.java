package com.example.speechapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecordingStorage {
    private static final String PREFS_NAME = "RecordingsPrefs";
    private static final String KEY_TONGUE_TWISTER = "tongue_twister_recordings";
    private static final String KEY_READING = "reading_recordings";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public RecordingStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // ===== Скороговорки =====

    public void saveTongueTwisterRecording(RecordingItem item) {
        List<RecordingItem> recordings = getTongueTwisterRecordings();
        recordings.add(0, item);
        saveList(KEY_TONGUE_TWISTER, recordings);
    }

    public List<RecordingItem> getTongueTwisterRecordings() {
        return getList(KEY_TONGUE_TWISTER);
    }

    public void deleteTongueTwisterRecording(int position) {
        deleteRecording(KEY_TONGUE_TWISTER, position);
    }

    // ===== Чтение вслух =====

    public void saveReadingRecording(RecordingItem item) {
        List<RecordingItem> recordings = getReadingRecordings();
        recordings.add(0, item);
        saveList(KEY_READING, recordings);
    }

    public List<RecordingItem> getReadingRecordings() {
        return getList(KEY_READING);
    }

    public void deleteReadingRecording(int position) {
        deleteRecording(KEY_READING, position);
    }

    // ===== Общие методы =====

    private void deleteRecording(String key, int position) {
        List<RecordingItem> recordings = getList(key);
        if (position >= 0 && position < recordings.size()) {
            java.io.File file = new java.io.File(recordings.get(position).getFilePath());
            if (file.exists()) {
                file.delete();
            }
            recordings.remove(position);
            saveList(key, recordings);
        }
    }

    private void saveList(String key, List<RecordingItem> list) {
        String json = gson.toJson(list);
        sharedPreferences.edit().putString(key, json).apply();
    }

    private List<RecordingItem> getList(String key) {
        String json = sharedPreferences.getString(key, "[]");
        Type type = new TypeToken<List<RecordingItem>>() {}.getType();
        List<RecordingItem> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }
}
package com.example.speechapp;

/**
 * Модель данных для сохранённой записи.
 */
public class RecordingItem {
    private String filePath;
    private String recognizedText;
    private String exerciseName;
    private long timestamp;

    public RecordingItem(String filePath, String recognizedText, String exerciseName, long timestamp) {
        this.filePath = filePath;
        this.recognizedText = recognizedText;
        this.exerciseName = exerciseName;
        this.timestamp = timestamp;
    }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getRecognizedText() { return recognizedText; }
    public void setRecognizedText(String recognizedText) { this.recognizedText = recognizedText; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
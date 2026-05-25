package com.example.speechapp;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class fragment_tongue_twister extends Fragment {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private Button recordButton, stopRecordButton, btnBack, btnViewRecordings;
    private TextView tvTongueTwister, tvRecordStatus;

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private boolean isRecording = false;
    private String audioFilePath;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private RecordingStorage recordingStorage;
    private RecyclerView rvRecordings;
    private RecordingsAdapter recordingsAdapter;
    private List<RecordingItem> recordingItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tongue_twister, container, false);

        tvTongueTwister = rootView.findViewById(R.id.tv_tongue_twister);
        tvRecordStatus = rootView.findViewById(R.id.tv_record_status);
        recordButton = rootView.findViewById(R.id.btn_record);
        stopRecordButton = rootView.findViewById(R.id.btn_stop_record);
        btnBack = rootView.findViewById(R.id.btn_back);
        btnViewRecordings = rootView.findViewById(R.id.btn_view_recordings);
        rvRecordings = rootView.findViewById(R.id.rv_recordings);

        recordingStorage = new RecordingStorage(requireContext());
        recordingItems = new ArrayList<>();

        recordingsAdapter = new RecordingsAdapter(recordingItems, this::playRecording, this::deleteRecording);
        rvRecordings.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecordings.setAdapter(recordingsAdapter);

        tvTongueTwister.setText(getFullLiguriaText());

        recordButton.setOnClickListener(v -> startRecording());
        stopRecordButton.setOnClickListener(v -> stopRecording());
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        btnViewRecordings.setOnClickListener(v -> {
            if (rvRecordings.getVisibility() == View.VISIBLE) {
                rvRecordings.setVisibility(View.GONE);
                btnViewRecordings.setText(R.string.view_recordings);
            } else {
                loadRecordings();
                rvRecordings.setVisibility(View.VISIBLE);
                btnViewRecordings.setText(R.string.hide_recordings);
            }
        });

        showHintIfNeeded("hint_tongue_twister_shown", R.string.hint_tongue_twister_title, R.string.hint_tongue_twister);

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

    private void loadRecordings() {
        recordingItems.clear();
        recordingItems.addAll(recordingStorage.getTongueTwisterRecordings());
        recordingsAdapter.notifyDataSetChanged();
        if (recordingItems.isEmpty()) {
            Toast.makeText(getActivity(), "Нет сохранённых записей", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRecording() {
        if (!checkPermission()) { requestPermission(); return; }
        if (isRecording) {
            Toast.makeText(getActivity(), R.string.already_recording, Toast.LENGTH_SHORT).show();
            return;
        }
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        audioFilePath = requireActivity().getExternalCacheDir().getAbsolutePath() + "/tt_" + timestamp + ".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            recordButton.setEnabled(false);
            stopRecordButton.setEnabled(true);
            stopRecordButton.setAlpha(1.0f);
            tvRecordStatus.setText(R.string.record_status_recording);
            tvRecordStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.record_error, Toast.LENGTH_SHORT).show();
            releaseMediaRecorder();
        }
    }

    private void stopRecording() {
        if (!isRecording) return;
        try { mediaRecorder.stop(); } catch (RuntimeException e) {}
        releaseMediaRecorder();
        isRecording = false;
        recordButton.setEnabled(true);
        stopRecordButton.setEnabled(false);
        stopRecordButton.setAlpha(0.5f);
        tvRecordStatus.setText(R.string.record_status_done);
        tvRecordStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        saveRecording();
        if (getActivity() != null) {
            AppDatabase db = ((MainActivity) getActivity()).getAppDatabase();
            db.incrementLessonsCompleted();
            db.incrementTasksDone();
        }
    }

    private void saveRecording() {
        File file = new File(audioFilePath);
        if (file.exists()) {
            RecordingItem item = new RecordingItem(audioFilePath, "Запись скороговорки", "Скороговорка «Лигурия»", System.currentTimeMillis());
            recordingStorage.saveTongueTwisterRecording(item);
            Toast.makeText(getActivity(), R.string.recording_saved, Toast.LENGTH_SHORT).show();
            if (rvRecordings.getVisibility() == View.VISIBLE) loadRecordings();
        }
    }

    private void playRecording(String filePath) {
        if (mediaPlayer != null) mediaPlayer.release();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(getActivity(), "▶ Воспроизведение...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.play_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteRecording(int position) {
        recordingStorage.deleteTongueTwisterRecording(position);
        loadRecordings();
        Toast.makeText(getActivity(), R.string.recording_deleted, Toast.LENGTH_SHORT).show();
    }

    private String getFullLiguriaText() {
        return "В четверг четвёртого числа в четыре с четвертью часа " +
                "лигурийский регулировщик регулировал в Лигурии, " +
                "но тридцать три корабля лавировали, лавировали, да так и не вылавировали.\n\n" +
                "И потом протокол про протокол протоколом запротоколировал, " +
                "как интервьюером интервьюируемый лигурийский регулировщик речисто, " +
                "да не чисто рапортовал, да не дорапортовал, дорапортовывал, " +
                "да так зарапортовался про размокропогодившуюся погоду, " +
                "что дабы инцидент не стал претендентом на судебный прецедент, " +
                "лигурийский регулировщик акклиматизировался в неконституционном Константинополе, " +
                "где хохлатые хохотушки хохотом хохотали и кричали турке, " +
                "который начерно обкурен трубкой: «Не кури, турка, трубку, " +
                "купи лучше кипу пик, лучше пик кипу купи, " +
                "а то придёт бомбардир из Бранденбурга — бомбами забомбардирует " +
                "за то, что некто чернорылый у него полдвора рылом изрыл, вырыл и подрыл».\n\n" +
                "Но на самом деле турка не был в деле, да и Клара-краля в то время кралась к ларю, " +
                "пока Карл у Клары крал кораллы, за что Клара у Карла украла кларнет, " +
                "а потом на дворе дёготниковой вдовы Варвары два этих вора дрова воровали.\n\n" +
                "Но грех — не смех — не уложить в орех: о Кларе с Карлом во мраке " +
                "все раки шумели в драке, — вот и не до бомбардира ворам было, " +
                "но и не до дёготниковой вдовы, и не до дёготниковых детей.\n\n" +
                "Зато рассердившаяся вдова убрала дрова в сарай: раз дрова, два дрова, три дрова — " +
                "не вместились все дрова, и два дровосека, два дровокола-дроворуба " +
                "для расчувствовавшейся Варвары выдворили дрова вширь двора обратно на дровяной двор, " +
                "где цапля чахла, цапля сохла, цапля сдохла.\n\n" +
                "Цыплёнок же цапли цепко цеплялся за цепь. Молодец против овец, " +
                "а против молодца сам овца, которой носит Сеня сено в сани, " +
                "потом везёт Сенька Соньку с Санькой на санках: " +
                "санки — скок, Сеньку — в бок, Соньку — в лоб, все — в сугроб, " +
                "а Сашка только шапкой шишки сшиб.\n\n" +
                "Затем по шоссе Саша пошёл, саше на шоссе Саша нашёл. " +
                "Сонька же — Сашкина подружка шла по шоссе и сосала сушку, " +
                "да притом у Соньки-вертушки во рту ещё и три ватрушки — " +
                "аккурат в медовик, но ей не до медовика — " +
                "Сонька и с ватрушками во рту пономаря перепономарит, перевыпономарит: " +
                "жужжит, как жужелица, жужжит да кружится.\n\n" +
                "Была у Фрола — Фролу на Лавра наврала, пойдёт к Лавру — " +
                "на Фрола Лавру наврёт, что — вахмистр с вахмистршей, " +
                "ротмистр с ротмистршей, у ужа — ужата, у ежа — ежата, " +
                "а у него высокопоставленный гость унёс трость, " +
                "и вскоре опять пять ребят съели пять опят с полчетвертью четверика чечевицы " +
                "без червоточины, и тысячу шестьсот шестьдесят шесть пирогов " +
                "с творогом из-под простокваши.\n\n" +
                "О всём о том около кола колокола звоном раззванивали, " +
                "да так, что даже Константин — зальцбургский бесперспективняк " +
                "из-под бронетранспортёра констатировал: «Как все колокола не переколоколовать, " +
                "не перевыколоколовать, так и всех скороговорок не перескороговорить, " +
                "не перевыскороговорить. Но попытка — не пытка!»";
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) { mediaRecorder.release(); mediaRecorder = null; }
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), R.string.permission_granted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), R.string.permission_denied, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRecording) stopRecording();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaRecorder();
        if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; }
    }
}
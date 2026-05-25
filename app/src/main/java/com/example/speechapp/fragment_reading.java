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
import android.widget.LinearLayout;
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

public class fragment_reading extends Fragment {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 300;

    private Button btnPoem1, btnPoem2, btnPoem3, btnPoem4, btnPoem5, btnPoem6, btnPoem7, btnPoem8, btnBack;
    private Button btnRecordReading, btnStopRecordReading, btnViewRecordingsReading;
    private LinearLayout layoutRecordingButtons;
    private TextView tvPoemTitle, tvPoemText, tvReadingStatus;
    private RecyclerView rvRecordingsReading;

    private String currentPoemTitle = "";
    private String currentPoemText = "";

    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private boolean isRecording = false;
    private String audioFilePath;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};

    private RecordingStorage recordingStorage;
    private RecordingsAdapter recordingsAdapter;
    private List<RecordingItem> recordingItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reading, container, false);

        btnPoem1 = rootView.findViewById(R.id.btn_poem1);
        btnPoem2 = rootView.findViewById(R.id.btn_poem2);
        btnPoem3 = rootView.findViewById(R.id.btn_poem3);
        btnPoem4 = rootView.findViewById(R.id.btn_poem4);
        btnPoem5 = rootView.findViewById(R.id.btn_poem5);
        btnPoem6 = rootView.findViewById(R.id.btn_poem6);
        btnPoem7 = rootView.findViewById(R.id.btn_poem7);
        btnPoem8 = rootView.findViewById(R.id.btn_poem8);
        btnBack = rootView.findViewById(R.id.btn_back_from_reading);

        layoutRecordingButtons = rootView.findViewById(R.id.layout_recording_buttons);
        btnRecordReading = rootView.findViewById(R.id.btn_record_reading);
        btnStopRecordReading = rootView.findViewById(R.id.btn_stop_record_reading);
        btnViewRecordingsReading = rootView.findViewById(R.id.btn_view_recordings_reading);

        tvPoemTitle = rootView.findViewById(R.id.tv_poem_title);
        tvPoemText = rootView.findViewById(R.id.tv_poem_text);
        tvReadingStatus = rootView.findViewById(R.id.tv_reading_status);
        rvRecordingsReading = rootView.findViewById(R.id.rv_recordings_reading);

        recordingStorage = new RecordingStorage(requireContext());
        recordingItems = new ArrayList<>();

        recordingsAdapter = new RecordingsAdapter(recordingItems, this::playRecording, this::deleteRecording);
        rvRecordingsReading.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecordingsReading.setAdapter(recordingsAdapter);

        btnPoem1.setOnClickListener(v -> selectPoem1());
        btnPoem2.setOnClickListener(v -> selectPoem2());
        btnPoem3.setOnClickListener(v -> selectPoem3());
        btnPoem4.setOnClickListener(v -> selectPoem4());
        btnPoem5.setOnClickListener(v -> selectPoem5());
        btnPoem6.setOnClickListener(v -> selectPoem6());
        btnPoem7.setOnClickListener(v -> selectPoem7());
        btnPoem8.setOnClickListener(v -> selectPoem8());

        btnRecordReading.setOnClickListener(v -> startRecording());
        btnStopRecordReading.setOnClickListener(v -> stopRecording());

        btnViewRecordingsReading.setOnClickListener(v -> {
            if (rvRecordingsReading.getVisibility() == View.VISIBLE) {
                rvRecordingsReading.setVisibility(View.GONE);
                btnViewRecordingsReading.setText(R.string.view_recordings);
            } else {
                loadRecordings();
                rvRecordingsReading.setVisibility(View.VISIBLE);
                btnViewRecordingsReading.setText(R.string.hide_recordings);
            }
        });

        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        showHintIfNeeded("hint_reading_shown", R.string.hint_reading_title, R.string.hint_reading);

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

    // Стихотворения
    private void selectPoem1() {
        currentPoemTitle = "«Я помню чудное мгновенье...» (К***)";
        currentPoemText = "Я помню чудное мгновенье:\nПередо мной явилась ты,\nКак мимолётное виденье,\nКак гений чистой красоты.\n\nВ томленьях грусти безнадежной,\nВ тревогах шумной суеты,\nЗвучал мне долго голос нежный\nИ снились милые черты.\n\nШли годы. Бурь порыв мятежный\nРассеял прежние мечты,\nИ я забыл твой голос нежный,\nТвои небесные черты.\n\nВ глуши, во мраке заточенья\nТянулись тихо дни мои\nБез божества, без вдохновенья,\nБез слёз, без жизни, без любви.\n\nДуше настало пробужденье:\nИ вот опять явилась ты,\nКак мимолётное виденье,\nКак гений чистой красоты.\n\nИ сердце бьётся в упоенье,\nИ для него воскресли вновь\nИ божество, и вдохновенье,\nИ жизнь, и слёзы, и любовь.\n\n1825 г.";
        showPoem();
    }

    private void selectPoem2() {
        currentPoemTitle = "«Зимнее утро»";
        currentPoemText = "Мороз и солнце; день чудесный!\nЕщё ты дремлешь, друг прелестный —\nПора, красавица, проснись:\nОткрой сомкнуты негой взоры\nНавстречу северной Авроры,\nЗвездою севера явись!\n\nВечор, ты помнишь, вьюга злилась,\nНа мутном небе мгла носилась;\nЛуна, как бледное пятно,\nСквозь тучи мрачные желтела,\nИ ты печальная сидела —\nА нынче... погляди в окно:\n\nПод голубыми небесами\nВеликолепными коврами,\nБлестя на солнце, снег лежит;\nПрозрачный лес один чернеет,\nИ ель сквозь иней зеленеет,\nИ речка подо льдом блестит.\n\nВся комната янтарным блеском\nОзарена. Весёлым треском\nТрещит затопленная печь.\nПриятно думать у лежанки.\nНо знаешь: не велеть ли в санки\nКобылку бурую запречь?\n\nСкользя по утреннему снегу,\nДруг милый, предадимся бегу\nНетерпеливого коня\nИ навестим поля пустые,\nЛеса, недавно столь густые,\nИ берег, милый для меня.\n\n1829 г.";
        showPoem();
    }

    private void selectPoem3() {
        currentPoemTitle = "«Я вас любил...»";
        currentPoemText = "Я вас любил: любовь ещё, быть может,\nВ душе моей угасла не совсем;\nНо пусть она вас больше не тревожит;\nЯ не хочу печалить вас ничем.\n\nЯ вас любил безмолвно, безнадежно,\nТо робостью, то ревностью томим;\nЯ вас любил так искренно, так нежно,\nКак дай вам бог любимой быть другим.\n\n1829 г.";
        showPoem();
    }

    private void selectPoem4() {
        currentPoemTitle = "«Узник»";
        currentPoemText = "Сижу за решёткой в темнице сырой.\nВскормлённый в неволе орёл молодой,\nМой грустный товарищ, махая крылом,\nКровавую пищу клюёт под окном.\n\nКлюёт, и бросает, и смотрит в окно,\nКак будто со мною задумал одно.\nЗовёт меня взглядом и криком своим\nИ вымолвить хочет: «Давай улетим!\n\nМы вольные птицы; пора, брат, пора!\nТуда, где за тучей белеет гора,\nТуда, где синеют морские края,\nТуда, где гуляем лишь ветер... да я!..»\n\n1822 г.";
        showPoem();
    }

    private void selectPoem5() {
        currentPoemTitle = "«Няне»";
        currentPoemText = "Подруга дней моих суровых,\nГолубка дряхлая моя!\nОдна в глуши лесов сосновых\nДавно, давно ты ждёшь меня.\n\nТы под окном своей светлицы\nГорюешь, будто на часах,\nИ медлят поминутно спицы\nВ твоих наморщенных руках.\n\nГлядишь в забытые вороты\nНа чёрный отдалённый путь:\nТоска, предчувствия, заботы\nТеснят твою всечасно грудь.\n\n1826 г.";
        showPoem();
    }

    private void selectPoem6() {
        currentPoemTitle = "«Туча»";
        currentPoemText = "Последняя туча рассеянной бури!\nОдна ты несёшься по ясной лазури,\nОдна ты наводишь унылую тень,\nОдна ты печалишь ликующий день.\n\nТы небо недавно кругом облегала,\nИ молния грозно тебя обвивала;\nИ ты издавала таинственный гром\nИ алчную землю поила дождём.\n\nДовольно, сокройся! Пора миновалась,\nЗемля освежилась, и буря промчалась,\nИ ветер, лаская листочки древес,\nТебя с успокоенных гонит небес.\n\n1835 г.";
        showPoem();
    }

    private void selectPoem7() {
        currentPoemTitle = "«Осень» (отрывок)";
        currentPoemText = "Октябрь уж наступил — уж роща отряхает\nПоследние листы с нагих своих ветвей;\nДохнул осенний хлад — дорога промерзает.\nЖурча ещё бежит за мельницу ручей,\nНо пруд уже застыл; сосед мой поспешает\nВ отъезжие поля с охотою своей,\nИ страждут озими от бешеной забавы,\nИ будит лай собак уснувшие дубравы.\n\n1833 г.";
        showPoem();
    }

    private void selectPoem8() {
        currentPoemTitle = "«Пророк»";
        currentPoemText = "Духовной жаждою томим,\nВ пустыне мрачной я влачился, —\nИ шестикрылый серафим\nНа перепутье мне явился.\nПерстами лёгкими как сон\nМоих зениц коснулся он.\nОтверзлись вещие зеницы,\nКак у испуганной орлицы.\nМоих ушей коснулся он, —\nИ их наполнил шум и звон:\nИ внял я неба содроганье,\nИ горний ангелов полёт,\nИ гад морских подводный ход,\nИ дольней лозы прозябанье.\n\n1826 г.";
        showPoem();
    }

    private void showPoem() {
        tvPoemTitle.setVisibility(View.VISIBLE);
        tvPoemTitle.setText(currentPoemTitle);
        tvPoemText.setVisibility(View.VISIBLE);
        tvPoemText.setText(currentPoemText);
        layoutRecordingButtons.setVisibility(View.VISIBLE);
        btnViewRecordingsReading.setVisibility(View.VISIBLE);
        tvReadingStatus.setText(R.string.reading_status_default);
        tvReadingStatus.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }

    private void startRecording() {
        if (!checkPermission()) { requestPermission(); return; }
        if (isRecording) { Toast.makeText(getActivity(), R.string.already_recording, Toast.LENGTH_SHORT).show(); return; }
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        audioFilePath = requireActivity().getExternalCacheDir().getAbsolutePath() + "/reading_" + timestamp + ".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            btnRecordReading.setEnabled(false);
            btnStopRecordReading.setEnabled(true);
            btnStopRecordReading.setAlpha(1.0f);
            tvReadingStatus.setText("🔴 Идёт запись... Читайте выразительно!");
            tvReadingStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
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
        btnRecordReading.setEnabled(true);
        btnStopRecordReading.setEnabled(false);
        btnStopRecordReading.setAlpha(0.5f);
        tvReadingStatus.setText("✅ Запись завершена! Упражнение выполнено.");
        tvReadingStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        saveRecording();
        if (getActivity() != null) {
            AppDatabase db = ((MainActivity) getActivity()).getAppDatabase();
            db.incrementTasksDone();
            db.incrementLessonsCompleted();
        }
    }

    private void saveRecording() {
        File file = new File(audioFilePath);
        if (file.exists()) {
            RecordingItem item = new RecordingItem(audioFilePath, "Чтение: " + currentPoemTitle, currentPoemTitle, System.currentTimeMillis());
            recordingStorage.saveReadingRecording(item);
            Toast.makeText(getActivity(), R.string.recording_saved, Toast.LENGTH_SHORT).show();
            if (rvRecordingsReading.getVisibility() == View.VISIBLE) loadRecordings();
        }
    }

    private void loadRecordings() {
        recordingItems.clear();
        recordingItems.addAll(recordingStorage.getReadingRecordings());
        recordingsAdapter.notifyDataSetChanged();
        if (recordingItems.isEmpty()) Toast.makeText(getActivity(), "Нет сохранённых записей", Toast.LENGTH_SHORT).show();
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
        recordingStorage.deleteReadingRecording(position);
        loadRecordings();
        Toast.makeText(getActivity(), R.string.recording_deleted, Toast.LENGTH_SHORT).show();
    }

    private void releaseMediaRecorder() { if (mediaRecorder != null) { mediaRecorder.release(); mediaRecorder = null; } }

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

    @Override public void onStop() { super.onStop(); if (isRecording) stopRecording(); }

    @Override public void onDestroy() {
        super.onDestroy();
        releaseMediaRecorder();
        if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; }
    }
}
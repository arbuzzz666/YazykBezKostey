package com.example.speechapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class fragment_diction extends Fragment {

    private LinearLayout layoutTongueTwister;
    private LinearLayout layoutReading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_diction, container, false);

        // Инициализация элементов по ID из макета fragment_diction.xml
        // ID должны совпадать с android:id в XML-файле
        layoutTongueTwister = rootView.findViewById(R.id.layout_tongue_twister);
        layoutReading = rootView.findViewById(R.id.layout_reading);

        // При нажатии на карточку "Скороговорка" переходим на TongueTwisterFragment
        layoutTongueTwister.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, new fragment_tongue_twister())
                    .addToBackStack(null) // Чтобы можно было вернуться назад
                    .commit();
        });

        // При нажатии на карточку "Чтение вслух" переходим на ReadingFragment
        layoutReading.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, new fragment_reading())
                    .addToBackStack(null) // Чтобы можно было вернуться назад
                    .commit();
        });

        return rootView;
    }
}
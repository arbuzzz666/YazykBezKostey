package com.example.speechapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class fragment_literacy extends Fragment {

    private LinearLayout layoutDescribeItem;
    private LinearLayout layoutInterview;
    private LinearLayout layoutMarathon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_literacy, container, false);

        // Инициализация карточек
        layoutDescribeItem = rootView.findViewById(R.id.layout_describe_item);
        layoutInterview = rootView.findViewById(R.id.layout_interview);
        layoutMarathon = rootView.findViewById(R.id.layout_marathon);

        // Переход на экран "Описание предмета"
        layoutDescribeItem.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, new fragment_describe_item())
                    .addToBackStack(null)
                    .commit();
        });

        // Переход на экран "Симулятор собеседования"
        layoutInterview.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, new fragment_interview())
                    .addToBackStack(null)
                    .commit();
        });

        // Переход на экран "Лексический марафон"
        layoutMarathon.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, new fragment_marathon())
                    .addToBackStack(null)
                    .commit();
        });

        return rootView;
    }
}
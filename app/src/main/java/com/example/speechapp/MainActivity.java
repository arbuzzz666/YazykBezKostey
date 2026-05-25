package com.example.speechapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // Поле для работы со статистикой. Передается или используется внутри фрагментов.
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализируем базу данных статистики
        appDatabase = new AppDatabase(this);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Загружаем первый фрагмент при старте приложения, если он еще не загружен
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_view, new fragment_diction())
                    .commit();
        }
    }

    // Метод для получения базы данных из фрагментов
    public AppDatabase getAppDatabase() {
        return appDatabase;
    }

    // Слушатель для переключения фрагментов через нижнюю навигацию
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    int itemId = item.getItemId();
                    if (itemId == R.id.navigation_diction) {
                        selectedFragment = new fragment_diction();
                    } else if (itemId == R.id.navigation_literacy) {
                        selectedFragment = new fragment_literacy();
                    } else if (itemId == R.id.navigation_profile) {
                        selectedFragment = new fragment_profile();
                    }

                    // Замена текущего фрагмента выбранным
                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_view, selectedFragment)
                                .commit();
                    }
                    return true;
                }
            };
}
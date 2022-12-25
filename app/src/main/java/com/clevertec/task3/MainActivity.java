package com.clevertec.task3;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.room.Room;
import com.clevertec.task3.database.AppDatabase;
import com.clevertec.task3.fragment.MainFragment;
import com.clevertec.task3.singleton.ConnectionSingleton;

public class MainActivity extends AppCompatActivity {
    private final String NAME_DB = "contact_db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppDatabase appDatabase = Room
                .databaseBuilder(getApplicationContext(), AppDatabase.class, NAME_DB)
                .allowMainThreadQueries()
                .build();
        // TODO: vitmvit: в тестовых целях
        //appDatabase.contactDao().deleteAll();
        ConnectionSingleton.getInstance(appDatabase);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(R.id.container, MainFragment.newInstance())
                .commit();
    }
}
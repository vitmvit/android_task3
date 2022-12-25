package com.clevertec.task3.singleton;

import com.clevertec.task3.database.AppDatabase;

public class ConnectionSingleton {

    private static ConnectionSingleton instance;
    private final AppDatabase appDatabase;

    private ConnectionSingleton(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public static ConnectionSingleton getInstance(AppDatabase appDatabase) {
        if (instance == null) {
            instance = new ConnectionSingleton(null);
        }
        if (appDatabase != null) {
            instance = new ConnectionSingleton(appDatabase);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}

package com.clevertec.task3.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.clevertec.task3.dao.ContactDao;
import com.clevertec.task3.model.Contact;

@Database(entities = {Contact.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ContactDao contactDao();
}
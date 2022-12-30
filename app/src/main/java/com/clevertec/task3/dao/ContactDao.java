package com.clevertec.task3.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.clevertec.task3.model.Contact;

import java.util.List;

@Dao
public interface ContactDao {

    @Query("SELECT * FROM contact")
    List<Contact> findAll();

    @Query("SELECT * FROM contact WHERE id = :id")
    Contact findById(Long id);

    @Query("SELECT * FROM contact WHERE name LIKE :name LIMIT 1")
    Contact findByName(String name);

    @Query("SELECT * FROM contact WHERE phone LIKE :phone LIMIT 1")
    Contact findByPhone(String phone);

    @Insert
    void save(Contact contact);

    @Delete
    void delete(Contact contact);

    @Query("DELETE FROM contact")
    void deleteAll();
}

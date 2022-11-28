package com.example.keepmynotes.Model.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.keepmynotes.Model.User;

@Dao
public interface UserDAO {
    @Query("SELECT  * FROM User LIMIT 1")
    User getCurrentUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Query("DELETE FROM User")
    void DeleteUser();
}

package com.cqu.notes.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DatabaseLists {
    @Query("SELECT * FROM NotesItem WHERE type=0 ORDER BY pos")
    List<NotesItem> getAllType();

    @Query("SELECT * FROM NotesItem WHERE type=1 AND (:typeName='全部类别' OR :typeName=noteType) ORDER BY modifiedTime DESC")
    List<NotesItem> getNote(String typeName);

    @Query("SELECT * FROM NotesItem WHERE type=2 ORDER BY done,eventTime")
    List<NotesItem> getAllTodo();

    @Query("DELETE FROM NotesItem WHERE type=1 AND :typeName=noteType")
    void deleteNote(String typeName);

    @Insert
    void insert(NotesItem... cw);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(NotesItem... cw);

    @Delete
    void delete(NotesItem... cw);

    @Query("SELECT COUNT(*) FROM NotesItem WHERE title=:s")
    boolean existType(String s);

}
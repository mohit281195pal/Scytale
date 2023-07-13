package com.example.Scytale.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface MessageDatabaseDao {

    @Insert
    fun insert(register: MessageEntity)

    @Query("SELECT * FROM message_table ORDER BY messageId DESC")
    fun getAllMessages(): LiveData<List<MessageEntity>>

}
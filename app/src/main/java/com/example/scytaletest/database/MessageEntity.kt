package com.example.Scytale.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_table")
data class MessageEntity(

    @PrimaryKey(autoGenerate = true)
    var messageId: Int = 0,

    @ColumnInfo(name = "user_name")
    var UserName: String,

    @ColumnInfo(name = "message")
    var Message: String,
    @ColumnInfo(name = "time")
    var Time: String,

    )
package com.example.scytaletest.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.Scytale.database.MessageDatabaseDao
import com.example.Scytale.database.MessageEntity

@Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
abstract class MessageDatabase : RoomDatabase() {

    abstract val messageDatabaseDao: MessageDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: MessageDatabase? = null


        fun getInstance(context: Context): MessageDatabase {
            synchronized(this) {

                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MessageDatabase::class.java,
                        "message_database"
                    )
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}


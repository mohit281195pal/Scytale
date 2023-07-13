package com.example.Scytale.database

import android.util.Log

class MessageRepository(private val dao: MessageDatabaseDao) {

    val messages = dao.getAllMessages()
    fun insert(user: MessageEntity) {
        return dao.insert(user)
    }


}
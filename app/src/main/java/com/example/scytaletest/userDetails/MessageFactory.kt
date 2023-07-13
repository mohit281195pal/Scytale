package com.example.Scytale.userDetails

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.Scytale.database.MessageRepository
import com.example.scytaletest.userDetails.MessageViewModel

class MessageFactory(
    private val repository: MessageRepository,
    private val application: Application
) :
        ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            return MessageViewModel(repository, application) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }

}
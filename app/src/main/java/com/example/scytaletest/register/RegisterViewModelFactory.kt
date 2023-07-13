package com.example.Scytale.register

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.Scytale.database.MessageRepository
import com.example.scytaletest.register.RegisterViewModel
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class RegisterViewModelFactory(
    private  val repository: MessageRepository,
    private val application: Application):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel( application) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}
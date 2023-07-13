package com.example.scytaletest.userDetails

import android.app.Application
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.Scytale.database.MessageEntity
import com.example.Scytale.database.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class MessageViewModel(private val repository: MessageRepository, application: Application) :
    AndroidViewModel(application) , Observable {

    var message = repository.messages
    val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
    val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX
    init {
        Log.i("MYTAG", "inside_users_Lisrt_init")
    }

    @Bindable
    val inputMessage = MutableLiveData<String>()

    private val _navigateto = MutableLiveData<String>()

    val navigateto: LiveData<String>
        get() = _navigateto

    private val _messageSent = MutableLiveData<String>()

    val messageSent: LiveData<String>
        get() = _messageSent

    fun doneNavigating() {
        _navigateto.value = ""
    }
    fun startNavigating() {
        _navigateto.value = "details"
    }
    fun donemessage() {
        message= repository.messages
    }


    private val _errorToast = MutableLiveData<Boolean>()
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val errotoast: LiveData<Boolean>
        get() = _errorToast

    fun backButtonclicked() {
        _navigateto.value = "back"
    }

    fun donetoast() {
        _errorToast.value = false
        Log.i("MYTAG", "Done taoasting ")
    }

    fun sendMessage() {
        if (inputMessage.value == null) {
            _errorToast.value = true
        } else {
            uiScope.launch {
//            withContext(Dispatchers.IO) {

                val currentTime: String =
                    SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                val message = encrypt(inputMessage.value!!)
                val email =
                    PreferenceManager.getDefaultSharedPreferences(getApplication<Application?>().applicationContext)
                        .getString("email", "")

                val time = currentTime
                Log.i("MYTAG", "insidi Sumbit")
                insert(MessageEntity(0, email!!, message!!, time))

                _messageSent.value = message
            }
        }
    }
    fun encrypt(strToEncrypt: String) :  String?
    {
        try
        {
            val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec)
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            return Base64.encodeToString(cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8)), Base64.DEFAULT)
        }
        catch (e: Exception)
        {
            println("Error while encrypting: $e")
        }
        return null
    }

    private fun insert(message: MessageEntity): Job = viewModelScope.launch {
        repository.insert(message)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

}
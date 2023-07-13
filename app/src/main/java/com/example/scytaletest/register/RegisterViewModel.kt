package com.example.scytaletest.register

import android.app.Application
import android.util.Base64
import android.util.Log
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class RegisterViewModel(application: Application) :
    AndroidViewModel(application), Observable {
var photoStr:String =""

     val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
     val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
     val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX

    init {
        Log.i("MYTAG", "init")
    }

    @Bindable
    val inputFirstName = MutableLiveData<String>()

    @Bindable
    val inputLastName = MutableLiveData<String>()

    @Bindable
    val inputMobile = MutableLiveData<String>()

    @Bindable
    val inputEmail = MutableLiveData<String>()

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _navigateto = MutableLiveData<Boolean>()

    val navigateto: LiveData<Boolean>
        get() = _navigateto
    private val _Details = MutableLiveData<String>()

    val Details: LiveData<String>
        get() = _Details

    private val _addImage = MutableLiveData<Boolean>()

    val addImage: LiveData<Boolean>
        get() = _addImage

    private val _errorToast = MutableLiveData<Boolean>()

    val errotoast: LiveData<Boolean>
        get() = _errorToast

    private val _errorToastUsername = MutableLiveData<Boolean>()

    val errotoastUsername: LiveData<Boolean>
        get() = _errorToastUsername


    fun sumbitButton() {
        Log.i("MYTAG", "Inside SUBMIT BUTTON")
        if (_addImage.value == null || inputFirstName.value == null || inputLastName.value == null || inputMobile.value == null || inputEmail.value == null) {
            _errorToast.value = true
        } else {
            uiScope.launch {


                Log.i("MYTAG", "OK im in")
                val firstName = encrypt(inputFirstName.value!!)
                val lastName =encrypt( inputLastName.value!!)
                val mobile = encrypt(inputMobile.value!!)
                val email = encrypt(inputEmail.value!!)
                val photo=encrypt(photoStr)
                Log.i("MYTAG", "insidi Sumbit")
                _Details.value= "$firstName,$lastName,$mobile,$email,$photo"
                inputFirstName.value = null
                inputLastName.value = null
                inputMobile.value = null
                inputEmail.value = null
                _navigateto.value = true
            }
        }
    }
    fun doneNavigating() {
        _navigateto.value = false
        Log.i("MYTAG", "Done navigating ")
    }



    fun addPhoto() {
        Log.i("MYTAG", "Inside Add PHOTO")
        _addImage.value=true
    }
    fun donePhoto(profileStr: String) {
        Log.i("MYTAG", "Inside Add PHOTO")
        _addImage.value=false
        this.photoStr=profileStr
    }

    fun donetoast() {
        _errorToast.value = false
        Log.i("MYTAG", "Done taoasting ")
    }


//    fun clearALl():Job = viewModelScope.launch {
//        repository.deleteAll()
//    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

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


}








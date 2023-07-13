package com.example.scytaletest.userDetails

import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.Scytale.database.MessageEntity
import com.example.scytaletest.R
import com.example.scytaletest.databinding.MessageItemBinding
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class MyRecycleViewAdapter(
    private val messageViewModel: MessageViewModel,
    private val messageList: List<MessageEntity>,
    private val email: String?
) :
    RecyclerView.Adapter<MyviewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyviewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: MessageItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.message_item, parent, false)
        return MyviewHolder(binding)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MyviewHolder, position: Int) {
        holder.bind(messageList[position], email)
        holder.itemView.setOnClickListener(
            {
                messageViewModel.startNavigating()
            }
        )

    }


}

class MyviewHolder(private val binding: MessageItemBinding) : RecyclerView.ViewHolder(binding.root) {
    val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
    val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX
    fun bind(message: MessageEntity, email: String?) {
        if (message.UserName.equals(email)) {
            binding.bubbleSent.visibility = View.VISIBLE
            binding.bubble.visibility = View.GONE
            val messageStr:String= decrypt(message.Message)
            val emailStr:String= decrypt(message.UserName)
            binding.senderTextView.text = emailStr.toString()
            binding.messageSentTextView.text = messageStr.toString()
            binding.SenttimestampTextView.text = message.Time

        } else {
            binding.bubbleSent.visibility = View.GONE
            binding.bubble.visibility = View.VISIBLE
            val messageStr=decrypt(message.Message)
            val emailStr= decrypt(message.UserName)
            binding.reciveTextView.text = emailStr.toString()
            binding.messageTextView.text = messageStr.toString()
            binding.timestampTextView.text = message.Time
        }

    }
    fun decrypt(strToDecrypt : String) : String {
        try
        {

            val ivParameterSpec =  IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =  PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey =  SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return  String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
        }
        catch (e : Exception) {
            println("Error while decrypting: $e");
        }
        return ""
    }
}
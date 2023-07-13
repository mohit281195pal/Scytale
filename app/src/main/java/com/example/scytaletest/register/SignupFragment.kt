package com.example.scytaletest.register

import android.Manifest
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.scytaletest.R
import com.example.scytaletest.databinding.RegisterHomeFragmentBinding
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


class SignupFragment : Fragment() {
    val secretKey = "tK5UTui+DPh8lIlBxya5XVsmeDCoUl6vHhdIESMB6sQ="
    val salt = "QWlGNHNhMTJTQWZ2bGhpV3U=" // base64 decode => AiF4sa12SAfvlhiWu
    val iv = "bVQzNFNhRkQ1Njc4UUFaWA==" // base64 decode => mT34SaFD5678QAZX
    private lateinit var registerViewModel: RegisterViewModel
    var binding: RegisterHomeFragmentBinding? = null
    var profileStr: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.register_home_fragment, container, false
        )



        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        binding!!.myViewModel = registerViewModel

        binding!!.lifecycleOwner = this
        if (arguments != null) {


            if (requireArguments().getString("details").equals("details")) {
        val firstName = decrypt(requireContext().getSharedPreferences("SHARED_PREFS", MODE_PRIVATE)
                        .getString("firstName", "")!!
                ).toString()
                val secondName = decrypt(
                    requireContext().getSharedPreferences("SHARED_PREFS", MODE_PRIVATE)
                        .getString("lastName", "")!!
                ).toString()
                val mobile = decrypt(
                    requireContext().getSharedPreferences("SHARED_PREFS", MODE_PRIVATE)
                        .getString("mobile", "")!!
                ).toString()
                val email = decrypt(
                    requireContext().getSharedPreferences("SHARED_PREFS", MODE_PRIVATE)
                        .getString("email", "")!!
                ).toString()
                binding!!.firstNameText.text = "First Name: "+firstName
                binding!!.secondNameText.text = "Last Name: "+secondName

                binding!!.userNameTextView.text ="Mobile: "+
                    mobile

                binding!!.passwordTextView.text ="Email: "+
                    email

                val photo = decrypt(
                    requireContext().getSharedPreferences("SHARED_PREFS", MODE_PRIVATE).getString("photo", "")!!
                )
                val encodeByte: ByteArray =
                    Base64.decode(photo, Base64.DEFAULT)
                binding!!.profileImage.setImageBitmap(
                    BitmapFactory.decodeByteArray(
                        encodeByte,
                        0,
                        encodeByte.size
                    )
                )
                binding!!.submitButton.visibility = View.GONE
                binding!!.firstNameTextField.visibility = View.GONE
                binding!!.secondNameTextField.visibility = View.GONE
                binding!!.userNameTextField.visibility = View.GONE
                binding!!.passwordTextField.visibility = View.GONE
            }
        }

        registerViewModel.navigateto.observe(viewLifecycleOwner, Observer { hasFinished ->
            if (hasFinished == true) {
                val action = SignupFragmentDirections.actionSignupToMessage()
                NavHostFragment.findNavController(this).navigate(action)

                /* val someFragment: Fragment = MessgaeFragment()
                 val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
                 transaction.replace(
                     R.id.myNavHostFragment,
                     someFragment
                 ) // give your fragment container id in first parameter

                 transaction.addToBackStack(null) // if written, this transaction will be added to backstack

                 transaction.commit()*/
                registerViewModel.doneNavigating()
            }
        })
        registerViewModel.addImage.observe(viewLifecycleOwner, Observer { hasFinished ->
            if (hasFinished == true) {
                val pictureDialog = AlertDialog.Builder(requireContext())
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from gallery", "Capture photo from camera")
                pictureDialog.setItems(
                    pictureDialogItems
                ) { dialog, which ->
                    when (which) {
                        0 -> choosePhotoFromGallary()
                        1 -> takePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }
        })




        registerViewModel.errotoast.observe(viewLifecycleOwner, Observer { hasError ->
            if (hasError == true) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                    .show()
                registerViewModel.donetoast()
            }
        })

        registerViewModel.Details.observe(viewLifecycleOwner, Observer { userDetails ->
            if (!userDetails.equals("")) {
                val tempStr = userDetails.split(",")
                val firstName = tempStr[0]
                val lastName = tempStr[1]
                val mobile = tempStr[2]
                val email = tempStr[3]
                val photo = tempStr[4]
                val sharedPreferences = requireContext().getSharedPreferences("SHARED_PREFS", MODE_PRIVATE)
                val prefEditor = sharedPreferences.edit()
                prefEditor.putString("firstName", firstName)
                prefEditor.putString("lastName", lastName)
                prefEditor.putString("mobile", mobile)
                prefEditor.putString("email", email)
                prefEditor.putString("photo", photo)
                prefEditor.apply()


            }
        })



        return binding!!.root
    }

    fun choosePhotoFromGallary() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 1000
            );
        } else {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 100)
        }


    }

    fun takePhotoFromCamera() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA), 1001
            );
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 101)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(context?.contentResolver, contentURI)
                    Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
                    binding!!.profileImage.setImageBitmap(bitmap)
                    profileStr = BitmapToString(bitmap)
                    registerViewModel.donePhoto(profileStr)

                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed!", Toast.LENGTH_LONG).show()
                }
            }
        } else if (requestCode == 101) {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            binding!!.profileImage.setImageBitmap(thumbnail)
            profileStr = BitmapToString(thumbnail)
            registerViewModel.donePhoto(profileStr)
            Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
        }
    }

    fun BitmapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()

        return Base64.encodeToString(b, Base64.DEFAULT)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1000 ->                 // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, 100)
                }
            1001 ->                 // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, 101)
                }
        }
    }

    fun decrypt(strToDecrypt: String): String? {
        try {

            val ivParameterSpec = IvParameterSpec(Base64.decode(iv, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =
                PBEKeySpec(secretKey.toCharArray(), Base64.decode(salt, Base64.DEFAULT), 10000, 256)
            val tmp = factory.generateSecret(spec);
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
        } catch (e: Exception) {
            println("Error while decrypting: $e");
        }
        return null
    }
}
package com.priyo.songthrush

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.priyo.songthrush.databinding.ActivityCreateProfileBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CreateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProfileBinding

    private val PERMISSION_CODE = 101
    var currentPhotoPath: String? = null

    private var tempUri: Uri? = null
    var activityResultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cameraIcon.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            openCamera()
        }

        binding.submitButton.setOnClickListener {
            onSubmitClick()
        }
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val f = File(currentPhotoPath)
                val imageUri = Uri.fromFile(f)

                tempUri = imageUri
                Log.d("ProfileActivity", imageUri.toString())
                Glide.with(this).load(imageUri).circleCrop().into(binding.userImage)
//                binding.userImage.setImageURI(Uri.fromFile(f))

                //Log.e("frontPath", frontPhotoPath)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString("imagePath", currentPhotoPath)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentPhotoPath = savedInstanceState.getString("imagePath")
        if (currentPhotoPath!=null) {
            val f = File(currentPhotoPath)
            val imageUri = Uri.fromFile(f)

            tempUri = imageUri
            Log.d("ProfileActivity", imageUri.toString())
            Glide.with(this).load(imageUri).circleCrop().into(binding.userImage)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    dispatchTakePictureIntent()
                } else {
                    Toast.makeText(this, "permission denied...", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = timeStamp
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        Log.e(this.javaClass.name, currentPhotoPath.toString())
        return image
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    this,
                    "com.priyo.songthrush.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                activityResultLauncher?.launch(takePictureIntent)
            }
        }
    }

    private fun openCamera() {

        if (checkSelfPermission(Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_DENIED ||
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_DENIED
        ) {
            //permission not enable, request it
            val permission =
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            //show popup to request permissions
            requestPermissions(permission, PERMISSION_CODE)
        } else {
            //permission already given
            dispatchTakePictureIntent()
        }
    }

    /**
     * Checks if Email Valid or Not
     *
     * @param text
     * @return boolean
     */
    private fun isEmailValid(text: String): Boolean {
        if (text.isEmpty()) {
            //error
            binding.emailLayout.error = "Email Can't be Empty"
            return false
        }
        return if (Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
            //success
            binding.emailLayout.error = null
            true
        } else {
            //error enter valid email
            binding.emailLayout.error = "Invalid Email!"
            false
        }
    }

    /**
     * Check for valid username
     * @param userName
     * @return
     */
    private fun isUsernameValid(userName: String): Boolean {
        return if (userName.isEmpty()) {
            binding.userNameLayout.error = "Username Can't be Empty"
            false
        } else {
            binding.userNameLayout.error = null
            true
        }
    }

    /**
     * Handle Click on Submit Button
     */
    private fun onSubmitClick() {
        if (!isUsernameValid(binding.userFullName.text.toString())) {
            Toast.makeText(applicationContext, "Enter valid username", Toast.LENGTH_SHORT).show()
        } else if (!isEmailValid(binding.userEmail.text.toString())) {
            Toast.makeText(applicationContext, "Enter valid email", Toast.LENGTH_SHORT).show()
        } else if (tempUri.toString().isEmpty() || tempUri == null) {
            Toast.makeText(applicationContext, "Please Select a photo", Toast.LENGTH_SHORT).show()
            Log.d("TAG", tempUri.toString())
        } else {
            tempUri?.path?.let {
                openViewActivity(
                    binding.userFullName.text.toString(), binding.userEmail.text.toString(),
                    it
                )
            }
        }
    }

    /**
     * Open Next Page
     */
    private fun openViewActivity(userName: String, userEmail: String, userImagePath: String) {
        val intent = Intent(this, ViewProfileActivity::class.java)
        intent.putExtra("userName", userName)
        intent.putExtra("userEmail", userEmail)
        intent.putExtra("userImagePath", userImagePath)
        startActivity(intent)
    }
}
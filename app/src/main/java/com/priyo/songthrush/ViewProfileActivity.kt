package com.priyo.songthrush

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.priyo.songthrush.databinding.ActivityCreateProfileBinding
import com.priyo.songthrush.databinding.ActivityViewProfileBinding
import java.io.File

class ViewProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityViewProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get intent
        val intentProfile = intent
        val userName = intentProfile.getStringExtra("userName")
        val userEmail = intentProfile.getStringExtra("userEmail")
        val userImagePath = intentProfile.getStringExtra("userImagePath")
        binding.userEmail.text = userEmail
        binding.userName.text = userName

        val f = File(userImagePath)
        val imageUri = Uri.fromFile(f)

        Log.e("TAG",imageUri?.path.toString())
        Glide.with(this).load(imageUri).circleCrop().into(binding.userImage)

        binding.nextButton.setOnClickListener {
            openYouTubeActivity()
            finishAffinity()
        }
    }

    /**
     * Open Next Page
     */
    private fun openYouTubeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
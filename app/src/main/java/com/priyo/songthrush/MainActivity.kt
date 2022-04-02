package com.priyo.songthrush

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerView
import com.priyo.songthrush.databinding.ActivityMainBinding

const val YOUTUBE_VIDEO_ID = "Evfe8GEn33w"

class MainActivity : YouTubeBaseActivity() {
    private val TAG = "YoutubeActivity"

    private val list = mutableListOf("IEF6mw7eK4s", "8CEJoCr_9UI", "344u6uK9qeQ", "3-nM3yNi3wg", "RlcY37n5j9M", "nB7nGcW9TyE")
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.youTubePlayer.initialize(getString(R.string.GOOGLE_API_KEY), onInitializedListener)
    }

    private val playbackEventListener = object: YouTubePlayer.PlaybackEventListener {
        override fun onSeekTo(p0: Int) {
        }

        override fun onBuffering(p0: Boolean) {
        }

        override fun onPlaying() {
            Toast.makeText(this@MainActivity, "Good, video is playing ok", Toast.LENGTH_SHORT).show()
        }

        override fun onStopped() {
            Toast.makeText(this@MainActivity, "Video has stopped", Toast.LENGTH_SHORT).show()
        }

        override fun onPaused() {
            Toast.makeText(this@MainActivity, "Video has paused", Toast.LENGTH_SHORT).show()
        }
    }

    private val onInitializedListener = object : YouTubePlayer.OnInitializedListener {
        override fun onInitializationSuccess(provider: YouTubePlayer.Provider?, youTubePlayer: YouTubePlayer?,
                                             wasRestored: Boolean) {
            Log.d(TAG, "onInitializationSuccess: provider is ${provider?.javaClass}")
            Log.d(TAG, "onInitializationSuccess: youTubePlayer is ${youTubePlayer?.javaClass}")
            Toast.makeText(this@MainActivity, "Initialized Youtube Player successfully", Toast.LENGTH_SHORT).show()

            youTubePlayer?.setPlayerStateChangeListener(playerStateChangeListener)
            youTubePlayer?.setPlaybackEventListener(playbackEventListener)
            

            if (!wasRestored) {
                try {
                    youTubePlayer?.cueVideos(list)
                }catch (e: Exception){
                    Toast.makeText(this@MainActivity, "Exception Occurred ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onInitializationFailure(provider: YouTubePlayer.Provider?,
                                             youTubeInitializationResult: YouTubeInitializationResult?) {
            val REQUEST_CODE = 0

            if (youTubeInitializationResult?.isUserRecoverableError == true) {
                youTubeInitializationResult.getErrorDialog(this@MainActivity, REQUEST_CODE).show()
            } else {
                val errorMessage = "There was an error initializing the YoutubePlayer ($youTubeInitializationResult)"
                Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        }

    }

    private val playerStateChangeListener = object: YouTubePlayer.PlayerStateChangeListener {
        override fun onAdStarted() {
            Toast.makeText(this@MainActivity, "Click Ad now, make the video creator rich!", Toast.LENGTH_SHORT).show()
        }

        override fun onLoading() {
        }

        override fun onVideoStarted() {
            Toast.makeText(this@MainActivity, "Video has started", Toast.LENGTH_SHORT).show()
        }

        override fun onLoaded(p0: String?) {
        }

        override fun onVideoEnded() {
            Toast.makeText(this@MainActivity, "Congratulations! You've completed another video.", Toast.LENGTH_SHORT).show()
        }

        override fun onError(p0: YouTubePlayer.ErrorReason?) {
            Toast.makeText(this@MainActivity, "Error: ${p0.toString()}", Toast.LENGTH_SHORT).show()
        }
    }
}
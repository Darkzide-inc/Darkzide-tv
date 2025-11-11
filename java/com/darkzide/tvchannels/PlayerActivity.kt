package com.darkzide.tvchannels

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView

class PlayerActivity : AppCompatActivity() {
    
    private lateinit var playerView: StyledPlayerView
    private lateinit var player: ExoPlayer
    private lateinit var channelNameText: TextView
    private lateinit var backButton: ImageButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        
        setupViews()
        setupPlayer()
        loadChannel()
    }
    
    private fun setupViews() {
        playerView = findViewById(R.id.playerView)
        channelNameText = findViewById(R.id.channelName)
        backButton = findViewById(R.id.backButton)
        
        backButton.setOnClickListener {
            finish()
        }
    }
    
    private fun setupPlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player
        
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        // Show loading indicator if needed
                    }
                    Player.STATE_READY -> {
                        // Hide loading indicator
                    }
                    Player.STATE_ENDED -> {
                        // Handle playback ended
                    }
                    Player.STATE_IDLE -> {
                        // Handle idle state
                    }
                }
            }
            
            override fun onPlayerError(error: com.google.android.exoplayer2.PlaybackException) {
                // Handle playback errors
                channelNameText.text = "Playback Error"
            }
        })
    }
    
    private fun loadChannel() {
        val channelName = intent.getStringExtra("CHANNEL_NAME") ?: "Unknown Channel"
        val channelUrl = intent.getStringExtra("CHANNEL_URL") ?: ""
        
        channelNameText.text = channelName
        
        if (channelUrl.isNotEmpty()) {
            val mediaItem = MediaItem.fromUri(channelUrl)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } else {
            channelNameText.text = "Invalid Channel URL"
        }
    }
    
    override fun onPause() {
        super.onPause()
        player.pause()
    }
    
    override fun onResume() {
        super.onResume()
        player.play()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
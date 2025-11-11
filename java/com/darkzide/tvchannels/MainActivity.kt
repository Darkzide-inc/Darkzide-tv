package com.darkzide.tvchannels

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.darkzide.tvchannels.adapter.ChannelAdapter
import com.darkzide.tvchannels.databinding.ActivityMainBinding
import com.darkzide.tvchannels.model.Channel
import com.darkzide.tvchannels.network.RetrofitClient
import com.darkzide.tvchannels.utils.M3UParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var channelAdapter: ChannelAdapter
    
    // GitHub configuration - You can change these values
    private val githubOwner = "YOUR_GITHUB_USERNAME" // Replace with your GitHub username
    private val githubRepo = "tv-playlists" // Replace with your repository name
    private val playlistPath = "channels.m3u" // Replace with your playlist file path
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Darkzide TV"
        binding.refreshButton.visibility = View.GONE

        setupRecyclerView()
        setupRefreshButton()
        loadChannels()
        setContentView(R.layout.activity_main)

        val webView = findViewById<android.webkit.WebView>(R.id.webView)
        android.webkit.WebView.setWebContentsDebuggingEnabled(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.loadUrl("file:///android_asset/index.html")
    }
    
    private fun setupRecyclerView() {
        channelAdapter = ChannelAdapter(emptyList()) { channel ->
            openPlayer(channel)
        }

        val spanCount =
            if (resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) 3 else 2

        binding.channelsRecyclerView.apply {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@MainActivity, spanCount)
            adapter = channelAdapter
            addItemDecoration(object : androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: android.graphics.Rect,
                    view: android.view.View,
                    parent: androidx.recyclerview.widget.RecyclerView,
                    state: androidx.recyclerview.widget.RecyclerView.State
                ) {
                    val space = (12 * resources.displayMetrics.density).toInt()
                    outRect.set(space, space, space, space)
                }
            })
            setHasFixedSize(true)
        }
    }
    
    private fun setupRefreshButton() {
        binding.refreshButton.setOnClickListener {
            loadChannels()
        }
    }
    
    private fun loadChannels() {
        showLoading()
        
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.gitHubService.getFileContent(
                    owner = githubOwner,
                    repo = githubRepo,
                    path = playlistPath
                )
                
                if (response.isSuccessful) {
                    val fileResponse = response.body()
                    fileResponse?.let {
                        val channels = if (it.encoding == "base64") {
                            M3UParser.parseM3UFromBase64(it.content)
                        } else {
                            M3UParser.parseM3UContent(it.content)
                        }
                        
                        withContext(Dispatchers.Main) {
                            updateChannels(channels)
                            showContent()
                        }
                    } ?: run {
                        showError("No content received")
                    }
                } else {
                    showError("Failed to fetch playlist: ${response.code()}")
                }
            } catch (e: Exception) {
                showError("Error: ${e.message}")
            }
        }
    }
    
    private fun updateChannels(channels: List<com.darkzide.tvchannels.model.Channel>) {
        channelAdapter = ChannelAdapter(channels) { channel ->
            openPlayer(channel)
        }
        binding.channelsRecyclerView.adapter = channelAdapter
        supportActionBar?.subtitle = "${channels.size} kanaler"
    }
    
    private fun openPlayer(channel: Channel) {
        val intent = Intent(this, PlayerActivity::class.java).apply {
            putExtra("CHANNEL_NAME", channel.name)
            putExtra("CHANNEL_URL", channel.url)
        }
        startActivity(intent)
    }
    
    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.channelsRecyclerView.visibility = View.GONE
        binding.errorText.visibility = View.GONE
    }
    
    private fun showContent() {
        binding.progressBar.visibility = View.GONE
        binding.channelsRecyclerView.visibility = View.VISIBLE
        binding.errorText.visibility = View.GONE
    }
    
    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.channelsRecyclerView.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.errorText.text = message
        
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
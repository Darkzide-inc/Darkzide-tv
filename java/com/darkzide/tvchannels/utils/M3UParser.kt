package com.darkzide.tvchannels.utils

import android.util.Base64
import com.darkzide.tvchannels.model.Channel

object M3UParser {
    
    fun parseM3UContent(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = content.lines()
        
        var currentChannel: Channel? = null
        
        for (line in lines) {
            val trimmedLine = line.trim()
            
            when {
                trimmedLine.startsWith("#EXTINF:") -> {
                    // Parse channel info
                    val info = parseExtInf(trimmedLine)
                    currentChannel = Channel(
                        id = info["tvg-id"] ?: "",
                        name = info["tvg-name"] ?: extractNameFromExtInf(trimmedLine),
                        logo = info["tvg-logo"] ?: "",
                        url = "", // URL will be set on next line
                        group = info["group-title"] ?: ""
                    )
                }
                trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#") && currentChannel != null -> {
                    // This is the URL line
                    currentChannel = currentChannel.copy(url = trimmedLine)
                    channels.add(currentChannel)
                    currentChannel = null
                }
            }
        }
        
        return channels
    }
    
    fun parseM3UFromBase64(base64Content: String): List<Channel> {
        return try {
            val decodedBytes = Base64.decode(base64Content, Base64.DEFAULT)
            val decodedString = String(decodedBytes)
            parseM3UContent(decodedString)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun parseExtInf(line: String): Map<String, String> {
        val info = mutableMapOf<String, String>()
        val header = line.removePrefix("#EXTINF:").trim()
        val attrsPart = header.substringBefore(",")
        val namePart = header.substringAfter(",", "").trim()

        val attributeRegex = Regex("""([A-Za-z0-9-]+)="([^"]*)"""")
        attributeRegex.findAll(attrsPart).forEach { match ->
            val (key, value) = match.destructured
            info[key] = value
        }
        if (namePart.isNotEmpty()) {
            info.putIfAbsent("tvg-name", namePart)
        }
        return info
    }

    private fun extractNameFromExtInf(line: String): String {
        return line.substringAfterLast(",").trim()
    }
}
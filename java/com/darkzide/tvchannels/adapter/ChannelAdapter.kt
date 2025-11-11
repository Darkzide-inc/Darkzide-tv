package com.darkzide.tvchannels.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.darkzide.tvchannels.R
import com.darkzide.tvchannels.model.Channel

class ChannelAdapter(
    private val channels: List<Channel>,
    private val onChannelClick: (Channel) -> Unit
) : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {

    inner class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val channelName: TextView = itemView.findViewById(R.id.channelName)
        private val channelLogo: ImageView = itemView.findViewById(R.id.channelLogo)
        private val channelGroup: TextView = itemView.findViewById(R.id.channelGroup)

        fun bind(channel: com.darkzide.tvchannels.model.Channel) {
            channelName.text = channel.name
            channelGroup.visibility = View.GONE

            if (channel.logo.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(channel.logo)
                    .placeholder(R.drawable.ic_channel_placeholder)
                    .error(R.drawable.ic_channel_placeholder)
                    .into(channelLogo)
            } else {
                channelLogo.setImageResource(R.drawable.ic_channel_placeholder)
            }

            itemView.setOnClickListener { onChannelClick(channel) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(channels[position])
    }

    override fun getItemCount(): Int = channels.size
}
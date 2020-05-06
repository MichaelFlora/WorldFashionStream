package com.flora.michael.wfcstream.view.streamerhome.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.flora.michael.wfcstream.R
import com.flora.michael.wfcstream.model.response.broadcast.BroadcastInformation
import com.flora.michael.wfcstream.view.home.HomeFragmentDirections

class ActiveChannelsListAdapter(
    private val navigationController: NavController
): RecyclerView.Adapter<ActiveChannelsListAdapter.ActiveChannelViewHolder>() {

    private var activeChannels = mutableListOf<BroadcastInformation>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveChannelViewHolder {
        return ActiveChannelViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.home_streams_list_item, parent, false) as ConstraintLayout
        )
    }

    override fun getItemCount(): Int = activeChannels.size

    override fun onBindViewHolder(holder: ActiveChannelViewHolder, position: Int) {
        holder.apply {
            val activeChannel = activeChannels[position]

            channelName.text = activeChannel.userName
            broadcastTitle.text = activeChannel.broadcastName

            setOnClickListener {
                val action = HomeFragmentDirections.actionDestinationHomeToDestinationStream(activeChannel.userId, activeChannel.userName, activeChannel.broadcastName)
                navigationController.navigate(action)
            }
        }
    }

    fun setNewChannels(activeChannels: List<BroadcastInformation>){
        this.activeChannels = activeChannels.toMutableList()
        notifyDataSetChanged()
    }

    class ActiveChannelViewHolder(private val activeChannelContainer: ConstraintLayout): RecyclerView.ViewHolder(activeChannelContainer){
        val context = activeChannelContainer.context

        val channelName: TextView by lazy { activeChannelContainer.findViewById<TextView>(R.id.home_streams_list_item_channel_name) }
        val broadcastTitle: TextView by lazy { activeChannelContainer.findViewById<TextView>(R.id.home_streams_list_item_stream_title) }

        fun setOnClickListener(onClickListener: (ConstraintLayout) -> Unit){
            activeChannelContainer.setOnClickListener {
                onClickListener(activeChannelContainer)
            }
        }
    }
}
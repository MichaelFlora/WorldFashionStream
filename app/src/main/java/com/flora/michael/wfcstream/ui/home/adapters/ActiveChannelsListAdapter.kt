package com.flora.michael.wfcstream.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.flora.michael.wfcstream.R
import com.flora.michael.wfcstream.model.response.channels.ChannelInformation
import com.flora.michael.wfcstream.ui.home.HomeFragmentDirections
import com.flora.michael.wfcstream.view.ViewersCounterView

class ActiveChannelsListAdapter(
    private val navigationController: NavController
): RecyclerView.Adapter<ActiveChannelsListAdapter.ActiveChannelViewHolder>() {

    private var activeChannels = mutableListOf<ChannelInformation>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveChannelViewHolder {
        return ActiveChannelViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.home_live_channels_list_item,
                parent,
                false
            ) as ConstraintLayout
        )
    }

    override fun getItemCount(): Int = activeChannels.size

    override fun onBindViewHolder(holder: ActiveChannelViewHolder, position: Int) {
        holder.apply {
            val activeChannel = activeChannels[position]

            channelName.text = activeChannel.userName
            broadcastName.text = activeChannel.broadcastName
            viewersCounterView.viewersCount = activeChannel.viewersCount

            setOnClickListener {
                val action = HomeFragmentDirections.actionDestinationHomeToDestinationBroadcast(activeChannel.channelId, activeChannel.userName, activeChannel.broadcastName)
                navigationController.navigate(action)
            }
        }
    }

    fun setNewChannels(activeChannels: List<ChannelInformation>){
        this.activeChannels = activeChannels.toMutableList()
        notifyDataSetChanged()
    }

    class ActiveChannelViewHolder(private val activeChannelContainer: ConstraintLayout): RecyclerView.ViewHolder(activeChannelContainer){
        val context = activeChannelContainer.context

        val channelName: TextView by lazy { activeChannelContainer.findViewById<TextView>(R.id.home_live_channels_list_item_channel_name) }
        val broadcastName: TextView by lazy { activeChannelContainer.findViewById<TextView>(R.id.home_live_channels_list_item_broadcast_name) }
        val viewersCounterView: ViewersCounterView by lazy { activeChannelContainer.findViewById<ViewersCounterView>(R.id.home_live_channels_list_item_viewers_count_view) }

        fun setOnClickListener(onClickListener: (ConstraintLayout) -> Unit){
            activeChannelContainer.setOnClickListener {
                onClickListener(activeChannelContainer)
            }
        }
    }
}
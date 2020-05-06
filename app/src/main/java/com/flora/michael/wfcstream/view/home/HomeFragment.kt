package com.flora.michael.wfcstream.view.home

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.flora.michael.wfcstream.R
import com.flora.michael.wfcstream.view.LoadableContentFragment
import com.flora.michael.wfcstream.view.streamerhome.adapters.ActiveChannelsListAdapter
import com.flora.michael.wfcstream.viewmodel.home.HomeViewModel

class HomeFragment: LoadableContentFragment(R.layout.home_fragment) {
    private val viewModel by viewModels<HomeViewModel>()

    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var absenceOfActiveChannelsTextView: TextView? = null
    private var activeChannelsRecyclerView: RecyclerView? = null
    private var absenceOfInactiveChannelsTextView: TextView? = null
    private var inactiveChannelsRecyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findAllViews()
        initializeAllViews()
        viewModel.getChannelsInformationFromServer()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navigationController) || super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    private fun findAllViews(){
        view?.run{
            swipeRefreshLayout = findViewById(R.id.home_fragment_swipe_refresh_layout)
            absenceOfActiveChannelsTextView = findViewById(R.id.home_absence_of_active_channels_message)
            activeChannelsRecyclerView = findViewById(R.id.home_active_channels_list)
            absenceOfInactiveChannelsTextView = findViewById(R.id.home_absence_of_inactive_channels_message)
            inactiveChannelsRecyclerView = findViewById(R.id.home_inactive_channels_list)
        }
    }

    private fun initializeAllViews(){
        initializeContentLoadingObservation()
        initializeSwipeRefreshLayout()
        initializeActiveChannelsRecyclerView()
        initializeInactiveChannelsObservation()
    }

    private fun initializeContentLoadingObservation(){
        viewModel.isContentLoading.observe(viewLifecycleOwner, Observer { isContentLoading ->
            when{
                isContentLoading -> showLoadingProgressBar()
                viewModel.isChannelsInformationLoaded() ->
                    hideLoadingProgressBar()
                else ->
                    hideLoadingProgressBar(withError = true)
            }
        })
    }

    private fun initializeSwipeRefreshLayout(){
        swipeRefreshLayout?.setOnRefreshListener {
            viewModel.refreshChannelsInformation()
        }

        viewModel.isRefreshing.observe(viewLifecycleOwner, Observer { isRefreshing: Boolean? ->
            if(isRefreshing == false){
                swipeRefreshLayout?.isRefreshing = false
            }
        })
    }

    private fun initializeActiveChannelsRecyclerView(){
        activeChannelsRecyclerView?.apply {
            //setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = ActiveChannelsListAdapter(navigationController)
        }

        viewModel.activeChannels.observe(viewLifecycleOwner, Observer { activeChannels ->

            (activeChannelsRecyclerView?.adapter as? ActiveChannelsListAdapter)?.setNewChannels(activeChannels ?: emptyList())

            setVisibilityForAbsenceOfActiveChannelsMessage(activeChannels?.isEmpty() == true)
        })
    }

    private fun initializeInactiveChannelsObservation(){
        //TODO: implement when needed
    }

    private fun setVisibilityForAbsenceOfActiveChannelsMessage(isVisible: Boolean = true){
        if(isVisible){
            activeChannelsRecyclerView?.visibility = View.GONE
            absenceOfActiveChannelsTextView?.visibility = View.VISIBLE
        } else{
            absenceOfActiveChannelsTextView?.visibility = View.GONE
            activeChannelsRecyclerView?.visibility = View.VISIBLE
        }
    }

    private fun setVisibilityForAbsenceOfInactiveChannelsMessage(isVisible: Boolean = true){
        if(isVisible){
            inactiveChannelsRecyclerView?.visibility = View.GONE
            absenceOfInactiveChannelsTextView?.visibility = View.VISIBLE
        } else{
            absenceOfInactiveChannelsTextView?.visibility = View.GONE
            inactiveChannelsRecyclerView?.visibility = View.VISIBLE
        }
    }

}
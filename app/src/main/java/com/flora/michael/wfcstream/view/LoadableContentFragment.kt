package com.flora.michael.wfcstream.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.flora.michael.wfcstream.R
import com.flora.michael.wfcstream.viewmodel.main.SharedViewModel

abstract class LoadableContentFragment(
    @LayoutRes
    private val  contentLayoutId: Int
): Fragment(R.layout.loadable_content_fragment) {

    protected val navigationController by lazy { findNavController() }
    protected val sharedViewModel by activityViewModels<SharedViewModel>()

    private var contentIsNotLoadedView: View? = null
    private var contentView: ViewGroup? = null
    private var progressBarContainer: ViewGroup? = null
    private var progressBar: ContentLoadingProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        context?.let{
            view?.setBackgroundColor(ContextCompat.getColor(it, R.color.colorDestinationDefaultBackground) )
        }

        findAllViews(view)

        inflater.inflate(contentLayoutId, contentView)

        return view
    }

    private fun findAllViews(view: View?){
        view?.run{
            contentIsNotLoadedView = findViewById(R.id.loadable_content_fragment_content_is_not_loaded_text_view)
            contentView = findViewById(R.id.loadable_content_fragment_content_layout)
            progressBarContainer = findViewById(R.id.loadable_content_fragment_progress_bar_container)
            progressBar = findViewById(R.id.loadable_content_fragment_progress_bar)
        }
    }

    fun showLoadingProgressBar(withHiddenContent: Boolean = false){
        if(withHiddenContent){
            setProgressBarBackgroundColorWhenContentIsHidden()
            hideContent()
        } else {
            setProgressBarBackgroundColorWhenContentIsVisible()
        }

        hideError()
        progressBarContainer?.visibility = View.VISIBLE
    }

    fun hideLoadingProgressBar(withError: Boolean = false){
        progressBarContainer?.visibility = View.INVISIBLE

        if(withError){
            hideContent()
            showError()
        } else{
            hideError()
            showContent()
        }
    }

    private fun hideContent(){
        contentView?.visibility = View.INVISIBLE
    }

    private fun showContent(){
        contentView?.visibility = View.VISIBLE
    }

    private fun hideError(){
        contentIsNotLoadedView?.visibility = View.INVISIBLE
    }

    private fun showError(){
        contentIsNotLoadedView?.visibility = View.VISIBLE
    }

    private fun setProgressBarBackgroundColorWhenContentIsHidden(){
        setProgressBarContainerBackgroundColor(R.color.colorProgressbarWithHiddenContent)
    }

    private fun setProgressBarBackgroundColorWhenContentIsVisible(){
        setProgressBarContainerBackgroundColor(R.color.colorProgressbarWithVisibleContent)
    }

    private fun setProgressBarContainerBackgroundColor(@ColorRes colorResource: Int){
        context?.let { contextNotNull ->
            val backgroundColor = ContextCompat.getColor(contextNotNull, colorResource)
            progressBarContainer?.setBackgroundColor(backgroundColor)
        }
    }
}
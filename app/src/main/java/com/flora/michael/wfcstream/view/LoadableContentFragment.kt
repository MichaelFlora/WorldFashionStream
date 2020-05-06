package com.flora.michael.wfcstream.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
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
            progressBar = findViewById(R.id.loadable_content_fragment_progress_bar)
        }
    }

    fun showLoadingProgressBar(){
        contentView?.visibility = View.INVISIBLE
        contentIsNotLoadedView?.visibility = View.INVISIBLE
        progressBar?.show()
    }

    fun hideLoadingProgressBar(withError: Boolean = false){
        progressBar?.hide()

        if(withError){
            contentIsNotLoadedView?.visibility = View.VISIBLE
        } else{
            contentView?.visibility = View.VISIBLE
        }
    }

//    protected inner class ContentLoader(
//        @IdRes private val content: Int,
//        @IdRes private val progressBarId: Int,
//        @IdRes private val errorViewId: Int? = null
//    ){
//        private var loadableContentLayout: ConstraintLayout? = view?.findViewById(content)
//        private var contentLoadingProgressBar: ContentLoadingProgressBar? = view?.findViewById(progressBarId)
//        private var errorView: View? = errorViewId?.let { view?.findViewById(it) }
//
//
//
//
//
//    }
}
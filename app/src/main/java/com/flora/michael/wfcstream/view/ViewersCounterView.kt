package com.flora.michael.wfcstream.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.flora.michael.wfcstream.R

class ViewersCounterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr){
    private val iconImageView: ImageView by lazy { findViewById<ImageView>(R.id.viewers_count_view_icon) }
    private val viewersCountTextView: TextView by lazy { findViewById<TextView>(R.id.viewers_count_view_text) }
    private val attributes: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewersCounterView)

    var viewersCount: Int = 0
        set(value){
            viewersCountTextView.text = viewersCount.toString()
            field = value
        }

    fun setIcon(@DrawableRes iconResource: Int){
        iconImageView.setImageResource(iconResource)
    }

    init{
        inflate(context, R.layout.viewers_count_view, this)

        val viewersIcon = attributes.getResourceId(R.styleable.ViewersCounterView_viewersIcon, -1)

        if(viewersIcon != -1){
            setIcon(viewersIcon)
        }

        viewersCount = attributes.getInteger(R.styleable.ViewersCounterView_viewersCount, 0)

        val viewersTint = attributes.getColor(R.styleable.ViewersCounterView_viewersTint, -1)

        if(viewersTint != -1){
            setTint(viewersTint)
        }

    }

    fun setTint(@ColorInt rgbInteger: Int){
        iconImageView.setColorFilter(rgbInteger, android.graphics.PorterDuff.Mode.SRC_IN)
        viewersCountTextView.setTextColor(rgbInteger)
    }
}
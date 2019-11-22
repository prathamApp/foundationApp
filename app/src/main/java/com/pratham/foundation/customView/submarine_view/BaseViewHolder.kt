package com.pratham.foundation.customView.submarine_view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View

/** BaseViewHolder is an abstract class for structuring the base view holder class. */
@Suppress("unused", "LeakingThis")
abstract class BaseViewHolder(private val view: View)
    : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {

    init {
        view.setOnClickListener(this)
        view.setOnLongClickListener(this)
    }

    /** binds data to the view holder class. */
    @Throws(Exception::class)
    abstract fun bindData(data: Any)

    /** gets the view of the [RecyclerView.ViewHolder]. */
    fun view(): View {
        return view
    }

    /** gets the context. */
    fun context(): Context {
        return view.context
    }
}
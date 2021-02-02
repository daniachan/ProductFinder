package com.dania.productfinder.ui.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import timber.log.Timber
import com.dania.productfinder.AppExecutors
import com.dania.productfinder.R
import com.dania.productfinder.databinding.RecordItemBinding
import com.dania.productfinder.vo.SearchResult

/**
 * A RecyclerView adapter for [SearchResult] class.
 */
class RecordListAdapter (
        private val dataBindingComponent: DataBindingComponent,
        appExecutors: AppExecutors,
        private val showFullName: Boolean,
        private val taskListener: TaskListener?,
        private val deleteListener: TaskListener?
) : DataBoundListAdapter<SearchResult, RecordItemBinding>(
        appExecutors = appExecutors,
        diffCallback = object : DiffUtil.ItemCallback<SearchResult>() {
            override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
                return oldItem.query == newItem.query
            }

            override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
                return oldItem.query == newItem.query
            }
        }
) {

    override fun createBinding(parent: ViewGroup): RecordItemBinding {
        val binding = DataBindingUtil.inflate<RecordItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.record_item,
                parent,
                false,
                dataBindingComponent
        )
        binding.root.setOnClickListener {

            binding.plpSuggest?.let {
                taskListener?.onTaskClick(it.query)
                //repoClickCallback?.invoke(it)
            }
        }

        binding.recordDeleteButton.setOnClickListener{
            binding.plpSuggest?.let {
                Timber.d("delete ${it.query}")
                deleteListener?.onTaskClick(it.query)
            }

        }

        return binding
    }

    override fun bind(binding: RecordItemBinding, item: SearchResult) {
        binding.plpSuggest = item
    }
}

interface TaskListener {
    fun onTaskClick(task: String)
}
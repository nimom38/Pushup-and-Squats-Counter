package com.example.android.getfit

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.getfit.data.Datum
import com.example.android.getfit.databinding.OuterCardBinding


class SeePastCountsAdapter : ListAdapter<Datum.OuterCard, RecyclerView.ViewHolder>(OuterCardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return OuterCardViewHolder(
            OuterCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val outer_item = getItem(position)
        (holder as OuterCardViewHolder).bind(outer_item)
    }

    class OuterCardViewHolder(
        private val binding: OuterCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.outerCardId.setOnExpandedListener { view, isExpanded ->
                if(isExpanded) {
                    binding.outerCardId.collapse()
                    binding.outerCardId.
                }
            }
        }

        fun bind(item: Datum.OuterCard) {
            binding.apply {
                outerCard = item
                executePendingBindings()
            }
        }
    }
}

private class OuterCardDiffCallback : DiffUtil.ItemCallback<Datum.OuterCard>() {

    override fun areItemsTheSame(oldItem: Datum.OuterCard, newItem: Datum.OuterCard): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Datum.OuterCard, newItem: Datum.OuterCard): Boolean {
        return oldItem == newItem
    }
}

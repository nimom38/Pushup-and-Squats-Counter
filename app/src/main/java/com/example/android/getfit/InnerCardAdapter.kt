package com.example.android.getfit

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.getfit.data.Datum
import com.example.android.getfit.databinding.InnerCardItemBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class InnerCardAdapter() : ListAdapter<Datum.InnerCard, RecyclerView.ViewHolder>(InnerItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return InnerItemViewHolder(
            InnerCardItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val inner_item = getItem(position)
        (holder as InnerItemViewHolder).bind(inner_item)
    }

    class InnerItemViewHolder(
        private val binding: InnerCardItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.delete.setOnClickListener {
                GlobalScope.launch {
                    binding.viewModel.database.delete(binding.innerCard.id)
                }
            }
        }

        fun bind(item: Datum.InnerCard) {
            binding.apply {
                innerCard = item
                executePendingBindings()
            }
        }
    }
}

private class InnerItemDiffCallback : DiffUtil.ItemCallback<Datum.InnerCard>() {

    override fun areItemsTheSame(oldItem: Datum.InnerCard, newItem: Datum.InnerCard): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Datum.InnerCard, newItem: Datum.InnerCard): Boolean {
        return oldItem == newItem
    }
}

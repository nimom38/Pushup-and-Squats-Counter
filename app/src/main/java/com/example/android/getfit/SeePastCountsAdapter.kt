package com.example.android.getfit

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.getfit.data.Datum
import com.example.android.getfit.databinding.CardListBinding


class SeePastCountsAdapter : ListAdapter<Datum.Card, RecyclerView.ViewHolder>(CardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CardViewHolder(
            CardListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        (holder as CardViewHolder).bind(item)
    }

    class CardViewHolder(
        private val binding: CardListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Datum.Card) {
            binding.apply {
                card = item
                executePendingBindings()
            }
        }
    }
}

private class CardDiffCallback : DiffUtil.ItemCallback<Datum.Card>() {

    override fun areItemsTheSame(oldItem: Datum.Card, newItem: Datum.Card): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Datum.Card, newItem: Datum.Card): Boolean {
        return oldItem == newItem
    }
}

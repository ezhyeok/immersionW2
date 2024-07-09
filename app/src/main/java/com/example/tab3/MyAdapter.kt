package com.example.tab3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tab3.databinding.ItemRvBinding

class MyAdapter(
    private val itemClickListener: ItemClickListener
) : ListAdapter<StoreItem, MyAdapter.ItemViewHolder>(StoreItemDiffCallback()) {

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRvBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    class ItemViewHolder(
        private val binding: ItemRvBinding,
        private val itemClickListener: ItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition)
            }
        }

        fun bind(item: StoreItem) {
            binding.name.text = item.name
            binding.starScore.text = item.starScore.toString()
            binding.number.text = item.num
            binding.reviewCount.text = item.reviewCount
        }
    }
}

// DiffUtil.ItemCallback를 사용하여 리스트 항목 비교
class StoreItemDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<StoreItem>() {
    override fun areItemsTheSame(oldItem: StoreItem, newItem: StoreItem): Boolean {
        return oldItem.restaurantId == newItem.restaurantId
    }

    override fun areContentsTheSame(oldItem: StoreItem, newItem: StoreItem): Boolean {
        return oldItem == newItem
    }
}
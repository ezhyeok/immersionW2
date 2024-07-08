package com.example.tab3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tab3.databinding.StoreListItemBinding

class StoreListAdapter(
    val itemClick: (StoreResponseItem) -> Unit
) : RecyclerView.Adapter<StoreListAdapter.ViewHolder>() {
    private var items: List<StoreResponseItem> = listOf()

    companion object{
        const val LIMIT = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            StoreListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        if(items.size > LIMIT){
            return 3
        }else{
            return items.size
        }
    }

    inner class ViewHolder(
        private val binding: StoreListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: StoreResponseItem) {
            binding.storeName.text = item.place_name
            binding.storeType.text = item.category_group_name
            //binding.storeRating.rating = item.rating.toFloat()
            binding.storeAddress.text = item.address_name
            binding.storeDistance.text=item.distance+"m"
            binding.root.setOnClickListener {
                itemClick(item)
            }
        }
    }

    fun setItem(items: ArrayList<StoreResponseItem>) {
        this.items = items
        notifyDataSetChanged()
    }


}
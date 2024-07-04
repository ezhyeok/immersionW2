package com.example.tab3

import android.util.Log
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.tab3.databinding.ItemImageBinding
import com.example.tab3.databinding.ItemLoadMoreBinding

class ImageAdapter(
    private val itemClickListener: ItemClickListener,
    private val imageClickListener: ImageClickListener
) : ListAdapter<ImageItems, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<ImageItems>() {
        override fun areItemsTheSame(oldItem: ImageItems, newItem: ImageItems): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ImageItems, newItem: ImageItems): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun getItemCount(): Int {
        val originSize = currentList.size
        return originSize  // + footer ("사진 불러오기..")
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_IMAGE
    }

    // ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        /*
        if(viewType==ITEM_IMAGE){
            val binding = ItemImageBinding.inflate(inflater, parent, false)
            return ImageViewHolder(binding, imageClickListener)
        }

         */

        //return ImageViewHolder(binding = )
        val binding = ItemImageBinding.inflate(inflater, parent, false)
        return ImageViewHolder(binding, imageClickListener)


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                holder.bind(currentList[position] as ImageItems.Image)
            }
            /*
            is LoadMoreViewHolder -> {
                holder.bind(itemClickListener)
            }

             */
        }
    }

    interface ItemClickListener {
        fun onLoadMoreClick()
    }

    interface ImageClickListener {
        fun onImageClick(position: Int)
    }

    companion object {
        const val ITEM_IMAGE = 0
        const val ITEM_LOAD_MORE = 1
    }
}

// various types of data (2 types)
sealed class ImageItems {
    data class Image(
        val uri: Uri
    ) : ImageItems()

    object LoadMore : ImageItems()
}

class ImageViewHolder(
    private val binding: ItemImageBinding,
    private val imageClickListener: ImageAdapter.ImageClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ImageItems.Image) {
        val transformation = MultiTransformation(
            CenterCrop(),
            RoundedCorners(50)
        )

        Glide.with(binding.root)
            .load(item.uri)
            .apply(RequestOptions().centerCrop())
            .apply(RequestOptions.bitmapTransform(transformation))
            .into(binding.previewImageView)
        binding.previewImageView.setOnClickListener {
            Log.d("ImageViewHolder", "Image clicked at position: $adapterPosition")
            imageClickListener.onImageClick(adapterPosition)
        }
    }
}


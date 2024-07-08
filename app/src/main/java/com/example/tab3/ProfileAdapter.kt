package com.example.tab3

import android.util.Log
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

class ProfileAdapter(
    private val imageClickListener: ImageClickListener
) : ListAdapter<ReviewItem, ProfileAdapter.ImageViewHolder>(
    object : DiffUtil.ItemCallback<ReviewItem>() {
        override fun areItemsTheSame(oldItem: ReviewItem, newItem: ReviewItem): Boolean {
            return oldItem.reviewId == newItem.reviewId
        }

        override fun areContentsTheSame(oldItem: ReviewItem, newItem: ReviewItem): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemImageBinding.inflate(inflater, parent, false)
        return ImageViewHolder(binding, imageClickListener)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface ImageClickListener {
        fun onImageClick(position: Int)
    }

    class ImageViewHolder(
        private val binding: ItemImageBinding,
        private val imageClickListener: ImageClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReviewItem) {
            val transformation = MultiTransformation(
                CenterCrop(),
                RoundedCorners(50)
            )

            Glide.with(binding.root)
                .load(item.reviewImg)
                .apply(RequestOptions().centerCrop())
                .apply(RequestOptions.bitmapTransform(transformation))
                .into(binding.previewImageView)

            binding.previewImageView.setOnClickListener {
                Log.d("ImageViewHolder", "Image clicked at position: $adapterPosition")
                imageClickListener.onImageClick(adapterPosition)
            }
        }
    }
}

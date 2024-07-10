package com.example.tab3

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.tab3.databinding.ProfileImageBinding

class ImageAdapter(
    private val imageClickListener: ImageClickListener
) : ListAdapter<ProfileItem, RecyclerView.ViewHolder>(
    object : DiffUtil.ItemCallback<ProfileItem>() {
        override fun areItemsTheSame(oldItem: ProfileItem, newItem: ProfileItem): Boolean {
            return oldItem.uniqueId == newItem.uniqueId
        }

        override fun areContentsTheSame(oldItem: ProfileItem, newItem: ProfileItem): Boolean {
            return oldItem == newItem
        }
    }
) {
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return ITEM_IMAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProfileImageBinding.inflate(inflater, parent, false)
        return ImageViewHolder(binding, imageClickListener, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder) {
            holder.bind(getItem(position), position == selectedPosition)
        }
    }

    interface ImageClickListener {
        fun onImageClick(position: Int)
    }
    fun setSelectedPosition(position: Int) {
        val previousPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(previousPosition)
        notifyItemChanged(selectedPosition)
    }

    companion object {
        const val ITEM_IMAGE = 0
    }
}

class ImageViewHolder(
    private val binding: ProfileImageBinding,
    private val imageClickListener: ImageAdapter.ImageClickListener,
    private val adapter: ImageAdapter
) : RecyclerView.ViewHolder(binding.root) {
    private var latestPosition: Int? = null

    fun bind(item: ProfileItem, isSelected:Boolean) {
        val transformation = MultiTransformation(
            CenterCrop(),
            RoundedCorners(100)
        )

        val borderSize = 10f // 원하는 테두리 크기
        val borderColor = R.color.red // 원하는 테두리 색상

        Glide.with(binding.root).clear(binding.previewImageView)
        val xxx=latestPosition
        if (isSelected) {
            Glide.with(binding.root)
                .load(item.profileImg)
                .apply(RequestOptions().centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .transform(BorderTransformation(borderSize, borderColor)))
                .into(binding.previewImageView)
        } else {
            Glide.with(binding.root)
                .load(item.profileImg)
                .apply(RequestOptions().centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .transform(transformation))
                .into(binding.previewImageView)
        }

        binding.previewText.text = item.uniqueId
        binding.previewImageView.setOnClickListener {
            val position = bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                Log.d("ImageViewHolder", "Image clicked at position: $position")
                (adapter as ImageAdapter).setSelectedPosition(position)
                imageClickListener.onImageClick(position)
            }
        }
    }
}

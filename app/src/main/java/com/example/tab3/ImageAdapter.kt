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
import com.example.tab3.databinding.ProfileImageBinding


//class ImageAdapter(
//    private val imageClickListener: ImageClickListener
//) : ListAdapter<ReviewItem, RecyclerView.ViewHolder>(
//    object : DiffUtil.ItemCallback<ReviewItem>() {
//        override fun areItemsTheSame(oldItem: ReviewItem, newItem: ReviewItem): Boolean {
//            return oldItem.reviewId == newItem.reviewId
//        }
//
//        override fun areContentsTheSame(oldItem: ReviewItem, newItem: ReviewItem): Boolean {
//            return oldItem == newItem
//        }
//    }
//) {
//
//    override fun getItemCount(): Int {
//        val originSize = currentList.size
//        return originSize  // + footer ("사진 불러오기..")
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return ITEM_IMAGE
//    }
//
//    // ViewHolder
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//
//        val binding = ProfileImageBinding.inflate(inflater, parent, false)
//        return ImageViewHolder(binding, imageClickListener)
//
//
//    }
//
//    //    override fun onBindViewHolder(holder:ImageViewHolder,position:Int){//holder: RecyclerView.ViewHolder, position: Int) {
//////        when (holder) {
//////            is ImageViewHolder -> {
//////                holder.bind(currentList[position] as ImageItems.Image)
//////            }
//////
//////        }
////        holder.bind(getItem(position))
////    }
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (holder is ImageViewHolder) {
//            holder.bind(getItem(position))
//        }
//    }
//
//    interface ItemClickListener {
//        fun onImageClick(position:Int)
//    }
//
//    interface ImageClickListener {
//        fun onImageClick(position: Int)
//    }
//
//    companion object {
//        const val ITEM_IMAGE = 0
//        const val ITEM_LOAD_MORE = 1
//    }
//}
//
//// various types of data (2 types)
//sealed class ImageItems {
//    data class Image(
//        val uri: Uri
//    ) : ImageItems()
//
//    object LoadMore : ImageItems()
//}
//
//class ImageViewHolder(
//    private val binding: ProfileImageBinding,
//    private val imageClickListener: ImageAdapter.ImageClickListener
//) : RecyclerView.ViewHolder(binding.root) {
//
//    fun bind(item: ReviewItem) {
//        val transformation = MultiTransformation(
//            CenterCrop(),
//            RoundedCorners(100)
//        )
//
//        Glide.with(binding.root)
//            .load(item.reviewImg)
//            .apply(RequestOptions().centerCrop())
//            .apply(RequestOptions.bitmapTransform(transformation))
//            .into(binding.previewImageView)
//        binding.previewImageView.setOnClickListener {
//            Log.d("ImageViewHolder", "Image clicked at position: $adapterPosition")
//            imageClickListener.onImageClick(adapterPosition)
//        }
//    }
//}



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

        val binding = ProfileImageBinding.inflate(inflater, parent, false)

        return ImageViewHolder(binding, imageClickListener)


    }

    //    override fun onBindViewHolder(holder:ImageViewHolder,position:Int){//holder: RecyclerView.ViewHolder, position: Int) {
////        when (holder) {
////            is ImageViewHolder -> {
////                holder.bind(currentList[position] as ImageItems.Image)
////            }
////
////        }
//        holder.bind(getItem(position))
//    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder) {
            holder.bind(getItem(position))
        }
    }

    interface ItemClickListener {
        fun onImageClick(position:Int)
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
    private val binding: ProfileImageBinding,
    private val imageClickListener: ImageAdapter.ImageClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ProfileItem) {
        val transformation = MultiTransformation(
            CenterCrop(),
            RoundedCorners(100)
        )

        Glide.with(binding.root)
            .load(item.profileImg)
            .apply(RequestOptions().centerCrop())
            .apply(RequestOptions.bitmapTransform(transformation))
            .into(binding.previewImageView)
        binding.previewText.text = item.uniqueId
        binding.previewImageView.setOnClickListener {
            Log.d("ImageViewHolder", "Image clicked at position: $adapterPosition")
            imageClickListener.onImageClick(adapterPosition)
        }
    }
}

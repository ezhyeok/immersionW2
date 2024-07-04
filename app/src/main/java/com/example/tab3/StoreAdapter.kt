
package com.example.tab3

import android.net.Uri
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Filter
import android.widget.Filterable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop

import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.tab3.StoreListAdapter.Companion.LIMIT

import com.example.tab3.databinding.StoreRvBinding
import com.example.tab3.databinding.StoreListItemBinding

class StoreAdapter(val items: MutableList<Diary>) : RecyclerView.Adapter<StoreAdapter.ViewHolder>(){

    companion object {
        private const val VIEW_TYPE_DEFAULT = 0
        private const val VIEW_TYPE_ANOTHER = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            StoreRvBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(private val binding: StoreRvBinding) : RecyclerView.ViewHolder(binding.root) {
        private val profile = binding.profile
        private val name = binding.name
        private val comment=binding.comment
        private val date=binding.date
        private val favorite = binding.favorite
        fun bind(item: Diary) {
            //profile.setImageResource(item.profile)
            val transformation = MultiTransformation(
                CenterCrop(),
                RoundedCorners(10)
            )
            val resourceId = R.drawable.ic_chinese_food
            if(item.diaryImg!=null){
                //profile.setImageResource(R.drawable.star_filled)
                Glide.with(binding.root)
                    .load(item.diaryImg)
                    .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                    .into(profile)
                //profile.setImageURI(Uri.parse(item.image))
            }else{
                Glide.with(binding.root)
                    .load(resourceId)
                    .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                    .into(profile)
            }
            //name.text = item.name
            date.text = "${item.year}.${item.month}.${item.date}"
            comment.text=item.diaryContent
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount(): Int {
        return items.size
    }


    class AddressAdapterDecoration : RecyclerView.ItemDecoration() {
        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDraw(c, parent, state)

            val paint = Paint()
            paint.color = Color.GRAY

            for (i in 0 until parent.childCount) {
                val child = parent.getChildAt(i)
                val layoutParams = child.layoutParams as RecyclerView.LayoutParams
                val top = (child.bottom + layoutParams.bottomMargin + 40).toFloat()
                val bottom = top + 1f

                val left = parent.paddingStart.toFloat()
                val right = (parent.width - parent.paddingEnd).toFloat()

                c.drawRect(left, top, right, bottom, paint)
            }
        }


        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            val offset = 40
            outRect.top = offset
            outRect.bottom = offset
        }

    }



}

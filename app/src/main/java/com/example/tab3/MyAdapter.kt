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

import com.example.tab3.databinding.ItemRvBinding

class MyAdapter(val items: MutableList<MyItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable{
    var filteredItems: MutableList<MyItem> = items.toMutableList()

    companion object {
        private const val VIEW_TYPE_DEFAULT = 0
        private const val VIEW_TYPE_ANOTHER = 1
    }

    interface NumberClick {
        fun onNumberClick(view: View, position: Int)
    }
    var numberClick: NumberClick? = null

    interface FavoriteClick {
        fun onFavoriteClick(view: View, position: Int)
    }
    var favoriteClick: FavoriteClick? = null

    interface DeleteClick {
        fun onDeleteClick(view: View, position: Int)
    }
    var deleteClick: DeleteClick? = null
    interface ImageClick {
        fun onImageClick(view: View, position: Int)
    }
    var imageClick: ImageClick? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_DEFAULT -> {
                val binding = ItemRvBinding.inflate(inflater, parent, false)
                DefaultViewHolder(binding)
            }
            VIEW_TYPE_ANOTHER -> {
                val binding = ItemRvBinding.inflate(inflater, parent, false)
                AnotherViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                //val filteredList:MutableList<MyItem>
                filteredItems = if (charString.isEmpty()) {
                    items.toMutableList()
                } else {
                    val filteredList = items.filter {
                        it.name.contains(charString, ignoreCase = true) ||
                                it.number.contains(charString, ignoreCase = true)
                    }
                    filteredList.toMutableList()
                }
                val filterResults = FilterResults()
                filterResults.values =if(filteredItems.isEmpty()){
                    filteredItems
                }else {
                    filteredItems.sortedWith(compareByDescending<MyItem> { it.isFavorite }.thenBy { it.name })
                }
                Log.d("filtereditems", "${filterResults.values}")
                filteredItems = filterResults.values as MutableList<MyItem>
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = results?.values as MutableList<MyItem>
                notifyDataSetChanged()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = filteredItems[position]
        when (holder) {
            is DefaultViewHolder -> holder.bindDefault(item)
            is AnotherViewHolder -> holder.bindAnother(item)
        }
        holder.itemView.findViewById<ImageView>(R.id.favorite).setOnClickListener {
            favoriteClick?.onFavoriteClick(it, position)
        }
        holder.itemView.findViewById<ImageView>(R.id.delete).setOnClickListener {
            deleteClick?.onDeleteClick(it, position)
        }
        holder.itemView.findViewById<TextView>(R.id.number).setOnClickListener {
            numberClick?.onNumberClick(it, position)
        }

        holder.itemView.findViewById<ImageView>(R.id.profile).setOnClickListener {
            imageClick?.onImageClick(it, position)
        }



    }

    override fun getItemViewType(position: Int): Int {
        return if (filteredItems[position].isFavorite) {
            VIEW_TYPE_DEFAULT
        } else {
            VIEW_TYPE_ANOTHER
        }
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    inner class DefaultViewHolder(private val binding: ItemRvBinding) : RecyclerView.ViewHolder(binding.root) {
        private val profile = binding.profile
        private val name = binding.name
        private val number = binding.number
        private val favorite = binding.favorite
        init {
            profile.clipToOutline = true
        }
        fun bindDefault(item: MyItem) {
            //profile.setImageResource(item.profile)
            val transformation = MultiTransformation(
                CenterCrop(),
                RoundedCorners(10)
            )
            val resourceId = when (item.selcted) {
                1 -> R.drawable.ic_person_01
                2 -> R.drawable.ic_person_02
                3 -> R.drawable.ic_person_03
                4 -> R.drawable.ic_person_04
                5 -> R.drawable.ic_person_05
                6 -> R.drawable.ic_person_06
                7 -> R.drawable.ic_person_07
                8 -> R.drawable.ic_person_08
                else -> R.drawable.ic_person // 기본 이미지
            }
            if(item.image!=null){
                //profile.setImageResource(R.drawable.star_filled)
                Glide.with(binding.root)
                    .load(Uri.parse(item.image))
                    .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                    .into(binding.profile)
                //profile.setImageURI(Uri.parse(item.image))
            }else{
                Glide.with(binding.root)
                    .load(resourceId)
                    .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                    .into(binding.profile)
            }
            name.text = item.name
            number.text = item.number
            favorite.setImageResource(if (item.isFavorite) R.drawable.star_filled else R.drawable.star_empty)
        }
    }

    inner class AnotherViewHolder(private val binding: ItemRvBinding) : RecyclerView.ViewHolder(binding.root) {
        private val profile = binding.profile
        private val name = binding.name
        //val profile: Int = R.drawable.default_profile, // 기본 이미지 리소스 ID 설정
        private val number = binding.number
        private val favorite = binding.favorite
        init {
            profile.clipToOutline = true
        }
        fun bindAnother(item: MyItem) {
            //profile.setImageResource(item.profile)
            val transformation = MultiTransformation(
                CenterCrop(),
                RoundedCorners(10)
            )
            val resourceId = when (item.selcted) {
                1 -> R.drawable.ic_person_01
                2 -> R.drawable.ic_person_02
                3 -> R.drawable.ic_person_03
                4 -> R.drawable.ic_person_04
                5 -> R.drawable.ic_person_05
                6 -> R.drawable.ic_person_06
                7 -> R.drawable.ic_person_07
                8 -> R.drawable.ic_person_08
                else -> R.drawable.ic_person // 기본 이미지
            }
            if(item.image!=null){
                //profile.setImageResource(R.drawable.star_filled)
                Glide.with(binding.root)
                    .load(Uri.parse(item.image))
                    .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                    .into(binding.profile)
                //profile.setImageURI(Uri.parse(item.image))
            }else{
                Glide.with(binding.root)
                    .load(resourceId)
                    .apply(RequestOptions().transform(MultiTransformation(CenterCrop(), CircleCrop())))
                    .into(binding.profile)
            }
            name.text = item.name
            number.text = item.number
            favorite.setImageResource(if (item.isFavorite) R.drawable.star_filled else R.drawable.star_empty)
        }
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
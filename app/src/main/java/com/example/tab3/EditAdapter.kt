/*
package com.example.intentexample

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

class EditAdapter(
    private var imagePaths: String
) : BaseAdapter() {

    private var useDrawableResources = false
    private val imageResIds = intArrayOf(
        R.drawable.default_image1,
        R.drawable.default_image2,
        R.drawable.default_image3,
        R.drawable.default_image4,
        R.drawable.default_image5,
        R.drawable.default_image6,
        R.drawable.default_image7,
        R.drawable.default_image8
    )

    override fun getCount(): Int {
        return if (useDrawableResources) imageResIds.size else imagePaths.size
    }

    override fun getItem(position: Int): Any {
        return if (useDrawableResources) imageResIds[position] else imagePaths[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.grid_item_gallery, parent, false)
        val imageView: ImageView = view.findViewById(R.id.imageViewGridItem)

        if (useDrawableResources) {
            imageView.setImageResource(imageResIds[position])
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER // Adjust scaleType for drawable resources
        } else {
            val bitmap = BitmapFactory.decodeFile(imagePaths[position])
            val rotation = getSavedRotationForImage(imagePaths[position])
            imageView.rotation = rotation
            imageView.setImageBitmap(bitmap)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP // Adjust scaleType for bitmap images
        }

        return view
    }

    private fun getSavedRotationForImage(imagePath: String): Float {
        val prefs: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val key = "Comments_${imagePath.hashCode()}_rotation"
        return prefs.getFloat(key, 0.0f)
    }
}


 */
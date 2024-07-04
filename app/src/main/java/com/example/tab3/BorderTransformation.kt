package com.example.tab3

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.security.MessageDigest

class BorderTransformation(private val borderWidth: Float, private val borderColor: Int) : BitmapTransformation() {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("BorderTransformation".toByteArray(Charsets.UTF_8))
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val circleBitmap = TransformationUtils.circleCrop(pool, toTransform, outWidth, outHeight)
        return addBorderToCircularBitmap(circleBitmap, borderWidth, borderColor)
    }

    private fun addBorderToCircularBitmap(srcBitmap: Bitmap, borderWidth: Float, borderColor: Int): Bitmap {
        val size = srcBitmap.width + borderWidth * 2
        val dstBitmap = Bitmap.createBitmap(size.toInt(), size.toInt(), Bitmap.Config.ARGB_8888)

        val canvas = Canvas(dstBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = BitmapShader(srcBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val radius = srcBitmap.width / 2f
        val center = radius + borderWidth

        // Adjust this value to change the border radius
        val borderRadiusAdjustment = 1f // Reduce the radius of the border

        canvas.drawCircle(center, center, radius, paint)

        paint.shader = null
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth

        // Adjust the radius to ensure the border fits perfectly around the image
        val borderRadius = radius * borderRadiusAdjustment + borderWidth / 2
        canvas.drawCircle(center, center, borderRadius, paint)

        return dstBitmap
    }
}

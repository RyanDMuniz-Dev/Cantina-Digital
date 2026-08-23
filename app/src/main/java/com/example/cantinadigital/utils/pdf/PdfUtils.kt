package com.example.cantinadigital.utils.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import java.text.NumberFormat
import java.util.Locale
import androidx.core.graphics.createBitmap

object PdfUtils {

    private val brazilianLocale = Locale("pt", "BR")

    fun formatCurrency(value: Double) : String {
        return NumberFormat
            .getCurrencyInstance(brazilianLocale)
            .format(value)
    }

    fun drawableToBitmap(
        context: Context,
        @DrawableRes drawableRes: Int,
        width: Int,
        height: Int
    ) : Bitmap {

        val drawable: Drawable = ContextCompat.getDrawable(
            context,
            drawableRes
        ) ?: error("Drawable não encontrado: $drawableRes")

        drawable.setBounds(
            0,
            0,
            width,
            height
        )

        val bitmap = createBitmap(width, height)

        val canvas = Canvas(bitmap)
        drawable.draw(canvas)

        return bitmap

    }

}
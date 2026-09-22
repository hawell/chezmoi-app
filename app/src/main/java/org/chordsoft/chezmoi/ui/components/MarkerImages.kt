package org.chordsoft.chezmoi.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap

fun createMarkerBitmap(
    context: Context,
    @DrawableRes pinRes: Int,
    @DrawableRes iconRes: Int,
    iconColor: Int = Color.BLACK
): Bitmap {
    val pin = ContextCompat.getDrawable(context, pinRes)
        ?: error("Pin drawable not found")

    val icon = ContextCompat.getDrawable(context, iconRes)
        ?: error("Icon drawable not found")

    val width = pin.intrinsicWidth
    val height = pin.intrinsicHeight

    val bitmap = createBitmap(width, height)

    val canvas = Canvas(bitmap)

    // Draw the pin
    pin.setBounds(0, 0, width, height)
    pin.draw(canvas)

    // Draw the icon inside the upper part
    val iconSize = (width * 0.5f).toInt()

    val left = (width - iconSize) / 2
    val top = (height * 0.2f).toInt()

    icon.setBounds(
        left,
        top,
        left + iconSize,
        top + iconSize
    )

    icon.colorFilter = PorterDuffColorFilter(
        iconColor,
        PorterDuff.Mode.SRC_IN
    )
    icon.draw(canvas)

    return bitmap
}
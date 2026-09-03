package org.chordsoft.chezmoi.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import androidx.core.graphics.createBitmap

@Composable
fun rememberCustomMarkerIcon(
    @DrawableRes iconRes: Int,
    text: String,
    backgroundColor: Color = Color.White,
    iconColor: Color = Color.Black,
    textColor: Color = Color.Black,
    borderColor: Color = Color.DarkGray,
): BitmapDescriptor {
    val context = LocalContext.current

    return remember(
        iconRes,
        text,
        backgroundColor,
        iconColor,
        textColor,
        borderColor
    ) {
        createCustomMarkerBitmap(
            context = context,
            iconRes = iconRes,
            text = text,
            backgroundColor = backgroundColor,
            iconColor = iconColor,
            textColor = textColor,
            borderColor = borderColor
        )
    }
}

private fun createCustomMarkerBitmap(
    context: Context,
    @DrawableRes iconRes: Int,
    text: String,
    backgroundColor: Color,
    iconColor: Color,
    textColor: Color,
    borderColor: Color,
): BitmapDescriptor {

    val density = context.resources.displayMetrics.density

    fun dp(value: Float): Float = value * density

    // Dimensions
    val horizontalPadding = dp(2f)
    val iconSize = dp(12f)
    val iconTextGap = dp(2f)

    val rectangleHeight = dp(24f)
    val totalHeight = dp(30f)
    val cornerRadius = dp(4f)

    /*
     * Text paint
     */
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor.toArgb()
        textSize = dp(10f)
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.LEFT
    }

    /*
     * Measure text
     */
    val textWidth = textPaint.measureText(text)

    /*
     * Calculate width dynamically:
     *
     * padding
     * + icon
     * + gap
     * + text
     * + padding
     */
    val width =
        horizontalPadding +
                iconSize +
                iconTextGap +
                textWidth +
                horizontalPadding

    /*
     * Bitmap
     */
    val bitmap = createBitmap(width.toInt(), totalHeight.toInt())

    val canvas = Canvas(bitmap)

    /*
     * Rectangle
     */
    val rectangle = RectF(
        0f,
        0f,
        width,
        rectangleHeight
    )

    val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = backgroundColor.toArgb()
        style = Paint.Style.FILL
    }

    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = borderColor.toArgb()
        style = Paint.Style.STROKE
        strokeWidth = dp(1.5f)
    }

    canvas.drawRoundRect(
        rectangle,
        cornerRadius,
        cornerRadius,
        backgroundPaint
    )

    canvas.drawRoundRect(
        rectangle,
        cornerRadius,
        cornerRadius,
        borderPaint
    )

    /*
     * Icon
     */
    val drawable = ContextCompat.getDrawable(
        context,
        iconRes
    ) ?: error("Drawable resource $iconRes could not be loaded")

    DrawableCompat.setTint(
        drawable,
        iconColor.toArgb()
    )

    val iconLeft = horizontalPadding
    val iconTop = (rectangleHeight - iconSize) / 2f

    drawable.setBounds(
        iconLeft.toInt(),
        iconTop.toInt(),
        (iconLeft + iconSize).toInt(),
        (iconTop + iconSize).toInt()
    )

    drawable.draw(canvas)

    /*
     * Text
     */
    val textX =
        horizontalPadding +
                iconSize +
                iconTextGap

    val textY =
        rectangleHeight / 2f -
                (textPaint.ascent() + textPaint.descent()) / 2f

    canvas.drawText(
        text,
        textX,
        textY,
        textPaint
    )

    /*
     * Line attached to bottom
     */
    val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = borderColor.toArgb()
        style = Paint.Style.STROKE
        strokeWidth = dp(2f)
    }

    val centerX = width / 2f

    canvas.drawLine(
        centerX,
        rectangleHeight,
        centerX,
        totalHeight,
        linePaint
    )

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
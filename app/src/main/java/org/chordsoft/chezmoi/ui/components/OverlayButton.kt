package org.chordsoft.chezmoi.ui.components

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun OverlayButton(
    modifier: Modifier = Modifier,
    icon: Int,
    onClick: () -> Unit
) {
    val size = 48.dp
    val color = Color.White.copy(alpha = 0.9f)
    val shape = CircleShape
    val contentPadding = PaddingValues(size / 6)
    val interactionSource = remember { MutableInteractionSource() }
    val shadowElevation = 0.dp
    Box(
        modifier = modifier
            .requiredSize(size)
            .aspectRatio(1f)
            .shadow(shadowElevation, shape, clip = false)
            .background(color, shape)
            .clip(shape)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = Role.Button,
            ) {
                onClick()
            }
            .padding(contentPadding),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = null
        )
    }
}
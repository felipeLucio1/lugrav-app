package com.felipelucio.lugrav.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.felipelucio.lugrav.R
import com.felipelucio.lugrav.UserTag
import com.felipelucio.lugrav.ui.theme.tagColorFromHex

@Composable
fun TagBadge(
    tag: UserTag,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = remember(tag.colorHex) { tagColorFromHex(tag.colorHex) }
    val contentColor = if (backgroundColor.luminance() > 0.5f) Color.Black else Color.White
    val shape = RoundedCornerShape(percent = 40)
    val border = if (isSelected) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    }
    val interactiveModifier = onClick?.let { clickAction ->
        val description = stringResource(
            if (isSelected) {
                R.string.tag_selected_content_description
            } else {
                R.string.tag_not_selected_content_description
            },
            tag.name
        )
        Modifier
            .clickable(onClick = clickAction)
            .semantics { contentDescription = description }
    } ?: Modifier

    Surface(
        modifier = modifier
            .clip(shape)
            .then(interactiveModifier),
        shape = shape,
        color = backgroundColor,
        contentColor = contentColor,
        border = border
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = contentColor
                )
            }
            Text(
                text = tag.name,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor
            )
        }
    }
}

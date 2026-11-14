package com.felipelucio.lugrav.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.felipelucio.lugrav.R
import com.felipelucio.lugrav.UserTag
import com.felipelucio.lugrav.ui.theme.Slate
import com.felipelucio.lugrav.ui.theme.tagColorFromHex
import com.felipelucio.lugrav.ui.theme.tagContainerFromHex

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TagBadge(
    tag: UserTag,
    isSelected: Boolean = false,
    isDeletionMode: Boolean = false,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val storedColor = remember(tag.colorHex) { tagColorFromHex(tag.colorHex) }
    val accentColor = if (storedColor.luminance() > 0.9f) Slate else storedColor
    val containerColor = remember(tag.colorHex) { tagContainerFromHex(tag.colorHex) }
    val shape = RoundedCornerShape(percent = 50)
    val border = if (isSelected) {
        BorderStroke(2.dp, accentColor)
    } else {
        BorderStroke(1.dp, accentColor.copy(alpha = 0.18f))
    }
    val description = stringResource(
        if (isSelected) {
            R.string.tag_selected_content_description
        } else {
            R.string.tag_not_selected_content_description
        },
        tag.name
    )
    val deleteDescription = stringResource(R.string.tag_delete_content_description, tag.name)
    val interactiveModifier = if (onClick != null || onLongClick != null) {
        Modifier
            .combinedClickable(
                onClick = { onClick?.invoke() },
                onLongClick = if (isDeletionMode) null else onLongClick
            )
            .semantics { contentDescription = description }
    } else {
        Modifier
    }

    Surface(
        modifier = modifier
            .clip(shape)
            .then(interactiveModifier),
        shape = shape,
        color = containerColor,
        contentColor = accentColor,
        border = border
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isDeletionMode && onDeleteClick != null) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onDeleteClick)
                        .semantics { contentDescription = deleteDescription },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Text(
                text = tag.name,
                style = MaterialTheme.typography.labelLarge,
                color = accentColor
            )
        }
    }
}

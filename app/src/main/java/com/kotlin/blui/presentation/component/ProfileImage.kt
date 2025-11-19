package com.kotlin.blui.presentation.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kotlin.blui.ui.theme.BluiTheme

@Composable
fun ProfileImage(
    imageUrl: String? = null,
    imageResId: Int? = null,
    size: Dp = 180.dp,
    borderWidth: Dp = 6.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            !imageUrl.isNullOrEmpty() -> {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(size - borderWidth * 2)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            imageResId != null -> {
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(size - borderWidth * 2)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            else -> {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Profile Picture",
                    modifier = Modifier.size(size - borderWidth * 4),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileImagePreview() {
    BluiTheme {
        ProfileImage()
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileImageSmallPreview() {
    BluiTheme {
        ProfileImage(size = 80.dp)
    }
}

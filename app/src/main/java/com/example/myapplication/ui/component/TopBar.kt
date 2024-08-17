package com.example.myapplication.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun TopBar(
    topPadding: Dp,
    name: String,
    welcomeText: String
) {
    Row(
        modifier = Modifier.padding(top = topPadding + 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.video),
            contentDescription = "",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Row(
            modifier = Modifier.padding(start = 12.dp)
        ) {
            Text(
                text = welcomeText,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                modifier = Modifier.alpha(0.48f)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }
    }
}
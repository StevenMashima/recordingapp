package com.example.myapplication.ui.component

import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged

@Composable
fun Background(
    screenWidth: MutableState<Int>,
    screenHeight: MutableState<Int>
) {
    var targetValue = remember { mutableStateOf(0.0f) }

    val animatedFloat = animateFloatAsState(
        targetValue = targetValue.value,
        animationSpec = tween(durationMillis = 2000, easing = EaseOut,)
    )

    LaunchedEffect(Unit) {
        targetValue.value = 1f
    }

    val infiniteTransition = rememberInfiniteTransition()

    val animatedFloatParallax = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = InfiniteRepeatableSpec(
            animation = keyframes {
                durationMillis = 10000
                200f at 5000
                0f at 10000
            },
            repeatMode = RepeatMode.Restart
        ), label = "brush"
    )

    val animatedFloatParallax2 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = InfiniteRepeatableSpec(
            animation = keyframes {
                durationMillis = 8000
                300f at 4000
                0f at 8000
            },
            repeatMode = RepeatMode.Restart
        ), label = "brush"
    )

    val animatedFloatParallax3 = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = InfiniteRepeatableSpec(
            animation = keyframes {
                durationMillis = 8000
                250f at 4000
                0f at 8000
            },
            repeatMode = RepeatMode.Restart
        ), label = "brush"
    )

    val brush1 = Brush.radialGradient(
        listOf(Color(0xFF2c2e65), Color.Transparent),
        radius = if (screenWidth.value > 1) screenWidth.value.toFloat() else 1f,
        center = Offset(0f, (screenWidth.value.toFloat() / 4 * -1) + animatedFloatParallax.value)
    )

    val brush2 = Brush.radialGradient(
        listOf(Color(0xFF2c2e65), Color.Transparent),
        radius = if (screenWidth.value > 1) screenWidth.value.toFloat() / 2.5f else 1f,
        center = Offset(screenWidth.value.toFloat() * 1.1f, (screenHeight.value.toFloat() / 3f) + animatedFloatParallax2.value)
    )

    val brush3 = Brush.radialGradient(
        listOf(Color(0xFF2c2e65), Color.Transparent),
        radius = if (screenWidth.value > 1) screenWidth.value.toFloat() * 1.5f else 1f,
        center = Offset(0f, (screenHeight.value.toFloat() * 1.4f) + animatedFloatParallax3.value)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged {
                screenWidth.value = it.width
                screenHeight.value = it.height
            }
            .drawBehind {
                drawRect(Color(0xFF191a23))
                drawRect(
                    brush1,
                    size = Size(
                        screenWidth.value.toFloat(),
                        screenHeight.value.toFloat()
                    ),
                    alpha = animatedFloat.value
                )
                drawRect(
                    brush2,
                    size = Size(
                        screenWidth.value.toFloat(),
                        screenHeight.value.toFloat()
                    ),
                    alpha = animatedFloat.value
                )
                drawRect(
                    brush3,
                    size = Size(
                        screenWidth.value.toFloat(),
                        screenHeight.value.toFloat()
                    ),
                    alpha = animatedFloat.value
                )
            }
    )
}
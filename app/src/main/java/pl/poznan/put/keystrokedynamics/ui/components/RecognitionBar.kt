package pl.poznan.put.keystrokedynamics.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RecognitionBar(progress: Float, size: Int = 100) {
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 1000) // animation time
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(size.dp),
            progress = animatedProgress.value,
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = (size * 0.08).dp // scale with size
        )
        Text(
            text = "${(animatedProgress.value * 100).toInt()}%",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
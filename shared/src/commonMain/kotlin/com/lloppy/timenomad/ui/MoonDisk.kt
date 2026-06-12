package com.lloppy.timenomad.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun MoonDisk(
    illumination: Double,
    waxing: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
) {
    val lit = MaterialTheme.colorScheme.primary
    val night = MaterialTheme.colorScheme.surfaceVariant
    val edge = MaterialTheme.colorScheme.outline
    Canvas(modifier = modifier.size(size)) {
        val r = this.size.minDimension / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val topLeft = Offset(center.x - r, center.y - r)
        val box = Size(2 * r, 2 * r)

        drawCircle(night, r, center)
        val startAngle = if (waxing) 270f else 90f
        drawArc(lit, startAngle, 180f, useCenter = true, topLeft = topLeft, size = box)

        val hw = (r * abs(1.0 - 2.0 * illumination)).toFloat()
        val termColor = if (illumination < 0.5) night else lit
        drawOval(termColor, topLeft = Offset(center.x - hw, center.y - r), size = Size(2 * hw, 2 * r))

        drawCircle(edge, r, center, style = Stroke(width = 1.5f))
    }
}

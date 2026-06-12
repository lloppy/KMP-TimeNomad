package com.lloppy.timenomad.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.lloppy.timenomad.astro.model.Chart
import com.lloppy.timenomad.astro.model.ZodiacSign
import com.lloppy.timenomad.theme.AstroColors
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val DEG2RAD = kotlin.math.PI / 180.0

/**
 * Круговая астрологическая карта: кольца знаков и домов, глифы планет, линии аспектов.
 * Ориентация: Асцендент слева, долгота растёт против часовой стрелки.
 */
@Composable
fun ChartWheel(chart: Chart, modifier: Modifier = Modifier) {
    val measurer = rememberTextMeasurer()
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val outline = MaterialTheme.colorScheme.outline
    val faint = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier.fillMaxWidth().aspectRatio(1f)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val r = min(size.width, size.height) / 2f - 6f
        val signOuter = r
        val signInner = r * 0.82f
        val houseInner = r * 0.50f
        val planetRing = r * 0.70f
        val aspectR = r * 0.48f

        drawCircle(outline, r, center, style = Stroke(width = 2f))
        drawCircle(outline, signInner, center, style = Stroke(width = 1.5f))
        drawCircle(faint, houseInner, center, style = Stroke(width = 1f))
        drawCircle(faint, aspectR, center, style = Stroke(width = 1f))

        for (i in 0 until 12) {
            val boundary = i * 30.0
            val p1 = pointOn(center, signInner, boundary, chart.ascendant)
            val p2 = pointOn(center, signOuter, boundary, chart.ascendant)
            drawLine(outline, p1, p2, strokeWidth = 1.5f)

            val sign = ZodiacSign.entries[i]
            val glyphPos = pointOn(center, (signInner + signOuter) / 2f, boundary + 15.0, chart.ascendant)
            drawGlyph(measurer, sign.glyph, glyphPos, AstroColors.element(sign.element), 20f)
        }

        chart.houses.forEach { house ->
            val a = pointOn(center, houseInner, house.cusp, chart.ascendant)
            val b = pointOn(center, signInner, house.cusp, chart.ascendant)
            val angular = house.number == 1 || house.number == 10
            drawLine(if (angular) onSurface else faint, a, b, strokeWidth = if (angular) 2f else 1f)
            val numPos = pointOn(center, houseInner * 1.12f, house.cusp + 2.0, chart.ascendant)
            drawGlyph(measurer, house.number.toString(), numPos, onSurfaceVariant, 10f)
        }

        chart.aspects.forEach { hit ->
            val a = chart.position(hit.first) ?: return@forEach
            val b = chart.position(hit.second) ?: return@forEach
            val pa = pointOn(center, aspectR, a.longitude, chart.ascendant)
            val pb = pointOn(center, aspectR, b.longitude, chart.ascendant)
            drawLine(AstroColors.aspect(hit.type).copy(alpha = 0.7f), pa, pb, strokeWidth = 1.4f)
        }

        chart.positions.forEach { pos ->
            val tick1 = pointOn(center, signInner, pos.longitude, chart.ascendant)
            val tick2 = pointOn(center, planetRing * 1.07f, pos.longitude, chart.ascendant)
            drawLine(AstroColors.planet(pos.body).copy(alpha = 0.5f), tick1, tick2, strokeWidth = 1f)
            val glyphPos = pointOn(center, planetRing, pos.longitude, chart.ascendant)
            drawGlyph(measurer, pos.body.glyph, glyphPos, AstroColors.planet(pos.body), 18f)
        }
    }
}

private fun pointOn(center: Offset, radius: Float, longitude: Double, ascendant: Double): Offset {
    val phi = (180.0 + (longitude - ascendant)) * DEG2RAD
    return Offset(
        x = center.x + radius * cos(phi).toFloat(),
        y = center.y - radius * sin(phi).toFloat(),
    )
}

private fun DrawScope.drawGlyph(
    measurer: TextMeasurer,
    text: String,
    pos: Offset,
    color: Color,
    sizeSp: Float,
) {
    val layout = measurer.measure(text, style = TextStyle(color = color, fontSize = sizeSp.sp))
    drawText(
        layout,
        topLeft = Offset(pos.x - layout.size.width / 2f, pos.y - layout.size.height / 2f),
    )
}

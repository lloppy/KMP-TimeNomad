package com.lloppy.timenomad.screens.planetaryhours

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lloppy.timenomad.astro.planetaryhours.PlanetaryHour
import com.lloppy.timenomad.theme.AstroColors
import com.lloppy.timenomad.ui.AppBackground
import com.lloppy.timenomad.ui.CircleIconButton
import com.lloppy.timenomad.ui.ScreenHeader
import com.lloppy.timenomad.ui.SectionCard
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PlanetaryHoursScreen(viewModel: PlanetaryHoursViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        AppBackground()
        Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
            ScreenHeader(
                title = "Планетарные часы",
                subtitle = state.locationLabel,
                action = {
                    CircleIconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Обновить")
                    }
                },
            )
            val result = state.result
            if (result == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Недоступно для этой широты/даты (полярный день или ночь).",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                return@Column
            }

            SectionCard(Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Управитель дня: ${result.dayRuler.glyph} ${result.dayRuler.displayName}",
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    result.current?.let { cur ->
                        Text("Текущий час: ${cur.ruler.glyph} ${cur.ruler.displayName}" +
                            if (cur.daytime) " (день)" else " (ночь)",
                            color = MaterialTheme.colorScheme.primary, fontSize = 15.sp,
                            modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(result.hours, key = { it.index }) { hour ->
                    HourRow(hour, highlighted = hour.index == result.currentIndex)
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun HourRow(hour: PlanetaryHour, highlighted: Boolean) {
    val bg = if (highlighted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(bg).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(hour.ruler.glyph, fontSize = 18.sp, color = AstroColors.planet(hour.ruler),
            modifier = Modifier.width(30.dp))
        Text(hour.ruler.displayName, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
        Text("${time(hour.startUtcMillis)}–${time(hour.endUtcMillis)}",
            fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun time(epochMillis: Long): String {
    val dt = Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())
    fun p(v: Int) = if (v < 10) "0$v" else "$v"
    return "${p(dt.hour)}:${p(dt.minute)}"
}

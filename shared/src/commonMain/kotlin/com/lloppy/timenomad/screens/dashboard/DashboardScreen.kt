package com.lloppy.timenomad.screens.dashboard

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lloppy.timenomad.astro.format.AstroFormat
import com.lloppy.timenomad.theme.AstroColors
import com.lloppy.timenomad.ui.AppBackground
import com.lloppy.timenomad.ui.Chip
import com.lloppy.timenomad.ui.ChartWheel
import com.lloppy.timenomad.ui.CircleIconButton
import com.lloppy.timenomad.ui.MoonDisk
import com.lloppy.timenomad.ui.ScreenHeader
import com.lloppy.timenomad.ui.SectionCard
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    onOpenSky: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        AppBackground()
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ScreenHeader(
                title = "Небо сейчас",
                subtitle = "${state.locationLabel} • ${state.momentLabel}",
                action = {
                    CircleIconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Обновить")
                    }
                },
            )

            if (state.loading || state.chart == null) {
                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            val chart = state.chart!!
            state.moonPhase?.let { phase ->
                SectionCard(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        MoonDisk(phase.illumination, phase.waxing)
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(phase.name.displayName, fontWeight = FontWeight.Bold, fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface)
                            Text("Освещённость ${(phase.illumination * 100).roundToInt()}%",
                                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            chart.position(com.lloppy.timenomad.astro.model.CelestialBody.MOON)?.let {
                                Text("Луна: ${AstroFormat.position(it)}",
                                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            SectionCard(Modifier.fillMaxWidth(), onClick = onOpenSky) {
                Column(Modifier.padding(16.dp)) {
                    Text("Карта неба • ${state.zodiacLabel}", fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(12.dp))
                    ChartWheel(chart, Modifier.fillMaxWidth())
                }
            }

            SectionCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Положения", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    chart.positions.forEach { pos ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(pos.body.glyph, fontSize = 18.sp, color = AstroColors.planet(pos.body),
                                modifier = Modifier.width(28.dp))
                            Text(pos.body.displayName, modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface)
                            Text(AstroFormat.position(pos), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            if (state.retrogrades.isNotEmpty()) {
                SectionCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Ретроградные планеты", fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            state.retrogrades.forEach { Chip("${it.body.glyph} ${it.body.displayName}") }
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

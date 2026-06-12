package com.lloppy.timenomad.screens.transits

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
import com.lloppy.timenomad.ui.ChartWheel
import com.lloppy.timenomad.ui.CircleIconButton
import com.lloppy.timenomad.ui.ScreenHeader
import com.lloppy.timenomad.ui.SectionCard
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TransitsScreen(
    profileId: String,
    onBack: () -> Unit,
    viewModel: TransitsViewModel = koinViewModel { parametersOf(profileId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val natal = state.natal
    val transit = state.transit

    Box(Modifier.fillMaxSize()) {
        AppBackground()
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ScreenHeader(
                title = "Транзиты",
                subtitle = "${state.title} • ${state.momentLabel}",
                onBack = onBack,
                action = {
                    CircleIconButton(onClick = viewModel::refresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Обновить")
                    }
                },
            )

            if (natal == null || transit == null) {
                Spacer(Modifier.height(40.dp)); return@Column
            }

            SectionCard(Modifier.fillMaxWidth()) {
                Box(Modifier.padding(14.dp)) { ChartWheel(natal, Modifier.fillMaxWidth()) }
            }

            SectionCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Транзитные планеты сейчас", fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                    transit.positions.forEach { pos ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(pos.body.glyph, fontSize = 18.sp, color = AstroColors.planet(pos.body),
                                modifier = Modifier.width(28.dp))
                            Text(pos.body.displayName, Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface)
                            Text("дом ${natal.houseOf(pos.longitude)}", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(56.dp))
                            Text(AstroFormat.position(pos), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            SectionCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Транзитные аспекты к натальной карте", fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                    if (transit.aspects.isEmpty()) {
                        Text("Нет мажорных аспектов в пределах орбиса",
                            fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    transit.aspects.forEach { a ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("${a.transiting.glyph} ${a.type.glyph} ${a.natal.glyph}",
                                fontSize = 16.sp, color = AstroColors.aspect(a.type),
                                modifier = Modifier.width(72.dp))
                            Text("${a.transiting.displayName} → натал ${a.natal.displayName}",
                                Modifier.weight(1f), fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface)
                            Text("${formatOrb(a.orb)}${if (a.applying) " ↑" else " ↓"}",
                                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            SectionCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Пиковые дни — Луна на натальной точке", fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                    if (state.peakDays.isEmpty()) {
                        Text("Нет соединений в ближайшие 30 дней",
                            fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    state.peakDays.forEach { c ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("${c.transiting.glyph} ☌ ${c.natal.glyph}", fontSize = 16.sp,
                                color = AstroColors.planet(c.natal), modifier = Modifier.width(64.dp))
                            Text("Луна ☌ натал ${c.natal.displayName}", Modifier.weight(1f),
                                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(TransitsViewModel.formatTime(c.momentUtcMillis, withDate = true),
                                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            SectionCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Ближайшие смены знака Луны", fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                    state.moonIngresses.forEach { ing ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(ing.sign.glyph, fontSize = 16.sp,
                                color = AstroColors.element(ing.sign.element), modifier = Modifier.width(36.dp))
                            Text("Луна → ${ing.sign.displayName}", Modifier.weight(1f),
                                fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(TransitsViewModel.formatTime(ing.momentUtcMillis, withDate = true),
                                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

private fun formatOrb(orb: Double): String {
    val d = orb.toInt()
    val m = ((orb - d) * 60).toInt()
    return "${d}°${if (m < 10) "0$m" else "$m"}′"
}

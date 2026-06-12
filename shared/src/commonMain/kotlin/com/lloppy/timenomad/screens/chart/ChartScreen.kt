package com.lloppy.timenomad.screens.chart

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
import androidx.compose.material.icons.filled.Edit
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
fun ChartScreen(
    profileId: String?,
    onBack: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onTransits: (() -> Unit)? = null,
    viewModel: ChartViewModel = koinViewModel { parametersOf(profileId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val chart = state.chart

    Box(Modifier.fillMaxSize()) {
        AppBackground()
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ScreenHeader(
                title = state.title,
                subtitle = state.subtitle,
                onBack = onBack,
                action = onEdit?.let {
                    {
                        CircleIconButton(onClick = it) {
                            Icon(Icons.Filled.Edit, contentDescription = "Редактировать профиль")
                        }
                    }
                },
            )

            if (chart == null) {
                Spacer(Modifier.height(40.dp)); return@Column
            }

            SectionCard(Modifier.fillMaxWidth()) {
                Box(Modifier.padding(14.dp)) { ChartWheel(chart, Modifier.fillMaxWidth()) }
            }

            if (onTransits != null) {
                SectionCard(Modifier.fillMaxWidth(), onClick = onTransits) {
                    Text(
                        "Транзиты сейчас →",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }

            SectionCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Планеты и дома", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    chart.positions.forEach { pos ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(pos.body.glyph, fontSize = 18.sp, color = AstroColors.planet(pos.body),
                                modifier = Modifier.width(28.dp))
                            Text(pos.body.displayName, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
                            Text("дом ${chart.houseOf(pos.longitude)}", fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(56.dp))
                            Text(AstroFormat.position(pos), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text("ASC ${AstroFormat.longitude(chart.ascendant)}   MC ${AstroFormat.longitude(chart.midheaven)}",
                        fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                }
            }

            SectionCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Аспекты", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    if (chart.aspects.isEmpty()) {
                        Text("Нет мажорных аспектов в пределах орбиса",
                            fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    chart.aspects.forEach { a ->
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("${a.first.glyph} ${a.type.glyph} ${a.second.glyph}",
                                fontSize = 16.sp, color = AstroColors.aspect(a.type), modifier = Modifier.weight(1f))
                            Text("${a.type.displayName} • орб ${formatOrb(a.orb)}${if (a.applying) " ↑" else " ↓"}",
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

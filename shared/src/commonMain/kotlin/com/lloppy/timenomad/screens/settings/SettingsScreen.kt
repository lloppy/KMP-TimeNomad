package com.lloppy.timenomad.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lloppy.timenomad.astro.model.Ayanamsha
import com.lloppy.timenomad.astro.model.HouseSystem
import com.lloppy.timenomad.astro.model.ZodiacMode
import com.lloppy.timenomad.settings.ThemeMode
import com.lloppy.timenomad.ui.AppBackground
import com.lloppy.timenomad.ui.ScreenHeader
import com.lloppy.timenomad.ui.SectionCard
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val sidereal = state.astro.zodiacMode is ZodiacMode.Sidereal

    Box(Modifier.fillMaxSize()) {
        AppBackground()
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ScreenHeader(title = "Настройки")

            card("Тема") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    chip("Системная", state.themeMode == ThemeMode.SYSTEM) { viewModel.setTheme(ThemeMode.SYSTEM) }
                    chip("Тёмная", state.themeMode == ThemeMode.DARK) { viewModel.setTheme(ThemeMode.DARK) }
                    chip("Светлая", state.themeMode == ThemeMode.LIGHT) { viewModel.setTheme(ThemeMode.LIGHT) }
                }
            }

            card("Зодиак") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    chip("Тропический", !sidereal) { viewModel.setTropical() }
                    chip("Сидерический", sidereal) { viewModel.setSidereal(viewModel.currentAyanamsha()) }
                }
                if (sidereal) {
                    Spacer(Modifier.height(8.dp))
                    val ayan = (state.astro.zodiacMode as ZodiacMode.Sidereal).ayanamsha
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Ayanamsha.entries.forEach { a ->
                            chip(a.displayName, ayan == a) { viewModel.setSidereal(a) }
                        }
                    }
                }
            }

            card("Система домов") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    HouseSystem.entries.forEach { hs ->
                        chip(hs.displayName, state.astro.houseSystem == hs) { viewModel.setHouseSystem(hs) }
                    }
                }
            }

            HomeLocationCard(
                label = state.astro.homeLabel,
                lat = state.astro.homeLatitude,
                lon = state.astro.homeLongitude,
                onSave = viewModel::setHome,
            )

            Text(
                "Расчёты выполняются на устройстве (офлайн-эфемериды). Точность — единицы угловых минут; " +
                    "Placidus/Koch на высоких широтах используют деление Порфирия.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HomeLocationCard(
    label: String,
    lat: Double,
    lon: Double,
    onSave: (Double, Double, String) -> Unit,
) {
    var l by remember(label) { mutableStateOf(label) }
    var la by remember(lat) { mutableStateOf(lat.toString()) }
    var lo by remember(lon) { mutableStateOf(lon.toString()) }
    card("Домашняя локация (для карты неба)") {
        OutlinedTextField(l, { l = it }, label = { Text("Город") }, singleLine = true,
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(la, { la = it }, label = { Text("Широта") }, singleLine = true,
                modifier = Modifier.weight(1f))
            OutlinedTextField(lo, { lo = it }, label = { Text("Долгота E+") }, singleLine = true,
                modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        FilterChip(
            selected = false,
            onClick = { onSave(la.toDoubleOrNull() ?: 0.0, lo.toDoubleOrNull() ?: 0.0, l) },
            label = { Text("Сохранить локацию") },
        )
    }
}

@Composable
private fun card(title: String, content: @Composable () -> Unit) {
    SectionCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 10.dp))
            content()
        }
    }
}

@Composable
private fun chip(text: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(text) })
}

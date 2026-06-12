package com.lloppy.timenomad.screens.profiles

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lloppy.timenomad.data.profile.AstroProfile
import com.lloppy.timenomad.ui.AppBackground
import com.lloppy.timenomad.ui.ScreenHeader
import com.lloppy.timenomad.ui.SectionCard
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ProfileEditorScreen(
    profileId: String?,
    onSaved: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileEditorViewModel = koinViewModel { parametersOf(profileId) },
) {
    val initial = viewModel.initial
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var day by remember { mutableStateOf((initial?.day ?: 1).toString()) }
    var month by remember { mutableStateOf((initial?.month ?: 1).toString()) }
    var year by remember { mutableStateOf((initial?.year ?: 2000).toString()) }
    var hour by remember { mutableStateOf((initial?.hour ?: 12).toString()) }
    var minute by remember { mutableStateOf((initial?.minute ?: 0).toString()) }
    var offset by remember { mutableStateOf(((initial?.utcOffsetMinutes ?: 0) / 60.0).toString()) }
    var lat by remember { mutableStateOf((initial?.latitude ?: 55.7558).toString()) }
    var lon by remember { mutableStateOf((initial?.longitude ?: 37.6173).toString()) }
    var place by remember { mutableStateOf(initial?.placeLabel ?: "") }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }
    var tags by remember { mutableStateOf(initial?.tags?.joinToString(", ") ?: "") }
    var placeQuery by remember { mutableStateOf(initial?.placeLabel ?: "") }
    val searchState by viewModel.search.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        AppBackground()
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ScreenHeader(title = if (viewModel.isNew) "Новый профиль" else "Редактор", onBack = onBack)

            field("Имя", name) { name = it }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                numField("День", day, Modifier.weight(1f)) { day = it }
                numField("Месяц", month, Modifier.weight(1f)) { month = it }
                numField("Год", year, Modifier.weight(1f)) { year = it }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                numField("Час", hour, Modifier.weight(1f)) { hour = it }
                numField("Минута", minute, Modifier.weight(1f)) { minute = it }
                numField("UTC ±ч", offset, Modifier.weight(1f)) { offset = it }
            }

            SectionCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Найти место (заполнит координаты)", fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                    Text("Поиск: данные © OpenStreetMap", fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = placeQuery,
                            onValueChange = { placeQuery = it },
                            label = { Text("Город / адрес") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )
                        OutlinedButton(onClick = { viewModel.searchPlace(placeQuery) }) { Text("Найти") }
                    }
                    when {
                        searchState.loading ->
                            CircularProgressIndicator(Modifier.padding(4.dp))
                        searchState.error != null ->
                            Text(searchState.error!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                        else -> searchState.results.forEach { p ->
                            Text(
                                p.name,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth().clickable {
                                    lat = p.latitude.toString()
                                    lon = p.longitude.toString()
                                    place = p.name
                                    placeQuery = p.name
                                    offset = (ProfileEditorViewModel.estimateUtcOffsetMinutes(p.longitude) / 60.0).toString()
                                    viewModel.clearSearch()
                                }.padding(vertical = 6.dp),
                            )
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                numField("Широта", lat, Modifier.weight(1f)) { lat = it }
                numField("Долгота (E+)", lon, Modifier.weight(1f)) { lon = it }
            }
            field("Место", place) { place = it }
            field("Теги (через запятую)", tags) { tags = it }
            field("Заметки", notes) { notes = it }

            Spacer(Modifier.height(6.dp))
            Button(
                onClick = {
                    val profile = AstroProfile(
                        id = profileId ?: "",
                        name = name.ifBlank { "Без имени" },
                        year = year.toIntOrNull() ?: 2000,
                        month = (month.toIntOrNull() ?: 1).coerceIn(1, 12),
                        day = (day.toIntOrNull() ?: 1).coerceIn(1, 31),
                        hour = (hour.toIntOrNull() ?: 12).coerceIn(0, 23),
                        minute = (minute.toIntOrNull() ?: 0).coerceIn(0, 59),
                        utcOffsetMinutes = ((offset.toDoubleOrNull() ?: 0.0) * 60).toInt(),
                        latitude = lat.toDoubleOrNull() ?: 0.0,
                        longitude = lon.toDoubleOrNull() ?: 0.0,
                        placeLabel = place,
                        notes = notes,
                        tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                    )
                    onSaved(viewModel.save(profile))
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Сохранить") }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun field(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = label != "Заметки",
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun numField(label: String, value: String, modifier: Modifier, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
    )
}

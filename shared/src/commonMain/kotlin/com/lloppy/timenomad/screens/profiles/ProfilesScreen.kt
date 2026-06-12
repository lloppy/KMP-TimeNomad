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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lloppy.timenomad.ui.AppBackground
import com.lloppy.timenomad.ui.Chip
import com.lloppy.timenomad.ui.CircleIconButton
import com.lloppy.timenomad.ui.LetterAvatar
import com.lloppy.timenomad.ui.ScreenHeader
import com.lloppy.timenomad.ui.SectionCard
import com.lloppy.timenomad.ui.avatarColor
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfilesScreen(
    onOpenProfile: (String) -> Unit,
    onNewProfile: () -> Unit,
    onEditProfile: (String) -> Unit,
    viewModel: ProfilesViewModel = koinViewModel(),
) {
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        AppBackground()
        Column(Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
            ScreenHeader(
                title = "Профили",
                subtitle = "${profiles.size} натальных карт",
                action = {
                    CircleIconButton(onClick = onNewProfile, container = MaterialTheme.colorScheme.primary) {
                        Icon(Icons.Filled.Add, contentDescription = "Новый профиль",
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
            )
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                placeholder = { Text("Поиск по имени, месту, тегам") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            )

            if (profiles.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет профилей. Нажмите + чтобы создать.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(profiles, key = { it.id }) { profile ->
                        SectionCard(Modifier.fillMaxWidth(), onClick = { onOpenProfile(profile.id) }) {
                            Row(Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                LetterAvatar(profile.name, avatarColor(profile.name))
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(profile.name, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface)
                                    Text(profile.birthDateLabel, fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (profile.placeLabel.isNotBlank()) {
                                        Text(profile.placeLabel, fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                if (profile.tags.isNotEmpty()) Chip(profile.tags.first())
                                Spacer(Modifier.width(8.dp))
                                CircleIconButton(
                                    onClick = { onEditProfile(profile.id) },
                                    size = 40.dp,
                                ) {
                                    Icon(Icons.Filled.Edit, contentDescription = "Редактировать",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

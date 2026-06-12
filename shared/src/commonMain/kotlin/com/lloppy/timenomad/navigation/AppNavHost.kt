package com.lloppy.timenomad.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.lloppy.timenomad.screens.chart.ChartScreen
import com.lloppy.timenomad.screens.dashboard.DashboardScreen
import com.lloppy.timenomad.screens.planetaryhours.PlanetaryHoursScreen
import com.lloppy.timenomad.screens.profiles.ProfileEditorScreen
import com.lloppy.timenomad.screens.profiles.ProfilesScreen
import com.lloppy.timenomad.screens.settings.SettingsScreen
import kotlin.reflect.KClass

private data class Tab(val route: Any, val routeClass: KClass<*>, val label: String, val icon: ImageVector)

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val tabs = listOf(
        Tab(DashboardDestination, DashboardDestination::class, "Небо", Icons.Filled.Home),
        Tab(ProfilesDestination, ProfilesDestination::class, "Профили", Icons.Filled.Person),
        Tab(PlanetaryHoursDestination, PlanetaryHoursDestination::class, "Часы", Icons.Filled.DateRange),
        Tab(SettingsDestination, SettingsDestination::class, "Настройки", Icons.Filled.Settings),
    )

    Scaffold(
        bottomBar = {
            val entry by navController.currentBackStackEntryAsState()
            val dest = entry?.destination
            NavigationBar {
                tabs.forEach { tab ->
                    val selected = dest?.hierarchyHasRoute(tab.routeClass) == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = { navController.navigateTab(tab.route) },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = DashboardDestination,
            modifier = Modifier.padding(padding),
        ) {
            composable<DashboardDestination> {
                DashboardScreen(onOpenSky = { navController.navigate(SkyChartDestination) })
            }
            composable<SkyChartDestination> {
                ChartScreen(profileId = null, onBack = { navController.popBackStack() })
            }
            composable<NatalChartDestination> { backStackEntry ->
                val id = backStackEntry.toRoute<NatalChartDestination>().profileId
                ChartScreen(
                    profileId = id,
                    onBack = { navController.popBackStack() },
                    onEdit = { navController.navigate(ProfileEditorDestination(id)) },
                )
            }
            composable<ProfilesDestination> {
                ProfilesScreen(
                    onOpenProfile = { id -> navController.navigate(NatalChartDestination(id)) },
                    onNewProfile = { navController.navigate(ProfileEditorDestination()) },
                    onEditProfile = { id -> navController.navigate(ProfileEditorDestination(id)) },
                )
            }
            composable<ProfileEditorDestination> { backStackEntry ->
                ProfileEditorScreen(
                    profileId = backStackEntry.toRoute<ProfileEditorDestination>().profileId,
                    onSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                )
            }
            composable<PlanetaryHoursDestination> { PlanetaryHoursScreen() }
            composable<SettingsDestination> { SettingsScreen() }
        }
    }
}

private fun androidx.navigation.NavDestination.hierarchyHasRoute(route: KClass<*>): Boolean =
    hierarchy.any { it.hasRoute(route) }

private fun NavHostController.navigateTab(route: Any) {
    navigate(route) {
        popUpTo(graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

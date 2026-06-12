package com.lloppy.timenomad.navigation

import kotlinx.serialization.Serializable

@Serializable
object DashboardDestination

@Serializable
object SkyChartDestination

@Serializable
data class NatalChartDestination(val profileId: String)

@Serializable
data class TransitsDestination(val profileId: String)

@Serializable
object ProfilesDestination

@Serializable
data class ProfileEditorDestination(val profileId: String? = null)

@Serializable
object PlanetaryHoursDestination

@Serializable
object SettingsDestination

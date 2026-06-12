package com.lloppy.timenomad.navigation

import kotlinx.serialization.Serializable

@Serializable
object DashboardDestination

/** Карта неба на текущий момент. */
@Serializable
object SkyChartDestination

/** Натальная карта профиля. */
@Serializable
data class NatalChartDestination(val profileId: String)

@Serializable
object ProfilesDestination

@Serializable
data class ProfileEditorDestination(val profileId: String? = null)

@Serializable
object PlanetaryHoursDestination

@Serializable
object SettingsDestination

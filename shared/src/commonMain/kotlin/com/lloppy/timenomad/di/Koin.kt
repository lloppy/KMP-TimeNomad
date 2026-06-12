package com.lloppy.timenomad.di

import com.lloppy.timenomad.AppViewModel
import com.lloppy.timenomad.astro.aspects.AspectCalculator
import com.lloppy.timenomad.astro.chart.ChartCalculator
import com.lloppy.timenomad.astro.chart.TransitCalculator
import com.lloppy.timenomad.astro.chart.TransitForecaster
import com.lloppy.timenomad.astro.ephemeris.EphemerisService
import com.lloppy.timenomad.astro.ephemeris.MeeusEphemeris
import com.lloppy.timenomad.astro.planetaryhours.PlanetaryHoursCalculator
import com.lloppy.timenomad.data.geo.GeocodingService
import com.lloppy.timenomad.data.geo.NominatimGeocoder
import com.lloppy.timenomad.data.profile.ProfileRepository
import com.lloppy.timenomad.data.settings.AstroSettingsRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import com.lloppy.timenomad.screens.chart.ChartViewModel
import com.lloppy.timenomad.screens.dashboard.DashboardViewModel
import com.lloppy.timenomad.screens.planetaryhours.PlanetaryHoursViewModel
import com.lloppy.timenomad.screens.profiles.ProfileEditorViewModel
import com.lloppy.timenomad.screens.profiles.ProfilesViewModel
import com.lloppy.timenomad.screens.settings.SettingsViewModel
import com.lloppy.timenomad.screens.transits.TransitsViewModel
import com.lloppy.timenomad.settings.SettingsRepository
import com.russhwolf.settings.Settings
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    single<Settings> { Settings() }
    single { SettingsRepository(get()) }
    single { AstroSettingsRepository(get()) }
    single { ProfileRepository(get()) }
    single {
        HttpClient {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }
    single<GeocodingService> { NominatimGeocoder(get()) }
}

val astroModule = module {
    single<EphemerisService> { MeeusEphemeris() }
    single { AspectCalculator() }
    single { ChartCalculator(ephemeris = get(), aspectCalculator = get()) }
    single { TransitCalculator(ephemeris = get(), aspectCalculator = get()) }
    single { TransitForecaster(ephemeris = get()) }
    single { PlanetaryHoursCalculator(ephemeris = get()) }
}

val viewModelModule = module {
    factoryOf(::AppViewModel)
    factoryOf(::DashboardViewModel)
    factoryOf(::ProfilesViewModel)
    factoryOf(::PlanetaryHoursViewModel)
    factoryOf(::SettingsViewModel)
    factory { params -> ChartViewModel(params.getOrNull(), get(), get(), get()) }
    factory { params -> ProfileEditorViewModel(params.getOrNull(), get(), get()) }
    factory { params -> TransitsViewModel(params.get(), get(), get(), get(), get(), get()) }
}

fun initKoin(config: (KoinApplication.() -> Unit)? = null) {
    startKoin {
        config?.invoke(this)
        modules(
            appModule,
            astroModule,
            viewModelModule,
        )
    }
}

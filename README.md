# Time Nomad (KMP)

Кроссплатформенное астрологическое приложение на **Kotlin Multiplatform** + **Compose Multiplatform**
(Android · iOS · Desktop) — KMP-клон [Time Nomad — Astrology Charts](https://apps.apple.com/ua/app/time-nomad-astrology-charts/id1092841332).

Расчёты выполняются **на устройстве, офлайн** (без интернета): позиции планет, натальная карта, дома, аспекты,
тропический и сидерический зодиак, планетарные часы.

Пакет: `com.lloppy.timenomad`.

## Возможности (итерация 1)

- 🌌 **Небо сейчас** — текущая карта, фаза Луны, ретроградные планеты.
- 🪐 **Карта-колесо** — знаки, дома, планеты, аспекты; текущее небо или натал профиля.
- 👤 **Профили** — данные рождения, заметки, теги, поиск; натальные карты.
- ♄ **Планетарные часы** — управитель дня и текущий час (халдейский ряд).
- ⚙️ **Настройки** — тема, зодиак (тропик/сидерик + аянамша), система домов, домашняя локация.

## Движок расчётов

Офлайн-эфемериды в `commonMain` за интерфейсом `EphemerisService`:
кеплеровские элементы JPL/Standish (планеты), ряд Meeus (Луна), средний узел.
Точность — единицы угловых минут. Планируемый апгрейд — порт **Swiss Ephemeris** (drop-in).
Подробности и роадмап — в [docs/TZ.md](docs/TZ.md).

## Сборка и запуск

```bash
# Тесты движка
./gradlew :shared:desktopTest

# Android (APK)
./gradlew :androidApp:assembleDebug

# Desktop
./gradlew :desktopApp:run

# iOS — открыть iosApp/ в Xcode (фреймворк Shared собирается Gradle)
./gradlew :shared:compileKotlinIosSimulatorArm64
```

## Структура

```
shared/src/commonMain/kotlin/com/lloppy/timenomad/
├── astro/        ядро расчётов (time, math, model, ephemeris, houses, aspects, chart, planetaryhours, format)
├── data/         профили и настройки
├── screens/      dashboard, chart, profiles, planetaryhours, settings (Screen + ViewModel)
├── ui/           компоненты, ChartWheel, MoonDisk
├── theme/        палитра, AstroColors
├── navigation/   маршруты и нижняя навигация
└── di/           Koin
```

> ⚠️ При переходе на Swiss Ephemeris учесть лицензию (AGPL-3.0 / платная Astrodienst) — см. TZ.

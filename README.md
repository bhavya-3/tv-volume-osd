# TV Volume OSD

An Android TV utility that displays a brief, local volume overlay when Android exposes an external ARC/eARC device's music-stream volume. It observes Android's public `AudioManager` APIs; it never sends CEC commands, controls audio, uses ADB, or connects to the internet.

## Status

Phase 1 is a diagnostic probe: it validates whether the app process sees the same `STREAM_MUSIC` value as Android's AudioService. Monitoring and overlays are intentionally not enabled until this is proven on a real TV.

## Build

Set `sdk.dir` in `local.properties`, then run `./gradlew test lint assembleDebug`.

## Privacy and permissions

The Phase 1 build has no Android permissions and no network capability. Later overlay/background features will be explicitly documented and opt-in.

See [the development status](docs/DEVELOPMENT_STATUS.md) and [architecture](docs/ARCHITECTURE.md).

# Development status

## Current status

Phase 1 and Phase 2 have passed on the physical fixture. The app process uses only `AudioManager` public APIs and exposes the result in the TV UI and debug log. Phase 3 overlay work is complete with an efficient non-input-capturing test OSD.

## Verified on physical device

On 2026-07-25, ADB connected to a TCL Smart TV running Android 12. The app reported `STREAM_MUSIC` current 30, min 0, max 100, mute false; at the same time `dumpsys audio` reported current 30, min 0, max 100, routed to HDMI ARC. This proves public `AudioManager.getStreamVolume()` matches the active ARC volume on this fixture at that instant.

## Architectural decisions

Keep raw audio ranges; normalize only graphical progress. Do not use `Settings.System`, dumpsys, hidden APIs, CEC control, or vendor checks. A hybrid approach is implemented where `VOLUME_CHANGED_ACTION` broadcasts are used as an optimization when available, with polling as a fallback.

## Security decisions

Phase 1 has no permissions, network access, exported service, receiver, or provider. The launcher activity is exported only because Android requires it for launcher discovery.

## Current blockers

None.

## Failed approaches not worth retrying

`Settings.System volume_music` is known to be stale on the fixture and is not used.

## Next tasks

Implement persistent monitoring state and UI controls, add unit tests around monitoring logic, physical integration test Roku changes, and implement product-quality settings and diagnostics.

## Manual tests still needed

None for Phase 1.

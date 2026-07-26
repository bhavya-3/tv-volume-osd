# Architecture

`AudioManagerVolumeProvider` is the sole initial `VolumeProvider`. It calls public APIs for `STREAM_MUSIC` current/min/max/mute state and enumerates public output devices. `VolumeState` keeps the raw scale while `VolumeNormalizer` derives a safe 0–1 progress value.

A hybrid approach is implemented where `VOLUME_CHANGED_ACTION` broadcasts are used as an optimization when available, with polling as a fallback. This provides faster detection than polling while maintaining compatibility.

The monitoring component uses a `VolumeChangeTrigger` abstraction that can be either:
1. `VolumeBroadcastTrigger` - dynamically registers for `VOLUME_CHANGED_ACTION` broadcasts when supported
2. `PollingVolumeTrigger` - fallback that polls every 200ms

The planned overlay is a non-focusable, non-touchable `TYPE_APPLICATION_OVERLAY`, used only after the user grants the special access permission.

No vendor or HDMI-control API is used. The app observes Android's view of audio state and never changes volume.

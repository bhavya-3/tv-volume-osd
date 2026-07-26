# TV Volume OSD

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![API: 23+](https://img.shields.io/badge/API-23%2B-brightgreen)](app/build.gradle.kts)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple)](build.gradle.kts)
[![GitHub release](https://img.shields.io/github/v/release/bhavya-3/tv-volume-osd)](https://github.com/bhavya-3/tv-volume-osd/releases)

**TV Volume OSD** is a free, open-source Android TV application that displays a large, customizable on-screen volume indicator whenever you adjust the volume. It is specifically designed for Android TV setups where the television is connected to an external audio system — such as a soundbar or AV receiver — over **HDMI ARC** or **HDMI eARC**.

In many ARC/eARC configurations, the TV's built-in volume bar either does not appear at all (because the TV believes it is not controlling audio), is too small to read from across the room, or simply does not reflect the actual volume level of the connected soundbar or receiver. This app solves all of those problems by reading the volume directly from Android's `AudioManager` using only public, documented APIs and rendering a clear, configurable overlay on top of whatever you are watching.

The app is entirely self-contained, uses **zero internet access**, has **no telemetry or analytics**, and **never modifies the volume** — it only observes and displays.

---

## Table of Contents

- [Why this app exists](#why-this-app-exists)
- [Features](#features)
- [Requirements](#requirements)
- [Installation on your TV](#installation-on-your-tv)
  - [Method 1: ADB from a computer (easiest)](#method-1-adb-from-a-computer-easiest)
  - [Method 2: Downloader app on the TV](#method-2-downloader-app-on-the-tv)
  - [Method 3: Send Files to TV (phone to TV)](#method-3-send-files-to-tv-phone-to-tv)
  - [Method 4: USB drive](#method-4-usb-drive)
- [Step-by-step setup](#step-by-step-setup)
  - [1. Launch the app](#1-launch-the-app)
  - [2. Grant overlay permission](#2-grant-overlay-permission)
  - [3. Start monitoring](#3-start-monitoring)
  - [4. Test it](#4-test-it)
  - [5. Customize](#5-customize)
- [Settings reference](#settings-reference)
- [Diagnostics screen](#diagnostics-screen)
- [How it works](#how-it-works)
- [Why not use the TV's built-in volume bar?](#why-not-use-the-tvs-built-in-volume-bar)
- [Why not use CEC or a universal remote?](#why-not-use-cec-or-a-universal-remote)
- [Architecture](#architecture)
- [Permissions](#permissions)
- [Compatibility](#compatibility)
- [Troubleshooting](#troubleshooting)
- [FAQ](#faq)
- [Development](#development)
  - [Prerequisites](#prerequisites)
  - [Commands](#commands)
  - [Project structure](#project-structure)
  - [Contributing](#contributing)
- [License](#license)
- [Security](#security)

---

## Why this app exists

When you connect a soundbar or AV receiver to an Android TV over HDMI ARC (Audio Return Channel), the television sends audio out through the HDMI cable to the external device. In this configuration, the TV stops controlling its internal speakers and hands volume control to the external audio system.

The problem is that Android TV's behavior in this scenario varies dramatically by manufacturer:

- **Some TVs** stop showing any volume indicator at all. The volume changes are audible through the soundbar, but there is no on-screen feedback.
- **Some TVs** show a tiny volume bar in the corner that is difficult to read from a couch.
- **Some TVs** show a volume bar, but it represents the TV's *internal speaker* volume rather than the actual ARC volume, making it useless.
- **Some smart TV platforms** (like Roku TV) overlay their own volume indicator that conflicts with or replaces the Android TV indicator.

This app exists to provide a **consistent, reliable, and customizable** volume overlay that works the same way regardless of your TV manufacturer, using only the public audio APIs that Android makes available to every application. It is particularly useful for:

- TCL televisions running Android TV / Google TV
- Sony Bravia TVs with external soundbars
- Any Android TV device connected to an ARC or eARC audio system
- Users who find the default volume indicator too small or hard to see
- Home theater setups where the TV volume and soundbar volume are independently controlled

---

## Features

- **Real-time volume overlay** — A clear, readable volume indicator appears instantly whenever the volume changes, and disappears automatically after a configurable duration.
- **7 overlay positions** — Choose exactly where on the screen the volume indicator appears: top-left, top-center, top-right, center, bottom-left, bottom-center, or bottom-right.
- **3 sizes** — Small, medium, or large text and progress bar to match your viewing distance and preference.
- **Adjustable duration** — Keep the overlay visible for 1, 1.5, 2, 3, or 5 seconds after the last volume change.
- **Configurable components** — Independently toggle the speaker icon, the numeric volume level, and the horizontal progress bar on or off.
- **Start on boot** — Optionally begin monitoring volume automatically every time the TV powers on, so you never have to launch the app manually.
- **TV remote friendly** — The entire settings interface is navigable using only the D-pad (up, down, left, right, and OK/select) on your TV remote. Focused items are highlighted for easy visibility.
- **Built-in diagnostics** — A diagnostics screen shows your TV model, Android version, app version, current volume level, volume range, mute state, and all detected audio output devices (such as "HDMI eARC" and "TV speaker"). This helps you verify that the app is reading the correct audio state.
- **Zero network access** — The app declares no `INTERNET` permission. It never connects to the internet, never sends data, and has no telemetry, analytics, or crash reporting. Your audio state never leaves your TV.
- **Minimal permissions** — The app only requests permissions necessary for its function: overlay drawing (to display on top of other apps), foreground service (to monitor in the background), notifications (for the foreground service on Android 13+), and boot completed (for the optional auto-start feature).
- **Dark theme** — A low-contrast dark interface designed for comfortable use on large TV displays in a dimly lit room.

---

## Requirements

- **An Android TV**, Google TV, or any Android device running **Android 6.0 (API 23) or later**. The app targets modern Android versions but maintains backward compatibility to Marshmallow.
- **The `SYSTEM_ALERT_WINDOW` permission** must be granted. This is an Android permission that allows the app to draw an overlay on top of other applications. The app will prompt you to enable this the first time you try to start monitoring or show a preview.
- An **HDMI ARC or eARC connection** between your TV and an external sound system is strongly recommended for the best experience, but the app will display a volume overlay for any audio output — including the TV's internal speakers and Bluetooth audio.

---

## Installation on your TV

There are several ways to install the APK on your Android TV. Choose the method that is most convenient for you.

### Method 1: ADB from a computer (easiest)

If you have a laptop or desktop computer on the same Wi-Fi network as your TV, this is the simplest method.

**Step 1: Enable Developer options on your TV**

1. Go to **Settings** → **About** on your TV.
2. Scroll down to **Build** (or **Android TV OS build**).
3. Click **Build** repeatedly (7–10 times) until you see a message saying "You are now a developer."

**Step 2: Enable USB debugging / ADB debugging**

1. Go to **Settings** → **Developer options** (it should now appear in your settings menu).
2. Find and enable **USB debugging** (or **ADB debugging**). If prompted, confirm.
3. Note your TV's **IP address** from **Settings** → **Network & internet** → your Wi-Fi network.

**Step 3: Connect from your computer**

```shell
# Connect to your TV via ADB
adb connect <TV_IP_ADDRESS>:5555

# Verify the connection
adb devices
# Expected output: <TV_IP_ADDRESS>:5555   device
```

**Step 4: Download and install**

```shell
# Download the latest APK from GitHub
curl -L -o tv-volume-osd.apk https://github.com/bhavya-3/tv-volume-osd/releases/latest/download/tv-volume-osd-v1.0.0.apk

# Install the app on your TV
adb install tv-volume-osd.apk
```

You should see `Performing Streamed Install` followed by `Success`.

**Step 5: Launch the app**

```shell
# Launch the app directly
adb shell am start -n org.tvvolumeosd/.MainActivity
```

Alternatively, find **TV Volume OSD** in your TV's app drawer under the name you installed it with.

---

### Method 2: Downloader app on the TV

If you do not have access to a computer, you can download the APK directly on your TV using a file downloader app.

**Step 1: Install the Downloader app**

1. On your TV, go to the Google Play Store.
2. Search for and install **Downloader** by AFTVnews (it has a cloud icon).
3. Open the **Downloader** app.

**Step 2: Grant file permissions**

The first time you open Downloader, it may ask for file access permissions. Grant them.

**Step 3: Download the APK**

1. In the Downloader app's URL field, enter:
   ```
   https://github.com/bhavya-3/tv-volume-osd/releases/latest/download/tv-volume-osd-v1.0.0.apk
   ```
2. Press **Go**. The app will download the APK file.
3. When the download completes, Downloader will prompt you to install it. Click **Install**.
4. After installation, click **Done** (not Open).

**Step 4: Enable install from unknown sources**

If your TV blocks the installation, you may need to:

1. Go to **Settings** → **Apps** → **Security & restrictions**.
2. Find **Downloader** in the list and enable **Install unknown apps** (or **Allow from this source**).
3. Retry the installation from inside the Downloader app.

**Step 5: Launch the app**

Find **TV Volume OSD** in your TV's app drawer and launch it.

---

### Method 3: Send Files to TV (phone to TV)

If you have an Android phone, you can send the APK directly to your TV wirelessly.

**Step 1: Install Send Files to TV on both devices**

1. On your **Android phone**, install **Send Files to TV** from the Google Play Store.
2. On your **Android TV**, install **Send Files to TV** from the Google Play Store.

**Step 2: Download the APK to your phone**

On your phone, download the APK:

```shell
# Open a browser on your phone and visit:
https://github.com/bhavya-3/tv-volume-osd/releases/latest/download/tv-volume-osd-v1.0.0.apk
```

Or use a direct download link from the [Releases page](https://github.com/bhavya-3/tv-volume-osd/releases).

**Step 3: Send the file**

1. Open **Send Files to TV** on your phone.
2. Select the downloaded APK file.
3. Choose your TV from the list of nearby devices.
4. On your TV, accept the incoming file transfer.
5. Once received, the TV will prompt you to install the app. Click **Install**.

**Step 4: Launch the app**

Find **TV Volume OSD** in your TV's app drawer and launch it.

---

### Method 4: USB drive

If you have a USB flash drive, you can transfer the APK manually.

**Step 1: Download the APK to your computer**

Download the APK from the [Releases page](https://github.com/bhavya-3/tv-volume-osd/releases) and save it to a USB flash drive formatted as FAT32 or NTFS.

**Step 2: Connect the USB drive to your TV**

Plug the USB drive into your TV's USB port.

**Step 3: Install a file manager**

If your TV does not have a built-in file manager, install one from the Google Play Store, such as **X-plore File Manager**.

**Step 4: Locate and install the APK**

1. Open the file manager and navigate to your USB drive.
2. Find the `tv-volume-osd-v1.0.0.apk` file.
3. Click on it and select **Install**.
4. If prompted, enable **Install from unknown sources** for the file manager app.

**Step 5: Launch the app**

Find **TV Volume OSD** in your TV's app drawer and launch it.

---

## Step-by-step setup

Once the app is installed, follow these steps to get it running.

### 1. Launch the app

Open **TV Volume OSD** from your TV's app drawer. You will see the main screen with three sections:

- **Monitoring** — a large button to start or stop volume monitoring.
- **Settings** — controls for position, duration, size, and component visibility.
- **Diagnostics** — a read-only display of your TV's current audio state.

### 2. Grant overlay permission

If you tap **Start monitoring** or **Show preview**, the app will check whether it has the `SYSTEM_ALERT_WINDOW` permission. If not, it will open your TV's special app access settings screen. Find **TV Volume OSD** in the list and toggle the **Display over other apps** permission on.

After granting the permission, press Back on your remote to return to the app.

### 3. Start monitoring

Navigate to the **Start monitoring** button and press OK on your remote. The button text will change to **Stop monitoring**, and the diagnostics section will show **Status: Running**. A persistent notification will also appear in your TV's notification panel indicating that volume monitoring is active.

### 4. Test it

Press the volume up or down button on your TV remote (or your soundbar's remote, if it controls the TV volume). A dark, rounded overlay should appear at the bottom of the screen showing:

- A speaker icon (🔊 when unmuted, 🔇 when muted)
- The current numeric volume level
- A horizontal progress bar indicating the volume level

### 5. Customize

Navigate to the **Settings** section and adjust the following to your preference:

- **Position** — Click to cycle through 7 screen positions. Choose where the overlay appears.
- **Duration** — Click to cycle through 1s, 1.5s, 2s, 3s, and 5s. This controls how long the overlay stays visible after the last volume change.
- **Size** — Click to cycle through Small, Medium, and Large. This controls the text size and overall dimensions of the overlay.
- **Show icon** — Toggle the speaker icon on or off.
- **Show bar** — Toggle the horizontal progress bar on or off.
- **Show number** — Toggle the numeric volume readout on or off.

At least one of icon, number, or bar must remain visible.

---

## Settings reference

| Setting | What it does | Options | Default |
|---|---|---|---|
| **Position** | Where on the screen the overlay appears | Top left, Top center, Top right, Center, Bottom left, Bottom center, Bottom right | Bottom center |
| **Duration** | How long the overlay stays visible after the last volume change | 1.0s, 1.5s, 2.0s, 3.0s, 5.0s | 2.0s |
| **Size** | Text size and overall dimensions of the overlay | Small, Medium, Large | Medium |
| **Show icon** | Whether the speaker icon (🔊/🔇) is displayed | On / Off | On |
| **Show number** | Whether the numeric volume level is displayed | On / Off | On |
| **Show bar** | Whether the horizontal progress bar is displayed | On / Off | On |
| **Start on boot** | Whether to begin monitoring automatically when the TV powers on | On / Off | Off |

---

## Diagnostics screen

The diagnostics section at the bottom of the main screen shows real-time information about your TV's current audio state. This is useful for verifying that the app is reading the correct values from Android's `AudioManager`. It displays:

- **Status** — Whether monitoring is currently Running or Stopped.
- **Device** — The manufacturer and model of your TV (e.g., `TCL QM5K`).
- **Android** — The Android version and API level (e.g., `12 (API 32)`).
- **App** — The installed version of this app (e.g., `1.0.0`).
- **Volume** — The current stream volume as a fraction (e.g., `30/100`).
- **Range** — The volume range reported by AudioManager (e.g., `0–100`).
- **Muted** — Whether the audio stream is currently muted.
- **Outputs** — A list of all connected audio output devices as reported by `AudioManager.getDevices()`, such as `HDMI eARC, TV speaker`.

Tap **Refresh diagnostics** to update this information at any time.

---

## How it works

```
┌─────────────┐    ┌──────────────────┐    ┌─────────────────┐
│ AudioManager │───▶│ VolumeMonitor    │───▶│ OverlayController│
│ (public API) │    │ Service          │    │ (WindowManager) │
└─────────────┘    │ (foreground)      │    └─────────────────┘
                   │ broadcast + poll  │         │
                   └──────────────────┘         ▼
                                           ┌──────────────┐
                                           │ TYPE_APPLICA- │
                                           │ TION_OVERLAY  │
                                           │ (non-touch)   │
                                           └──────────────┘
```

The app implements a **hybrid detection strategy** that balances responsiveness with reliability:

1. **Broadcast trigger**: The app registers a `BroadcastReceiver` for the `android.media.VOLUME_CHANGED_ACTION` broadcast. When Android's AudioService detects a volume change, it sends this broadcast, and the app receives it as a hint that a change may have occurred. This provides near-instantaneous notification.

2. **Polling fallback**: Some Android TV devices do not reliably send the `VOLUME_CHANGED_ACTION` broadcast, or the broadcast may arrive before the audio state is fully updated. To handle this, the app also polls `AudioManager.getStreamVolume()` on a repeating schedule:
   - **Every 50ms** during an active burst of volume changes (for responsiveness).
   - **Every 200ms** during quiet periods when no changes are expected (for efficiency).
   - **Every 1000ms** when the broadcast trigger is working and no changes have been detected recently (for battery/idle efficiency).

3. **Overlay rendering**: When a volume change is detected, `OverlayController` creates a `TYPE_APPLICATION_OVERLAY` window (the standard Android API for drawing on top of other apps). The overlay is configured with three critical flags:
   - `FLAG_NOT_FOCUSABLE` — The overlay never takes focus away from whatever app or video is playing.
   - `FLAG_NOT_TOUCHABLE` — All touch, click, and remote events pass through the overlay to the TV UI underneath.
   - `FLAG_NOT_TOUCH_MODAL` — Touch events outside the overlay bounds are never blocked.

   The overlay stays visible for the duration you configured in Settings, then automatically removes itself. If you press volume again while it is visible, the overlay's content updates in place without any visual flicker, and the timer resets.

4. **Settings persistence**: All settings (position, duration, size, visibility toggles, auto-start) are stored in Android `SharedPreferences`. They persist across app restarts and TV power cycles.

The entire detection and rendering pipeline uses only **public, documented Android APIs**. The app never:
- Reads from `Settings.System` (known to return stale values on some TVs)
- Parses `dumpsys` output (not allowed in production apps)
- Uses hidden or internal Android APIs
- Sends CEC commands or HDMI control messages
- Modifies the volume or audio routing
- Connects to the network or sends data

---

## Why not use the TV's built-in volume bar?

Android TV's built-in volume indicator varies significantly by manufacturer:

- **Google TV / Android TV reference design**: Shows a thin horizontal bar at the top of the screen for a few seconds. It is functional but can be difficult to see from across the room, especially on larger screens.
- **TCL / Roku TV**: Roku's own operating system layer displays its own volume overlay that replaces Android's. This overlay may not reflect the ARC volume correctly and cannot be customized.
- **Sony Bravia**: Shows a translucent bar in the corner.
- **Hisense / Philips**: Custom implementations that vary by model and firmware version.

TV Volume OSD provides a **consistent experience** across all manufacturers and is fully customizable to your preferences.

---

## Why not use CEC or a universal remote?

HDMI-CEC (Consumer Electronics Control) allows devices connected over HDMI to control each other. In theory, a TV could use CEC to read a soundbar's volume and display it. In practice:

- CEC is an optional feature and many devices implement it inconsistently.
- CEC commands travel at a low priority and can be slow or unreliable.
- Some TVs do not expose CEC volume state through any public API.
- CEC can conflict with other control systems (e.g., Roku's CEC implementation).

**TV Volume OSD deliberately avoids CEC** and instead reads volume directly from Android's `AudioManager`, which aggregates the system's understanding of the current audio state regardless of how it is being controlled (TV remote, CEC, Bluetooth, USB, etc.).

---

## Architecture

For a detailed architecture overview, see [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

Key components in the source code:

| Component | File | Purpose |
|---|---|---|
| `MainActivity` | `MainActivity.kt` | Settings UI, diagnostics display, monitoring start/stop toggle |
| `VolumeMonitorService` | `VolumeMonitorService.kt` | Foreground service that detects volume changes via broadcast and polling |
| `OverlayController` | `OverlayController.kt` | Manages the `WindowManager` overlay — create, update, position, and remove |
| `AudioManagerVolumeProvider` | `AudioManagerVolumeProvider.kt` | Reads volume state from `AudioManager` public APIs only |
| `VolumeState` | `VolumeState.kt` | Data class holding current/min/max/muted/outputs with a normalized progress value |
| `VolumeNormalizer` | `VolumeState.kt` | Converts raw volume range to a **0.0–1.0** normalized progress value |
| `VolumeBroadcastTrigger` | `VolumeBroadcastTrigger.kt` | Registers for `VOLUME_CHANGED_ACTION` broadcasts as an optimization hint |
| `Prefs` | `Prefs.kt` | `SharedPreferences` wrapper for all user-configurable settings |
| `BootReceiver` | `BootReceiver.kt` | `BOOT_COMPLETED` broadcast receiver for optional auto-start |

---

## Permissions

| Permission | Purpose | Required | When requested |
|---|---|---|---|
| `SYSTEM_ALERT_WINDOW` | Draw the volume overlay on top of other apps | Yes | When you first tap Start monitoring or Show preview |
| `FOREGROUND_SERVICE` | Keep the monitoring service running in the background | Yes | Declared in manifest; no runtime prompt |
| `FOREGROUND_SERVICE_DATA_SYNC` | Declare the service type (required by Android 12+ for foreground services) | Yes | Declared in manifest; no runtime prompt |
| `POST_NOTIFICATIONS` | Show the foreground service notification (required by Android 13+ for visible foreground services) | Yes | When the service starts on Android 13+ |
| `RECEIVE_BOOT_COMPLETED` | Automatically start monitoring when the TV boots | Only if you enable Start on boot | Declared in manifest; no runtime prompt |

All permissions are declared in `AndroidManifest.xml` and requested through the normal Android permission UI at runtime. The app does not declare or request `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, `BLUETOOTH`, `LOCATION`, `CAMERA`, `RECORD_AUDIO`, `READ_EXTERNAL_STORAGE`, or any hardware permissions.

---

## Compatibility

**Tested on:**

| TV | Android version | Audio device | Connection | Status |
|---|---|---|---|---|
| TCL QM5K (2024) | 12 | Sony HT-BC50 soundbar | HDMI eARC | Verified — volume OSD displays correctly for ARC volume changes |

**Expected to work on:**

- Any Android TV or Google TV device running Android 6.0 (API 23) or later
- Any TV with HDMI ARC or eARC connected to a soundbar, AV receiver, or home theater system
- Tablets or phones running Android 6.0+ (the overlay will work, though the app is designed for TV interfaces)

**Known limitations:**

- The app reads `AudioManager.getStreamVolume()` which reflects Android's understanding of the volume. On some TV firmware, this value may briefly oscillate between the old and new volume after a change (a firmware-level quirk). The overlay updates smoothly and shows the correct final value.
- The app cannot detect volume changes that happen entirely outside of Android's audio framework (e.g., a soundbar's own remote that adjusts volume directly on the soundbar without involving the TV). For best results, use the TV remote or a universal remote configured to control TV volume via HDMI-CEC.
- The app cannot disable your TV's built-in volume overlay if your TV's firmware insists on showing one. However, on many TVs, the built-in overlay and this app's overlay will coexist.

---

## Troubleshooting

### The overlay does not appear when I change the volume

1. **Check that monitoring is running**: Open the app and verify the diagnostics section shows **Status: Running**. If it shows **Stopped**, tap the **Start monitoring** button.
2. **Check the overlay permission**: Ensure that **Display over other apps** is enabled for TV Volume OSD in your TV's settings. The app will prompt you for this the first time you try to start monitoring.
3. **Check your audio output**: The diagnostics section shows your current **Outputs**. If you see only `TV speaker` and not `HDMI ARC` or `HDMI eARC`, your TV may not be detecting the ARC connection. Check your HDMI cable and TV audio settings.
4. **Try changing the volume with the TV remote**: Some soundbar remotes control volume directly on the soundbar without involving the TV's Android system. Use the TV's own remote to change ARC volume.
5. **Reboot**: Try restarting your TV. Occasionally, Android's audio service can get into a state where volume broadcasts are not sent.

### The overlay shows the wrong volume level

The app displays what Android's `AudioManager.getStreamVolume()` returns for the `STREAM_MUSIC` stream. If this does not match your soundbar's actual volume:

1. Check the **diagnostics screen** to see what value the app is reading. If it matches `dumpsys audio` output, the app is reading the correct value and the mismatch is between Android's understanding of the volume and your soundbar's actual volume.
2. Some TVs synchronize volume between the TV and the soundbar over ARC. If yours does not, the TV's volume value and the soundbar's actual audio level may differ. This is a limitation of the ARC specification and the TV's implementation.

### The overlay flickers or blinks

The overlay updates its content in-place without destroying and recreating the window. If you see flickering, it may be caused by:

- **Your TV's firmware briefly oscillating the volume value** after a change. This is visible in the app's log as rapid `old=X new=Y` followed by `old=Y new=X` messages. The overlay updates smoothly, but the content may change briefly.
- **The TV's own volume overlay** appearing at the same time. The app cannot prevent the TV's system UI from also showing a volume indicator.

### The app does not start on boot

1. **Enable Start on boot** in the Settings section of the app.
2. Ensure that the `RECEIVE_BOOT_COMPLETED` permission is not being blocked by your TV's manufacturer. Some manufacturers disable this permission for third-party apps on their devices.

---

## FAQ

**Q: Does this app work with any soundbar?**

A: It works with any audio setup that is connected to your Android TV via HDMI ARC, HDMI eARC, optical, Bluetooth, or the TV's internal speakers. The app reads Android's unified volume state, so it will display the volume that Android believes the system is using.

**Q: Does the app use internet access?**

A: No. The app declares no `INTERNET` permission and never connects to any network. It is completely offline. There is no telemetry, no crash reporting, no ads, and no data collection of any kind.

**Q: Can this app control the volume?**

A: No. The app only reads and displays volume state. It never modifies the volume, sends audio commands, or controls any hardware. It is an observer, not a controller.

**Q: Will this app drain my TV's battery or slow it down?**

A: No. The app uses a lightweight polling mechanism that runs only when the app is actively monitoring. The poll interval ranges from 50ms (during active volume changes) to 1000ms (when idle). The CPU impact is negligible.

**Q: Does the overlay block the TV remote or interfere with gameplay?**

A: No. The overlay uses `FLAG_NOT_FOCUSABLE`, `FLAG_NOT_TOUCHABLE`, and `FLAG_NOT_TOUCH_MODAL`, which means it never receives input focus, never intercepts remote or touch events, and does not block any input from reaching the content underneath it. You can interact with your TV as if the overlay were not there.

**Q: Can I use this app on a tablet or phone?**

A: Yes. The app will work on any Android device running API 23+. The interface is optimized for TV remotes (D-pad navigation), but it is functional on touchscreen devices as well.

**Q: Why does the app need the POST_NOTIFICATIONS permission?**

A: On Android 13 (API 33) and later, foreground services must display a notification to the user. The `POST_NOTIFICATIONS` permission is required to show this notification. The notification is low-priority and reads "TV Volume OSD is monitoring volume." It is required for the background monitoring service to function.

**Q: Does the app support Android TV's Leanback library?**

A: No. The app uses plain Android views with D-pad focus management rather than the Leanback UI toolkit. This keeps the APK size small (under 100 KB) and avoids additional dependencies.

---

## Development

### Prerequisites

- **Android Studio** (latest version recommended) or command-line build tools
- **Android SDK** — compile SDK version 35
- **JDK 17**
- **Android TV device** or emulator running API 23+ for testing

### Commands

```shell
# Run all unit tests
./gradlew test

# Run Android lint checks
./gradlew lint

# Build a debug APK (signed with the debug keystore, installable via adb)
./gradlew assembleDebug

# Build a release APK (unsigned — requires your own keystore or adb install)
./gradlew assembleRelease

# Full verification: test + lint + build
./gradlew test lint assembleDebug
```

### Project structure

```
tv-volume-osd/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/org/tvvolumeosd/     # All Kotlin source files
│   │   │   │   ├── MainActivity.kt       # Settings UI, diagnostics, monitoring toggle
│   │   │   │   ├── VolumeMonitorService.kt # Foreground service for volume detection
│   │   │   │   ├── OverlayController.kt    # WindowManager overlay management
│   │   │   │   ├── AudioManagerVolumeProvider.kt # Public AudioManager API reader
│   │   │   │   ├── VolumeState.kt          # Data class + VolumeNormalizer
│   │   │   │   ├── VolumeProvider.kt       # VolumeProvider interface
│   │   │   │   ├── VolumeBroadcastTrigger.kt # VOLUME_CHANGED_ACTION receiver
│   │   │   │   ├── VolumeChangeTrigger.kt  # Trigger interface
│   │   │   │   ├── Prefs.kt                # SharedPreferences wrapper
│   │   │   │   └── BootReceiver.kt         # BOOT_COMPLETED receiver
│   │   │   ├── res/                        # Android resources
│   │   │   │   ├── values/strings.xml
│   │   │   │   ├── values/colors.xml
│   │   │   │   ├── values/styles.xml
│   │   │   │   ├── drawable/             # Vector icons
│   │   │   │   └── xml/data_extraction_rules.xml
│   │   │   └── AndroidManifest.xml
│   │   └── test/java/org/tvvolumeosd/     # Unit tests
│   │       ├── VolumeNormalizerTest.kt     # 17 normalizer tests
│   │       └── VolumeStateTest.kt          # 6 state tests
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── docs/
│   ├── ARCHITECTURE.md
│   ├── COMPATIBILITY.md
│   ├── DEVELOPMENT_STATUS.md
│   └── THREAT_MODEL.md
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── CONTRIBUTING.md
├── LICENSE
├── SECURITY.md
└── README.md
```

### Contributing

Contributions are welcome. Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

Before submitting a pull request, please keep the following principles in mind:

- **Local-first**: The app should never depend on network connectivity or remote services.
- **Manufacturer-neutral**: Avoid code that works around a specific TV manufacturer's quirks unless the approach is generalizable and uses documented APIs.
- **Public APIs only**: Do not use hidden Android APIs, `@hide` annotated methods, internal `com.android` classes, or reflection on non-SDK interfaces. The app must pass Google's API compatibility requirements.
- **No vendor lock-in**: Do not add support for manufacturer-specific SDKs, CEC extensions, or proprietary audio control protocols.

To contribute:

1. Fork the repository.
2. Create a feature branch.
3. Make your changes, ensuring `./gradlew test lint assembleDebug` passes.
4. Open a pull request against the `main` branch.

---

## License

[MIT](LICENSE) © Bhavya Shah

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

---

## Security

See [SECURITY.md](SECURITY.md) for the security policy and [THREAT_MODEL.md](docs/THREAT_MODEL.md) for a detailed threat model.

This app is designed with a security-first approach:

- **No network access** — The app cannot send or receive data over the internet. It operates entirely offline.
- **No telemetry or analytics** — No crash reporting, usage tracking, or analytics SDKs are included.
- **No hidden or vendor-specific APIs** — Only public, documented Android APIs are used.
- **Minimal, opt-in permissions** — Each permission serves a specific, documented purpose and is requested at runtime.
- **Non-interactive overlay** — The overlay never captures input, never takes focus, and never intercepts events. It is purely a visual indicator.

To report a security vulnerability, please use GitHub's private vulnerability reporting feature when this project is published.

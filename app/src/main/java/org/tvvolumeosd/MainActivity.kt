package org.tvvolumeosd

import android.app.Activity
import android.os.Bundle
import android.provider.Settings
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private val provider by lazy { AudioManagerVolumeProvider(this) }
    private lateinit var diagnostics: TextView
    private val overlay by lazy { OverlayController(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        diagnostics = TextView(this).apply {
            textSize = 22f
            setTextColor(getColor(R.color.foreground))
            setPadding(56, 48, 56, 32)
            isFocusable = true
        }
        val refresh = Button(this).apply {
            text = getString(R.string.refresh_audio_diagnostics)
            isAllCaps = false
            setOnClickListener { refresh() }
        }
        val testOverlay = Button(this).apply {
            text = "Test overlay"
            isAllCaps = false
            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) this@MainActivity.overlay.show(provider.getVolumeState())
                else startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            }
        }
        val monitoring = Button(this).apply {
            text = "Start volume monitoring"
            isAllCaps = false
            setOnClickListener {
                if (!Settings.canDrawOverlays(this@MainActivity)) {
                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
                } else {
                    val service = Intent(this@MainActivity, VolumeMonitorService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(service) else startService(service)
                }
            }
        }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            addView(diagnostics)
            addView(refresh, LinearLayout.LayoutParams(-2, -2).apply { leftMargin = 56 })
            addView(testOverlay, LinearLayout.LayoutParams(-2, -2).apply { leftMargin = 56 })
            addView(monitoring, LinearLayout.LayoutParams(-2, -2).apply { leftMargin = 56 })
        })
        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onDestroy() { overlay.remove(); super.onDestroy() }

    private fun refresh() {
        val state = provider.getVolumeState()
        Log.i(TAG, "audio_state current=${state.current} min=${state.min} max=${state.max} muted=${state.muted} outputs=${state.outputs.joinToString()}")
        diagnostics.text = getString(
            R.string.diagnostics,
            state.current,
            state.min,
            state.max,
            state.muted,
            state.outputs.joinToString().ifBlank { getString(R.string.no_outputs_reported) },
        )
    }

    private companion object { const val TAG = "TvVolumeOsd" }
}

package org.tvvolumeosd

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView

/** Brief, fixed-content overlay. It is deliberately incapable of receiving input. */
class OverlayController(context: Context) {
    private val appContext = context.applicationContext
    private val windows = context.getSystemService(WindowManager::class.java)
    private val handler = Handler(Looper.getMainLooper())
    private var view: TextView? = null
    private val hide = Runnable { remove() }

    fun show(state: VolumeState) {
        val existing = view
        if (existing != null) {
            existing.text = label(state)
            handler.removeCallbacks(hide)
            handler.postDelayed(hide, 1_500)
            return
        }
        val label = TextView(appContext).apply {
            text = label(state)
            textSize = 30f
            setTextColor(Color.WHITE)
            setPadding(40, 24, 40, 24)
            background = GradientDrawable().apply { setColor(0xDD202124.toInt()); cornerRadius = 18f }
        }
        val params = WindowManager.LayoutParams(
            -2, -2, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT,
        ).apply { gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL; y = 72 }
        windows.addView(label, params)
        view = label
        handler.postDelayed(hide, 1_500)
    }

    fun remove() { handler.removeCallbacks(hide); view?.let(windows::removeViewImmediate); view = null }

    private fun label(state: VolumeState) = "${if (state.muted) "🔇" else "🔊"}   ${state.current}   ${bar(state.normalized)}"

    private fun bar(progress: Float): String {
        val segments = 12
        val filled = (progress * segments).toInt().coerceIn(0, segments)
        return "█".repeat(filled) + "░".repeat(segments - filled)
    }
}

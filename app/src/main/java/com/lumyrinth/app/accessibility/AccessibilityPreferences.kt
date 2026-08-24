package com.lumyrinth.app.accessibility

import android.content.Context
import android.provider.Settings

/** Respects the Android animator scale rather than forcing decorative motion. */
fun reduceMotionEnabled(context: Context): Boolean = try {
    Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
} catch (_: Exception) { false }

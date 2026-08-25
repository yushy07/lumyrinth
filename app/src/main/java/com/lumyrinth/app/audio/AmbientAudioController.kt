package com.lumyrinth.app.audio

import android.content.Context
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.lumyrinth.app.R

/** Bundled offline ambient soundscape player with automatic audio focus management. */
class AmbientAudioController(context: Context) {
    private val appContext = context.applicationContext
    private var player: ExoPlayer? = null

    init {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build()

            player = ExoPlayer.Builder(appContext)
                .setAudioAttributes(audioAttributes, true)
                .build().apply {
                    repeatMode = Player.REPEAT_MODE_ONE
                    volume = 0.42f
                }
        } catch (e: Throwable) {
            Log.w("AmbientAudio", "Failed to initialize ExoPlayer", e)
        }
    }

    fun play(selection: String) {
        try {
            val p = player ?: return
            val resource = when (selection) {
                "Rain" -> R.raw.ambient_rain
                "Night" -> R.raw.ambient_night
                "Ocean" -> R.raw.ambient_ocean
                "Forest" -> R.raw.ambient_forest
                "Fireplace" -> R.raw.ambient_fireplace
                "Stream" -> R.raw.ambient_stream
                "Deep Space" -> R.raw.ambient_deep_space
                else -> null
            }
            if (resource == null) {
                p.pause()
                return
            }
            p.setMediaItem(MediaItem.fromUri("android.resource://${appContext.packageName}/$resource"))
            p.prepare()
            p.play()
        } catch (e: Throwable) {
            Log.w("AmbientAudio", "Failed to play ambient: $selection", e)
        }
    }

    fun setVolume(vol: Float) {
        try {
            player?.volume = vol.coerceIn(0f, 1f)
        } catch (e: Throwable) {
            Log.w("AmbientAudio", "Failed to set volume", e)
        }
    }

    fun pause() {
        try {
            player?.pause()
        } catch (e: Throwable) {
            Log.w("AmbientAudio", "Failed to pause player", e)
        }
    }

    fun release() {
        try {
            player?.release()
            player = null
        } catch (e: Throwable) {
            Log.w("AmbientAudio", "Failed to release player", e)
        }
    }
}


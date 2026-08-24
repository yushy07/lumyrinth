package com.lumyrinth.app.audio

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.lumyrinth.app.R

/** Prepared for bundled Rain, Night, and Ocean loops. No remote audio is used. */
class AmbientAudioController(private val context: Context) {
    private val player = ExoPlayer.Builder(context).build().apply { repeatMode = Player.REPEAT_MODE_ONE; volume = 0.42f }
    fun play(selection: String) {
        val resource = when (selection) {
            "Rain" -> R.raw.ambient_rain; "Night" -> R.raw.ambient_night; "Ocean" -> R.raw.ambient_ocean
            "Forest" -> R.raw.ambient_forest; "Fireplace" -> R.raw.ambient_fireplace
            "Stream" -> R.raw.ambient_stream; "Deep Space" -> R.raw.ambient_deep_space
            else -> null
        }
        if (resource == null) { player.pause(); return }
        player.setMediaItem(MediaItem.fromUri("android.resource://${context.packageName}/$resource")); player.prepare(); player.play()
    }
    fun pause() { player.pause() }
    fun release() { player.release() }
}

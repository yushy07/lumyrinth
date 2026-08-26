package com.lumyrinth.app.audio

import android.content.Context
import android.media.AudioAttributes as AndroidAudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.lumyrinth.app.R

/** Bundled offline ambient soundscape player with automatic audio focus management & ducking. */
class AmbientAudioController(context: Context) {
    private val appContext = context.applicationContext
    private var player: ExoPlayer? = null
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var targetVolume = 0.42f
    private var isPlayingAmbient = false
    private var currentSelection: String? = null
    private var audioFocusRequest: AudioFocusRequest? = null

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                player?.pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                player?.volume = (targetVolume * 0.25f).coerceIn(0f, 1f)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                player?.volume = targetVolume
                if (isPlayingAmbient && player?.isPlaying == false) {
                    player?.play()
                }
            }
        }
    }

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
                    volume = targetVolume
                }
        } catch (e: Throwable) {
            Log.w("AmbientAudio", "Failed to initialize ExoPlayer", e)
        }
    }

    private fun requestAudioFocus(): Boolean {
        val am = audioManager ?: return true
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val attr = AndroidAudioAttributes.Builder()
                .setUsage(AndroidAudioAttributes.USAGE_MEDIA)
                .setContentType(AndroidAudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(attr)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build()
            audioFocusRequest = request
            am.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            am.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    private fun abandonAudioFocus() {
        val am = audioManager ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { am.abandonAudioFocusRequest(it) }
            audioFocusRequest = null
        } else {
            @Suppress("DEPRECATION")
            am.abandonAudioFocus(audioFocusChangeListener)
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
                pause()
                return
            }
            currentSelection = selection
            isPlayingAmbient = true
            requestAudioFocus()
            p.setMediaItem(MediaItem.fromUri("android.resource://${appContext.packageName}/$resource"))
            p.prepare()
            p.volume = targetVolume
            p.play()
        } catch (e: Throwable) {
            Log.w("AmbientAudio", "Failed to play ambient: $selection", e)
        }
    }

    fun setVolume(vol: Float) {
        try {
            targetVolume = vol.coerceIn(0f, 1f)
            player?.volume = targetVolume
        } catch (e: Throwable) {
            Log.w("AmbientAudio", "Failed to set volume", e)
        }
    }

    fun pause() {
        try {
            isPlayingAmbient = false
            player?.pause()
            abandonAudioFocus()
        } catch (e: Throwable) {
            Log.w("AmbientAudio", "Failed to pause player", e)
        }
    }

    fun release() {
        try {
            isPlayingAmbient = false
            abandonAudioFocus()
            player?.release()
            player = null
        } catch (e: Throwable) {
            Log.w("AmbientAudio", "Failed to release player", e)
        }
    }
}


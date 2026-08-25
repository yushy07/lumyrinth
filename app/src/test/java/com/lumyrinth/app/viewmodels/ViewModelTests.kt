package com.lumyrinth.app.viewmodels

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.lumyrinth.app.data.UserPreferencesRepository
import com.lumyrinth.app.data.session.CustomRhythmEntity
import com.lumyrinth.app.data.session.SessionRepository
import com.lumyrinth.app.ui.viewmodels.HomeViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ViewModelTests {

    private lateinit var context: Context
    private lateinit var sessionRepo: SessionRepository
    private lateinit var prefsRepo: UserPreferencesRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        sessionRepo = SessionRepository.from(context)
        prefsRepo = UserPreferencesRepository(context)
    }

    @Test
    fun homeViewModel_initialState_returnsZeroProgressAndDefaults() = runBlocking {
        val viewModel = HomeViewModel(sessionRepo, prefsRepo)
        val progress = viewModel.progressSummary.value
        assertEquals(0, progress.totalSessionsCount)
        assertEquals(0, progress.totalMindfulMinutes)
        assertNull(viewModel.lastUsedRhythm.value)
    }

    @Test
    fun saveCustomRhythm_savesToRepo() = runBlocking {
        val entity = CustomRhythmEntity(
            id = "test_id",
            name = "Test Rhythm",
            inhaleSeconds = 4,
            hold1Seconds = 4,
            exhaleSeconds = 4,
            hold2Seconds = 4,
            defaultDurationMinutes = 5,
            soundDefault = true,
            hapticsDefault = true,
            createdAtEpochMillis = System.currentTimeMillis()
        )

        sessionRepo.saveCustomRhythm(entity)

        val rhythms = sessionRepo.customRhythms.first()
        assertTrue(rhythms.any { it.name == "Test Rhythm" })
    }

    @Test
    fun toggleDefaults_updatesPreferences() = runBlocking {
        prefsRepo.setHapticGuidanceDefault(false)
        prefsRepo.setSoundGuidanceDefault(false)

        val prefs = prefsRepo.preferences.first()
        assertEquals(false, prefs.hapticGuidanceDefault)
        assertEquals(false, prefs.soundGuidanceDefault)
    }
}

package com.proq.cryptosignals.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class SettingsRepository(private val context: Context) {

    private val KEY_DARK = booleanPreferencesKey("dark_mode")
    private val KEY_CAPITAL = doublePreferencesKey("capital_per_signal")
    private val KEY_MIN_SCORE = intPreferencesKey("min_score")

    val darkModeFlow: Flow<Boolean> = context.dataStore.data.map { it[KEY_DARK] ?: true }
    val capitalPerSignalFlow: Flow<Double> = context.dataStore.data.map { it[KEY_CAPITAL] ?: 50.0 }
    val minScoreFlow: Flow<Int> = context.dataStore.data.map { it[KEY_MIN_SCORE] ?: 80 }

    suspend fun setDarkMode(v: Boolean) { context.dataStore.edit { it[KEY_DARK] = v } }
    suspend fun setCapitalPerSignal(v: Double) { context.dataStore.edit { it[KEY_CAPITAL] = v } }
    suspend fun setMinScore(v: Int) { context.dataStore.edit { it[KEY_MIN_SCORE] = v } }
}

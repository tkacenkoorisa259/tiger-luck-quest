package com.Hefputu5.Nugsis.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    fun int(key: String, fallback: Int): Int = prefs.getInt(key, fallback)

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun bool(key: String, fallback: Boolean): Boolean = prefs.getBoolean(key, fallback)

    fun putBool(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun text(key: String, fallback: String): String = prefs.getString(key, fallback) ?: fallback

    fun putText(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val FILE_NAME = "tiger_luck_quest_store"

        const val KEY_UNLOCKED_LEVELS = "unlocked_levels"
        const val KEY_STARS = "stars_"
        const val KEY_BEST_WARDS = "best_wards_"
        const val KEY_WAVES = "waves_repelled"
        const val KEY_JADE = "jade_earned"
        const val KEY_STREAK = "best_streak"
        const val KEY_ARCHERS = "archers_placed"
        const val KEY_PROWLERS = "prowlers_killed"
        const val KEY_SEALED = "gates_sealed"
        const val KEY_ROARS = "roars_used"
        const val KEY_SPRINTS = "sprints_survived"
        const val KEY_ZERO_WARDS = "zero_ward_losses"
        const val KEY_RELICS = "unlocked_relics"
        const val KEY_EQUIPPED = "equipped_relic"
        const val KEY_PATH_PREVIEW = "setting_path_preview"
        const val KEY_CONFIRM_TURN = "setting_confirm_turn"
        const val KEY_TURN_SPEED = "setting_turn_speed"
    }
}

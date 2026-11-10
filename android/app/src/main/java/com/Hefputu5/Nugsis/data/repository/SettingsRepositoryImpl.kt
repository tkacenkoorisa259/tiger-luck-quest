package com.Hefputu5.Nugsis.data.repository

import com.Hefputu5.Nugsis.data.local.PreferencesStore
import com.Hefputu5.Nugsis.domain.model.AppSettings
import com.Hefputu5.Nugsis.domain.model.TurnSpeed
import com.Hefputu5.Nugsis.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val store: PreferencesStore) : SettingsRepository {

    override fun load(): AppSettings = AppSettings(
        pathPreview = store.bool(PreferencesStore.KEY_PATH_PREVIEW, true),
        confirmTurnStart = store.bool(PreferencesStore.KEY_CONFIRM_TURN, false),
        turnSpeed = if (store.text(PreferencesStore.KEY_TURN_SPEED, TurnSpeed.NORMAL.name) == TurnSpeed.FAST.name) {
            TurnSpeed.FAST
        } else {
            TurnSpeed.NORMAL
        }
    )

    override fun setPathPreview(enabled: Boolean) {
        store.putBool(PreferencesStore.KEY_PATH_PREVIEW, enabled)
    }

    override fun setConfirmTurnStart(enabled: Boolean) {
        store.putBool(PreferencesStore.KEY_CONFIRM_TURN, enabled)
    }

    override fun setTurnSpeed(speed: TurnSpeed) {
        store.putText(PreferencesStore.KEY_TURN_SPEED, speed.name)
    }

    override fun reset() {
        store.putBool(PreferencesStore.KEY_PATH_PREVIEW, true)
        store.putBool(PreferencesStore.KEY_CONFIRM_TURN, false)
        store.putText(PreferencesStore.KEY_TURN_SPEED, TurnSpeed.NORMAL.name)
    }
}

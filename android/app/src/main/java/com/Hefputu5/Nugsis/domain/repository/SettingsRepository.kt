package com.Hefputu5.Nugsis.domain.repository

import com.Hefputu5.Nugsis.domain.model.AppSettings
import com.Hefputu5.Nugsis.domain.model.TurnSpeed

interface SettingsRepository {
    fun load(): AppSettings
    fun setPathPreview(enabled: Boolean)
    fun setConfirmTurnStart(enabled: Boolean)
    fun setTurnSpeed(speed: TurnSpeed)
    fun reset()
}

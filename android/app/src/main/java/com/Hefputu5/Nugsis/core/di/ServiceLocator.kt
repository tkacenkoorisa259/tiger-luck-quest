package com.Hefputu5.Nugsis.core.di

import android.content.Context
import com.Hefputu5.Nugsis.data.local.PreferencesStore
import com.Hefputu5.Nugsis.data.repository.ProgressRepositoryImpl
import com.Hefputu5.Nugsis.data.repository.RelicRepositoryImpl
import com.Hefputu5.Nugsis.data.repository.SettingsRepositoryImpl
import com.Hefputu5.Nugsis.domain.repository.ProgressRepository
import com.Hefputu5.Nugsis.domain.repository.RelicRepository
import com.Hefputu5.Nugsis.domain.repository.SettingsRepository
import com.Hefputu5.Nugsis.domain.usecase.CalculateStarsUseCase
import com.Hefputu5.Nugsis.domain.usecase.EvaluateRelicUnlocksUseCase
import com.Hefputu5.Nugsis.domain.usecase.GenerateBoardUseCase
import com.Hefputu5.Nugsis.domain.usecase.ResolveTurnUseCase

object ServiceLocator {

    private var progress: ProgressRepository? = null
    private var settings: SettingsRepository? = null
    private var relics: RelicRepository? = null

    val resolveTurn: ResolveTurnUseCase = ResolveTurnUseCase()
    val generateBoard: GenerateBoardUseCase = GenerateBoardUseCase(resolveTurn)
    val calculateStars: CalculateStarsUseCase = CalculateStarsUseCase()
    val evaluateRelics: EvaluateRelicUnlocksUseCase = EvaluateRelicUnlocksUseCase()

    fun init(context: Context) {
        if (progress != null) return
        val store = PreferencesStore(context.applicationContext)
        progress = ProgressRepositoryImpl(store)
        settings = SettingsRepositoryImpl(store)
        relics = RelicRepositoryImpl(store)
    }

    val progressRepository: ProgressRepository
        get() = checkNotNull(progress)

    val settingsRepository: SettingsRepository
        get() = checkNotNull(settings)

    val relicRepository: RelicRepository
        get() = checkNotNull(relics)
}

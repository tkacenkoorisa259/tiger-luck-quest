package com.Hefputu5.Nugsis.domain.usecase

import com.Hefputu5.Nugsis.domain.model.GameOutcome
import com.Hefputu5.Nugsis.domain.model.RunState

class CalculateStarsUseCase {

    operator fun invoke(state: RunState): Int {
        if (state.outcome != GameOutcome.VICTORY) return 0
        var stars = 1
        if (state.wards >= 2) stars += 1
        if (state.wards >= state.maxWards && state.gatesOpen.count { it } >= 2) stars += 1
        return stars
    }
}

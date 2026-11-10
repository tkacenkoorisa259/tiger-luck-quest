package com.Hefputu5.Nugsis.domain.model

enum class RelicId {
    JADE_FANG,
    SUN_DISC,
    BAMBOO_CHARM,
    TIGER_TOTEM,
    STONE_WARD,
    MIST_VEIL,
    AMBER_EYE,
    VINE_LOCK,
    SHADOW_PELT
}

data class RelicProgress(
    val id: RelicId,
    val unlocked: Boolean,
    val equipped: Boolean,
    val current: Int,
    val target: Int
)

data class PlayerProgress(
    val unlockedLevels: Int,
    val stars: List<Int>,
    val bestWards: List<Int>,
    val wavesRepelled: Int,
    val jadeEarned: Int,
    val bestStreak: Int,
    val archersPlaced: Int,
    val prowlersKilled: Int,
    val gatesSealed: Int,
    val roarsUsed: Int,
    val sprintsSurvived: Int,
    val zeroWardLosses: Int
) {
    val totalStars: Int get() = stars.sum()
    val clearedLevels: Int get() = stars.count { it > 0 }
}

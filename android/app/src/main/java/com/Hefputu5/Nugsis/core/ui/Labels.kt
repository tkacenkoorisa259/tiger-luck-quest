package com.Hefputu5.Nugsis.core.ui

import com.Hefputu5.Nugsis.R
import com.Hefputu5.Nugsis.domain.model.BeastType
import com.Hefputu5.Nugsis.domain.model.GuardianType
import com.Hefputu5.Nugsis.domain.model.RelicId

object Labels {

    fun levelName(index: Int): Int = when (index) {
        1 -> R.string.level_1
        2 -> R.string.level_2
        3 -> R.string.level_3
        4 -> R.string.level_4
        5 -> R.string.level_5
        6 -> R.string.level_6
        7 -> R.string.level_7
        else -> R.string.level_8
    }

    fun guardianName(type: GuardianType): Int = when (type) {
        GuardianType.SENTRY -> R.string.guardian_sentry
        GuardianType.ARCHER -> R.string.guardian_archer
        GuardianType.TOTEM -> R.string.guardian_totem
    }

    fun guardianSprite(type: GuardianType): Int = when (type) {
        GuardianType.SENTRY -> R.drawable.sprite_1
        GuardianType.ARCHER -> R.drawable.sprite_2
        GuardianType.TOTEM -> R.drawable.sprite_3
    }

    fun beastName(type: BeastType): Int = when (type) {
        BeastType.PROWLER -> R.string.beast_prowler
        BeastType.APE -> R.string.beast_ape
        BeastType.SHADOW -> R.string.beast_shadow
    }

    fun beastSprite(type: BeastType): Int = when (type) {
        BeastType.PROWLER -> R.drawable.sprite_4
        else -> R.drawable.sprite_5
    }

    fun relicName(id: RelicId): Int = when (id) {
        RelicId.JADE_FANG -> R.string.relic_1_name
        RelicId.SUN_DISC -> R.string.relic_2_name
        RelicId.BAMBOO_CHARM -> R.string.relic_3_name
        RelicId.TIGER_TOTEM -> R.string.relic_4_name
        RelicId.STONE_WARD -> R.string.relic_5_name
        RelicId.MIST_VEIL -> R.string.relic_6_name
        RelicId.AMBER_EYE -> R.string.relic_7_name
        RelicId.VINE_LOCK -> R.string.relic_8_name
        RelicId.SHADOW_PELT -> R.string.relic_9_name
    }

    fun relicPassive(id: RelicId): Int = when (id) {
        RelicId.JADE_FANG -> R.string.relic_1_passive
        RelicId.SUN_DISC -> R.string.relic_2_passive
        RelicId.BAMBOO_CHARM -> R.string.relic_3_passive
        RelicId.TIGER_TOTEM -> R.string.relic_4_passive
        RelicId.STONE_WARD -> R.string.relic_5_passive
        RelicId.MIST_VEIL -> R.string.relic_6_passive
        RelicId.AMBER_EYE -> R.string.relic_7_passive
        RelicId.VINE_LOCK -> R.string.relic_8_passive
        RelicId.SHADOW_PELT -> R.string.relic_9_passive
    }

    fun relicUnlock(id: RelicId): Int = when (id) {
        RelicId.JADE_FANG -> R.string.relic_1_unlock
        RelicId.SUN_DISC -> R.string.relic_2_unlock
        RelicId.BAMBOO_CHARM -> R.string.relic_3_unlock
        RelicId.TIGER_TOTEM -> R.string.relic_4_unlock
        RelicId.STONE_WARD -> R.string.relic_5_unlock
        RelicId.MIST_VEIL -> R.string.relic_6_unlock
        RelicId.AMBER_EYE -> R.string.relic_7_unlock
        RelicId.VINE_LOCK -> R.string.relic_8_unlock
        RelicId.SHADOW_PELT -> R.string.relic_9_unlock
    }

    fun relicIcon(id: RelicId): Int = when (id) {
        RelicId.JADE_FANG -> R.drawable.ic_relic_fang
        RelicId.SUN_DISC -> R.drawable.ic_relic_sun
        RelicId.BAMBOO_CHARM -> R.drawable.ic_relic_bamboo
        RelicId.TIGER_TOTEM -> R.drawable.ic_relic_totem
        RelicId.STONE_WARD -> R.drawable.ic_relic_ward
        RelicId.MIST_VEIL -> R.drawable.ic_relic_mist
        RelicId.AMBER_EYE -> R.drawable.ic_relic_eye
        RelicId.VINE_LOCK -> R.drawable.ic_relic_vine
        RelicId.SHADOW_PELT -> R.drawable.ic_relic_pelt
    }
}

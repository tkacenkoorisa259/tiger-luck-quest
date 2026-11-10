package com.Hefputu5.Nugsis.domain.repository

import com.Hefputu5.Nugsis.domain.model.RelicId

interface RelicRepository {
    fun unlocked(): Set<RelicId>
    fun equipped(): RelicId?
    fun unlock(ids: List<RelicId>)
    fun equip(id: RelicId?)
    fun reset()
}

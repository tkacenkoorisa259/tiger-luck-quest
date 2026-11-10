package com.Hefputu5.Nugsis.data.repository

import com.Hefputu5.Nugsis.data.local.PreferencesStore
import com.Hefputu5.Nugsis.domain.model.RelicId
import com.Hefputu5.Nugsis.domain.repository.RelicRepository

class RelicRepositoryImpl(private val store: PreferencesStore) : RelicRepository {

    override fun unlocked(): Set<RelicId> =
        store.text(PreferencesStore.KEY_RELICS, "")
            .split(SEPARATOR)
            .filter { it.isNotBlank() }
            .mapNotNull { name -> RelicId.entries.firstOrNull { it.name == name } }
            .toSet()

    override fun equipped(): RelicId? {
        val stored = store.text(PreferencesStore.KEY_EQUIPPED, "")
        if (stored.isBlank()) return null
        val relic = RelicId.entries.firstOrNull { it.name == stored } ?: return null
        return if (relic in unlocked()) relic else null
    }

    override fun unlock(ids: List<RelicId>) {
        if (ids.isEmpty()) return
        val merged = unlocked() + ids
        store.putText(PreferencesStore.KEY_RELICS, merged.joinToString(SEPARATOR) { it.name })
    }

    override fun equip(id: RelicId?) {
        store.putText(PreferencesStore.KEY_EQUIPPED, id?.name.orEmpty())
    }

    override fun reset() {
        store.putText(PreferencesStore.KEY_RELICS, "")
        store.putText(PreferencesStore.KEY_EQUIPPED, "")
    }

    private companion object {
        const val SEPARATOR = ","
    }
}

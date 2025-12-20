package com.lalit.countanything.ui.models

import java.util.UUID

/**
 * Represents a user-created counter.
 */
enum class CounterType {
    STANDARD,
    CURRENCY
}

data class Counter(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val count: Int = 0,
    val type: CounterType = CounterType.STANDARD, // NEW: Defaults to Standard
    val history: Map<String, Int> = emptyMap() // Date (YYYY-MM-DD) -> Count
)

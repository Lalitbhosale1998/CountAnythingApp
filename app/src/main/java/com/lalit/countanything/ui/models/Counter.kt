package com.lalit.countanything.ui.models

import java.util.UUID

/**
 * Represents a user-created counter.
 */
enum class CounterType {
    STANDARD,
    FINANCE_COUNTDOWN,
    FINANCE_BUDGET_HUB,
    FINANCE_CUMULATIVE,
    SEXUAL_HEALTH
}

data class Counter(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val count: Float = 0f,
    val type: CounterType = CounterType.STANDARD,
    val history: Map<String, Float> = emptyMap(), // Date (YYYY-MM-DD) -> Count
    val targetDate: String? = null, // For FINANCE_COUNTDOWN (YYYY-MM-DD)
    val monthlySalaries: Map<String, Float> = emptyMap(), // For FINANCE_BUDGET_HUB
    val monthlySavings: Map<String, Float> = emptyMap() // For FINANCE_BUDGET_HUB
)

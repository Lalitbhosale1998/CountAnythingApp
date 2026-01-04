package com.lalit.countanything.utils

import com.lalit.countanything.R

object SexualHealthConstants {
    const val THRESHOLD_LOW = 1
    const val THRESHOLD_HIGH = 7

    val BALANCED_FACTS = listOf(
        R.string.sh_feedback_low_1,
        R.string.sh_feedback_low_2,
        R.string.sh_feedback_low_3,
        R.string.sh_feedback_low_4
    )

    val HIGH_FREQUENCY_WARNINGS = listOf(
        R.string.sh_warning_high_1,
        R.string.sh_warning_high_2,
        R.string.sh_warning_high_3,
        R.string.sh_warning_high_4
    )

    val LOW_FREQUENCY_FACTS = listOf(
        R.string.sh_fact_normal_1,
        R.string.sh_fact_normal_2,
        R.string.sh_fact_normal_3
    )

    fun getFeedback(weeklyCount: Int): Int {
        return when {
            weeklyCount > THRESHOLD_HIGH -> HIGH_FREQUENCY_WARNINGS.random()
            weeklyCount >= THRESHOLD_LOW -> BALANCED_FACTS.random()
            else -> LOW_FREQUENCY_FACTS.random()
        }
    }
}

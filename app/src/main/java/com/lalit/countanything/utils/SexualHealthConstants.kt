package com.lalit.countanything.utils

object SexualHealthConstants {
    const val THRESHOLD_LOW = 1
    const val THRESHOLD_HIGH = 7

    val BALANCED_FACTS = listOf(
        "Regular activity can help reduce stress and improve mood.",
        "Better sleep and lower blood pressure are linked to balanced sexual health.",
        "It can boost your immune system and heart health.",
        "Endorphins released can act as a natural pain reliever."
    )

    val HIGH_FREQUENCY_WARNINGS = listOf(
        "Excessive frequency may lead to physical soreness or skin irritation.",
        "Watch out for signs of physical desensitization; moderation is key.",
        "If it interferes with daily life or sleep, consider a more balanced routine.",
        "Consult a doctor if you feel it's becoming a compulsive habit."
    )

    val LOW_FREQUENCY_FACTS = listOf(
        "It's normal for libido to fluctuate based on stress, diet, or sleep.",
        "Low frequency is perfectly healthy and varies from person to person.",
        "Taking a break can sometimes reset physical sensitivity."
    )

    fun getFeedback(weeklyCount: Int): String {
        return when {
            weeklyCount > THRESHOLD_HIGH -> HIGH_FREQUENCY_WARNINGS.random()
            weeklyCount >= THRESHOLD_LOW -> BALANCED_FACTS.random()
            else -> LOW_FREQUENCY_FACTS.random()
        }
    }
}

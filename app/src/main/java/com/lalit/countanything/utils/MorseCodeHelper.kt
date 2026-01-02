package com.lalit.countanything.utils

object MorseCodeHelper {
    enum class Signal {
        DOT, DASH, SPACE_LETTER, SPACE_WORD
    }

    private val charMap = mapOf(
        'A' to listOf(Signal.DOT, Signal.DASH),
        'B' to listOf(Signal.DASH, Signal.DOT, Signal.DOT, Signal.DOT),
        'C' to listOf(Signal.DASH, Signal.DOT, Signal.DASH, Signal.DOT),
        'D' to listOf(Signal.DASH, Signal.DOT, Signal.DOT),
        'E' to listOf(Signal.DOT),
        'F' to listOf(Signal.DOT, Signal.DOT, Signal.DASH, Signal.DOT),
        'G' to listOf(Signal.DASH, Signal.DASH, Signal.DOT),
        'H' to listOf(Signal.DOT, Signal.DOT, Signal.DOT, Signal.DOT),
        'I' to listOf(Signal.DOT, Signal.DOT),
        'J' to listOf(Signal.DOT, Signal.DASH, Signal.DASH, Signal.DASH),
        'K' to listOf(Signal.DASH, Signal.DOT, Signal.DASH),
        'L' to listOf(Signal.DOT, Signal.DASH, Signal.DOT, Signal.DOT),
        'M' to listOf(Signal.DASH, Signal.DASH),
        'N' to listOf(Signal.DASH, Signal.DOT),
        'O' to listOf(Signal.DASH, Signal.DASH, Signal.DASH),
        'P' to listOf(Signal.DOT, Signal.DASH, Signal.DASH, Signal.DOT),
        'Q' to listOf(Signal.DASH, Signal.DASH, Signal.DOT, Signal.DASH),
        'R' to listOf(Signal.DOT, Signal.DASH, Signal.DOT),
        'S' to listOf(Signal.DOT, Signal.DOT, Signal.DOT),
        'T' to listOf(Signal.DASH),
        'U' to listOf(Signal.DOT, Signal.DOT, Signal.DASH),
        'V' to listOf(Signal.DOT, Signal.DOT, Signal.DOT, Signal.DASH),
        'W' to listOf(Signal.DOT, Signal.DASH, Signal.DASH),
        'X' to listOf(Signal.DASH, Signal.DOT, Signal.DOT, Signal.DASH),
        'Y' to listOf(Signal.DASH, Signal.DOT, Signal.DASH, Signal.DASH),
        'Z' to listOf(Signal.DASH, Signal.DASH, Signal.DOT, Signal.DOT),
        '0' to listOf(Signal.DASH, Signal.DASH, Signal.DASH, Signal.DASH, Signal.DASH),
        '1' to listOf(Signal.DOT, Signal.DASH, Signal.DASH, Signal.DASH, Signal.DASH),
        '2' to listOf(Signal.DOT, Signal.DOT, Signal.DASH, Signal.DASH, Signal.DASH),
        '3' to listOf(Signal.DOT, Signal.DOT, Signal.DOT, Signal.DASH, Signal.DASH),
        '4' to listOf(Signal.DOT, Signal.DOT, Signal.DOT, Signal.DOT, Signal.DASH),
        '5' to listOf(Signal.DOT, Signal.DOT, Signal.DOT, Signal.DOT, Signal.DOT),
        '6' to listOf(Signal.DASH, Signal.DOT, Signal.DOT, Signal.DOT, Signal.DOT),
        '7' to listOf(Signal.DASH, Signal.DASH, Signal.DOT, Signal.DOT, Signal.DOT),
        '8' to listOf(Signal.DASH, Signal.DASH, Signal.DASH, Signal.DOT, Signal.DOT),
        '9' to listOf(Signal.DASH, Signal.DASH, Signal.DASH, Signal.DASH, Signal.DOT)
    )

    fun toMorse(text: String): List<Signal> {
        val signals = mutableListOf<Signal>()
        val normalized = text.uppercase().trim()
        
        normalized.forEachIndexed { index, char ->
            if (char == ' ') {
                signals.add(Signal.SPACE_WORD)
            } else {
                val charSignals = charMap[char]
                if (charSignals != null) {
                    signals.addAll(charSignals)
                    // Add letter gap if not last char and next is not space
                    if (index < normalized.length - 1 && normalized[index + 1] != ' ') {
                        signals.add(Signal.SPACE_LETTER)
                    }
                }
            }
        }
        return signals
    }
}

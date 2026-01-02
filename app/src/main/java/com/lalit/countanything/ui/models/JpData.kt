package com.lalit.countanything.ui.models

import kotlinx.serialization.Serializable

@Serializable
data class Kanji(
    val character: String,
    val onyomi: List<String>,
    val kunyomi: List<String>,
    val meaning: String
)

@Serializable
data class Vocab(
    val word: String,
    val reading: String,
    val meaning: String
)

@Serializable
data class Grammar(
    val pattern: String,
    val meaning: String,
    val example: String
)

@Serializable
data class JpCounter(
    val kanji: String,
    val reading: String,
    val usage: String,
    val examples: String
)

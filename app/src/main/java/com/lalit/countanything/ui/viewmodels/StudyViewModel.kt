package com.lalit.countanything.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lalit.countanything.ui.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = JpRepository(application)
    private val prefs = application.getSharedPreferences("study_progress", android.content.Context.MODE_PRIVATE)

    private val _kanjiList = MutableStateFlow<List<Kanji>>(emptyList())
    val kanjiList: StateFlow<List<Kanji>> = _kanjiList.asStateFlow()

    private val _vocabList = MutableStateFlow<List<Vocab>>(emptyList())
    val vocabList: StateFlow<List<Vocab>> = _vocabList.asStateFlow()

    private val _grammarList = MutableStateFlow<List<Grammar>>(emptyList())
    val grammarList: StateFlow<List<Grammar>> = _grammarList.asStateFlow()

    private val _learnedKanji = MutableStateFlow<Set<String>>(emptySet())
    val learnedKanji: StateFlow<Set<String>> = _learnedKanji.asStateFlow()

    private val _learnedVocab = MutableStateFlow<Set<String>>(emptySet())
    val learnedVocab: StateFlow<Set<String>> = _learnedVocab.asStateFlow()
    
    private val _learnedGrammar = MutableStateFlow<Set<String>>(emptySet())
    val learnedGrammar: StateFlow<Set<String>> = _learnedGrammar.asStateFlow()

    // Separate lists for User Content to easily save/append
    private var userKanjiList: MutableList<Kanji> = mutableListOf()
    private var userVocabList: MutableList<Vocab> = mutableListOf()
    private var userGrammarList: MutableList<Grammar> = mutableListOf()

    init {
        loadData()
        loadProgress()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Load Asset Data
            val assetKanji = repository.loadKanji()
            val assetVocab = repository.loadVocab()
            val assetGrammar = repository.loadGrammar()

            // Load User Data
            userKanjiList = repository.loadUserKanji().toMutableList()
            userVocabList = repository.loadUserVocab().toMutableList()
            userGrammarList = repository.loadUserGrammar().toMutableList()

            // Combine and Emit (User items override asset items)
            _kanjiList.value = (userKanjiList + assetKanji).distinctBy { it.character }
            _vocabList.value = (userVocabList + assetVocab).distinctBy { it.word }
            _grammarList.value = (userGrammarList + assetGrammar).distinctBy { it.pattern }
        }
    }

    fun addKanji(character: String, meaning: String, onyomi: String, kunyomi: String, example: String = "") {
        val newItem = Kanji(
            character = character,
            meaning = meaning,
            onyomi = onyomi.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            kunyomi = kunyomi.split(",").map { it.trim() }.filter { it.isNotEmpty() },
            example = example
        )
        // Add to user list and save
        userKanjiList.removeAll { it.character == character } // Remove existing if any (update case)
        userKanjiList.add(0, newItem) // Add to top
        repository.saveUserKanji(userKanjiList)
        
        // Update combined flow
        val currentList = _kanjiList.value.toMutableList()
        currentList.removeAll { it.character == character }
        currentList.add(0, newItem)
        _kanjiList.value = currentList
    }

    fun updateKanji(originalChar: String, newMeaning: String, newOn: String, newKun: String, newExample: String) {
        // reuse addKanji which handles override/update logic since we use distinctBy and removeAll
        addKanji(originalChar, newMeaning, newOn, newKun, newExample)
    }

    fun addVocab(word: String, reading: String, meaning: String, example: String = "") {
        val newItem = Vocab(word, reading, meaning, example)
        userVocabList.removeAll { it.word == word }
        userVocabList.add(0, newItem)
        repository.saveUserVocab(userVocabList)
        
        val currentList = _vocabList.value.toMutableList()
        currentList.removeAll { it.word == word }
        currentList.add(0, newItem)
        _vocabList.value = currentList
    }

    fun updateVocab(originalWord: String, reading: String, meaning: String, example: String) {
        addVocab(originalWord, reading, meaning, example)
    }

    fun addGrammar(pattern: String, meaning: String, example: String) {
        val newItem = Grammar(pattern, meaning, example)
        userGrammarList.removeAll { it.pattern == pattern }
        userGrammarList.add(0, newItem)
        repository.saveUserGrammar(userGrammarList)
        
        val currentList = _grammarList.value.toMutableList()
        currentList.removeAll { it.pattern == pattern }
        currentList.add(0, newItem)
        _grammarList.value = currentList
    }

    fun updateGrammar(originalPattern: String, meaning: String, example: String) {
        addGrammar(originalPattern, meaning, example)
    }

    private fun loadProgress() {
        _learnedKanji.value = prefs.getStringSet("learned_kanji", emptySet()) ?: emptySet()
        _learnedVocab.value = prefs.getStringSet("learned_vocab", emptySet()) ?: emptySet()
        _learnedGrammar.value = prefs.getStringSet("learned_grammar", emptySet()) ?: emptySet()
    }

    fun markKanjiLearned(char: String, learned: Boolean) {
        val current = _learnedKanji.value.toMutableSet()
        if (learned) current.add(char) else current.remove(char)
        _learnedKanji.value = current
        saveSet("learned_kanji", current)
    }

    fun markVocabLearned(word: String, learned: Boolean) {
        val current = _learnedVocab.value.toMutableSet()
        if (learned) current.add(word) else current.remove(word)
        _learnedVocab.value = current
        saveSet("learned_vocab", current)
    }

    fun markGrammarLearned(pattern: String, learned: Boolean) {
        val current = _learnedGrammar.value.toMutableSet()
        if (learned) current.add(pattern) else current.remove(pattern)
        _learnedGrammar.value = current
        saveSet("learned_grammar", current)
    }

    private fun saveSet(key: String, set: Set<String>) {
        prefs.edit().putStringSet(key, set).apply()
    }
    
    fun getOverallProgress(): Float {
        val total = _kanjiList.value.size + _vocabList.value.size + _grammarList.value.size
        if (total == 0) return 0f
        val learned = _learnedKanji.value.size + _learnedVocab.value.size + _learnedGrammar.value.size
        return learned.toFloat() / total
    }
}

package com.lalit.countanything.ui.models

import android.content.Context
import org.json.JSONArray
import java.io.IOException

class JpRepository(private val context: Context) {

    fun loadKanji(): List<Kanji> {
        val list = mutableListOf<Kanji>()
        
        // Try loading JSON first (if exists)
        try {
            val jsonString = loadJSONFromAsset("n2_kanji.json")
            if (jsonString != "[]" && jsonString.isNotEmpty()) {
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val onyomiArray = obj.optJSONArray("onyomi")
                    val kunyomiArray = obj.optJSONArray("kunyomi")
                    val onyomiList = mutableListOf<String>()
                    val kunyomiList = mutableListOf<String>()
                    if (onyomiArray != null) {
                        for (j in 0 until onyomiArray.length()) onyomiList.add(onyomiArray.getString(j))
                    }
                    if (kunyomiArray != null) {
                         for (j in 0 until kunyomiArray.length()) kunyomiList.add(kunyomiArray.getString(j))
                    }
                    list.add(Kanji(obj.getString("character"), onyomiList, kunyomiList, obj.getString("meaning")))
                }
            }
        } catch (e: Exception) {
            // Ignore if JSON fails/doesn't exist
        }

        // Try loading CSV
        try {
            val inputStream = context.assets.open("n2_kanji.csv")
            val reader = inputStream.bufferedReader()
            val lines = reader.readLines()
            
            var currentKanji: Kanji? = null
            var currentMeanings = StringBuilder()
            
            // Skip first 3 lines (Header metadata)
            for (i in 3 until lines.size) {
                val line = lines[i]
                if (line.isBlank()) continue
                
                val parts = parseCSVLine(line)
                // Expected format: Index, Kanji, Onyomi, Kunyomi, Meaning, Vocab
                // Empty Index means continuation of previous row
                
                if (parts.isNotEmpty() && parts[0].isNotBlank()) {
                    // Save previous if exists
                    if (currentKanji != null) {
                        list.add(currentKanji.copy(meaning = currentMeanings.toString().trim().removeSuffix(",")))
                    }
                    
                    // Start new Kanji
                    currentMeanings = StringBuilder()
                    if (parts.size >= 5) {
                         currentMeanings.append(parts[4])
                         currentKanji = Kanji(
                             character = parts[1],
                             onyomi = parts[2].split(",").map{ it.trim() }.filter { it.isNotEmpty() },
                             kunyomi = parts[3].split(",").map{ it.trim() }.filter { it.isNotEmpty() },
                             meaning = "" // Will simplify deferred assignment
                         )
                    }
                } else {
                    // Continuation line
                     if (parts.size >= 5) {
                         if (currentMeanings.isNotEmpty()) currentMeanings.append(" ")
                         currentMeanings.append(parts[4])
                     }
                }
            }
            // Add last one
            if (currentKanji != null) {
                list.add(currentKanji.copy(meaning = currentMeanings.toString().trim().removeSuffix(",")))
            }
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return list
    }

    fun loadVocab(): List<Vocab> {
        return try {
            val list = mutableListOf<Vocab>()
            val assetFileName = "n2_vocab.csv" 
            val inputStream = context.assets.open(assetFileName)
            val reader = inputStream.bufferedReader()
            reader.useLines { lines ->
                lines.forEach { line ->
                    if (line.isNotBlank()) {
                         val parts = parseCSVLine(line)
                         // Format: Word, Reading, Meaning, Level
                         if (parts.size >= 3) {
                             list.add(
                                 Vocab(
                                     word = parts[0],
                                     reading = parts[1],
                                     meaning = parts[2]
                                 )
                             )
                         }
                    }
                }
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parseCSVLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var start = 0
        var inQuotes = false
        for (current in 0 until line.length) {
             if (line[current] == '\"') {
                 inQuotes = !inQuotes
             } else if (line[current] == ',' && !inQuotes) {
                 var field = line.substring(start, current)
                 if (field.startsWith("\"") && field.endsWith("\"") && field.length >= 2) {
                     field = field.substring(1, field.length - 1)
                     field = field.replace("\"\"", "\"")
                 }
                 result.add(field.trim())
                 start = current + 1
             }
        }
        var lastField = line.substring(start)
        if (lastField.startsWith("\"") && lastField.endsWith("\"") && lastField.length >= 2) {
             lastField = lastField.substring(1, lastField.length - 1)
             lastField = lastField.replace("\"\"", "\"")
        }
        result.add(lastField.trim())
        return result
    }

    fun loadGrammar(): List<Grammar> {
         return try {
            val jsonString = loadJSONFromAsset("n2_grammar.json")
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<Grammar>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Grammar(
                        pattern = obj.getString("pattern"),
                        meaning = obj.getString("meaning"),
                        example = obj.getString("example")
                    )
                )
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun loadJSONFromAsset(filename: String): String {
        return try {
            val inputStream = context.assets.open(filename)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            "[]"
        }
    }
    // --- User Added Content Persistence ---

    fun saveUserKanji(list: List<Kanji>) {
        val jsonArray = JSONArray()
        list.forEach { item ->
            val obj = org.json.JSONObject()
            obj.put("character", item.character)
            obj.put("meaning", item.meaning)
            obj.put("onyomi", JSONArray(item.onyomi))
            obj.put("kunyomi", JSONArray(item.kunyomi))
            jsonArray.put(obj)
        }
        saveJsonToFile("user_kanji.json", jsonArray.toString())
    }

    fun loadUserKanji(): List<Kanji> {
        val jsonString = loadJsonFromFile("user_kanji.json")
        if (jsonString.isBlank()) return emptyList()
        
        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<Kanji>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val onyomiList = mutableListOf<String>()
                val kunyomiList = mutableListOf<String>()
                
                val onyomiArray = obj.optJSONArray("onyomi")
                if (onyomiArray != null) {
                    for (j in 0 until onyomiArray.length()) onyomiList.add(onyomiArray.getString(j))
                }
                
                val kunyomiArray = obj.optJSONArray("kunyomi")
                if (kunyomiArray != null) {
                    for (j in 0 until kunyomiArray.length()) kunyomiList.add(kunyomiArray.getString(j))
                }

                list.add(
                    Kanji(
                        character = obj.getString("character"),
                        onyomi = onyomiList,
                        kunyomi = kunyomiList,
                        meaning = obj.getString("meaning")
                    )
                )
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun saveUserVocab(list: List<Vocab>) {
        val jsonArray = JSONArray()
        list.forEach { item ->
            val obj = org.json.JSONObject()
            obj.put("word", item.word)
            obj.put("reading", item.reading)
            obj.put("meaning", item.meaning)
            jsonArray.put(obj)
        }
        saveJsonToFile("user_vocab.json", jsonArray.toString())
    }

    fun loadUserVocab(): List<Vocab> {
        val jsonString = loadJsonFromFile("user_vocab.json")
        if (jsonString.isBlank()) return emptyList()

        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<Vocab>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Vocab(
                        word = obj.getString("word"),
                        reading = obj.getString("reading"),
                        meaning = obj.getString("meaning")
                    )
                )
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun saveUserGrammar(list: List<Grammar>) {
         val jsonArray = JSONArray()
        list.forEach { item ->
            val obj = org.json.JSONObject()
            obj.put("pattern", item.pattern)
            obj.put("meaning", item.meaning)
            obj.put("example", item.example)
            jsonArray.put(obj)
        }
        saveJsonToFile("user_grammar.json", jsonArray.toString())
    }

    fun loadUserGrammar(): List<Grammar> {
         val jsonString = loadJsonFromFile("user_grammar.json")
        if (jsonString.isBlank()) return emptyList()

        return try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<Grammar>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Grammar(
                        pattern = obj.getString("pattern"),
                        meaning = obj.getString("meaning"),
                        example = obj.getString("example")
                    )
                )
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun saveJsonToFile(filename: String, json: String) {
        try {
            context.openFileOutput(filename, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadJsonFromFile(filename: String): String {
        return try {
            val file = context.getFileStreamPath(filename)
            if (file.exists()) {
                context.openFileInput(filename).bufferedReader().use { it.readText() }
            } else {
                ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}

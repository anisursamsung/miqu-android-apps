package com.miqu.android.recitation.data

import android.content.Context
import com.miqu.android.recitation.model.MushafLine
import com.miqu.android.recitation.model.MushafPage
import com.miqu.android.recitation.model.MushafType
import java.io.BufferedReader
import java.io.InputStreamReader

class MushafRepository(private val context: Context) {

    companion object {
        @Volatile
        private var instance: MushafRepository? = null

        fun getInstance(context: Context): MushafRepository {
            return instance ?: synchronized(this) {
                instance ?: MushafRepository(context.applicationContext).also { instance = it }
            }
        }
    }

    private val cachedPages = mutableMapOf<MushafType, List<MushafPage>>()

    fun getPages(type: MushafType): List<MushafPage> {
        return cachedPages.getOrPut(type) {
            loadPages(type)
        }
    }

    fun getPage(type: MushafType, pageNumber: Int): MushafPage? {
        val pages = getPages(type)
        return pages.getOrNull(pageNumber - 1)
    }

    private fun loadPages(type: MushafType): List<MushafPage> {
        val (textAsset, juzAsset) = when (type) {
            MushafType.KING_FAHAD -> "texts/king_fahad.txt" to "texts/juz_king_fahad.txt"
            MushafType.INDO_PAK -> "texts/indo_pak_15_lines_huffaz.txt" to "texts/juz_fifteen_ind_pak.txt"
        }

        val juzMap = parseJuzMap(juzAsset)
        val content = context.assets.open(textAsset).bufferedReader().use { it.readText() }

        val rawPages = content.split("page:")
        if (rawPages.size <= 1) return emptyList()

        val pages = ArrayList<MushafPage>(rawPages.size - 1)
        var currentSurah = "Al-Fatihah"

        for (i in 1 until rawPages.size) {
            val chunk = rawPages[i]
            val lines = chunk.split("\n").map { it.trim() }.toMutableList()
            if (lines.isEmpty()) continue

            val pageNum = lines[0].toIntOrNull() ?: i
            val contentLines = lines.subList(1, lines.size).toMutableList()
            while (contentLines.isNotEmpty() && contentLines.last().isEmpty()) {
                contentLines.removeAt(contentLines.size - 1)
            }

            val pageLines = ArrayList<MushafLine>(contentLines.size)
            for (line in contentLines) {
                when {
                    line == "empty_verse" -> pageLines.add(MushafLine.EmptySpacer)
                    line == "bismillah" -> pageLines.add(MushafLine.Bismillah)
                    line.startsWith("surah:") -> {
                        val parts = line.split(":")
                        val sNum = parts.getOrNull(1)?.toIntOrNull() ?: 1
                        val sName = parts.getOrNull(2) ?: "Surah $sNum"
                        currentSurah = sName
                        pageLines.add(MushafLine.SurahHeader(sNum, sName))
                    }
                    else -> pageLines.add(MushafLine.TextLine(line))
                }
            }

            val juzNum = juzMap[pageNum] ?: 1
            pages.add(
                MushafPage(
                    pageNumber = pageNum,
                    juzNumber = juzNum,
                    surahName = currentSurah,
                    lines = pageLines
                )
            )
        }

        return pages
    }

    private fun parseJuzMap(assetPath: String): Map<Int, Int> {
        val map = HashMap<Int, Int>(604)
        try {
            context.assets.open(assetPath).bufferedReader().useLines { lines ->
                for (line in lines) {
                    val trimmed = line.trim()
                    if (trimmed.isEmpty()) continue
                    val parts = trimmed.split(":")
                    if (parts.size >= 2) {
                        val juzNum = parts[0].toIntOrNull() ?: continue
                        val rangeParts = parts[1].split("-")
                        if (rangeParts.size >= 2) {
                            val start = rangeParts[0].toIntOrNull() ?: continue
                            val end = rangeParts[1].toIntOrNull() ?: continue
                            for (p in start..end) {
                                map[p] = juzNum
                            }
                        }
                    }
                }
            }
        } catch (_: Exception) {}
        return map
    }
}

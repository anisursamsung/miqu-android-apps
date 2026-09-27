package com.miqu.android.recitation

import com.miqu.android.recitation.model.MushafLine
import com.miqu.android.recitation.model.MushafPage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class MushafParserTest {

    @Test
    fun testKingFahadTotalPagesAndLineCount() {
        val file = File("src/main/assets/texts/king_fahad.txt")
        assertTrue("king_fahad.txt must exist", file.exists())

        val content = file.readText()
        val rawPages = content.split("page:")
        assertEquals("King Fahad must have 604 pages", 605, rawPages.size)

        for (i in 1 until rawPages.size) {
            val lines = rawPages[i].split("\n").map { it.trim() }.toMutableList()
            val contentLines = lines.subList(1, lines.size).toMutableList()
            while (contentLines.isNotEmpty() && contentLines.last().isEmpty()) {
                contentLines.removeAt(contentLines.size - 1)
            }
            assertEquals("Page $i must have exactly 15 lines", 15, contentLines.size)
        }
    }

    @Test
    fun testJuzMapping() {
        val file = File("src/main/assets/texts/juz_king_fahad.txt")
        assertTrue("juz_king_fahad.txt must exist", file.exists())

        val juzMap = mutableMapOf<Int, Int>()
        file.forEachLine { line ->
            val trimmed = line.trim()
            if (trimmed.isNotEmpty()) {
                val parts = trimmed.split(":")
                val juz = parts[0].toInt()
                val range = parts[1].split("-")
                val start = range[0].toInt()
                val end = range[1].toInt()
                for (p in start..end) {
                    juzMap[p] = juz
                }
            }
        }

        assertEquals(604, juzMap.size)
        assertEquals(1, juzMap[1])
        assertEquals(1, juzMap[21])
        assertEquals(2, juzMap[22])
        assertEquals(30, juzMap[604])
    }

    @Test
    fun testIndoPakTotalPagesAndLineCount() {
        val file = File("src/main/assets/texts/indo_pak_15_lines_huffaz.txt")
        assertTrue("indo_pak_15_lines_huffaz.txt must exist", file.exists())

        val content = file.readText()
        val rawPages = content.split("page:")
        assertEquals("Indo-Pak must have 610 pages", 611, rawPages.size)

        for (i in 1 until rawPages.size) {
            val lines = rawPages[i].split("\n").map { it.trim() }.toMutableList()
            val contentLines = lines.subList(1, lines.size).toMutableList()
            while (contentLines.isNotEmpty() && contentLines.last().isEmpty()) {
                contentLines.removeAt(contentLines.size - 1)
            }
            assertEquals("Indo-Pak Page $i must have exactly 15 lines", 15, contentLines.size)
        }
    }

    @Test
    fun testIndoPakJuzMapping() {
        val file = File("src/main/assets/texts/juz_fifteen_ind_pak.txt")
        assertTrue("juz_fifteen_ind_pak.txt must exist", file.exists())

        val juzMap = mutableMapOf<Int, Int>()
        file.forEachLine { line ->
            val trimmed = line.trim()
            if (trimmed.isNotEmpty()) {
                val parts = trimmed.split(":")
                val juz = parts[0].toInt()
                val range = parts[1].split("-")
                val start = range[0].toInt()
                val end = range[1].toInt()
                for (p in start..end) {
                    juzMap[p] = juz
                }
            }
        }

        assertEquals(610, juzMap.size)
        assertEquals(1, juzMap[1])
        assertEquals(1, juzMap[21])
        assertEquals(2, juzMap[22])
        assertEquals(29, juzMap[562])
        assertEquals(29, juzMap[585])
        assertEquals(30, juzMap[586])
        assertEquals(30, juzMap[610])
    }
}

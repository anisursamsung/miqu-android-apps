package com.miqu.android.recitation.model

enum class MushafType(val id: String, val displayName: String, val totalPages: Int) {
    KING_FAHAD("king_fahad", "King Fahad Mushaf (Madinah)", 604),
    INDO_PAK("indo_pak", "Indo-Pak Mushaf (South Asian)", 610)
}

sealed class MushafLine {
    data class TextLine(val text: String) : MushafLine()
    data class SurahHeader(val surahNumber: Int, val surahName: String) : MushafLine()
    object Bismillah : MushafLine()
    object EmptySpacer : MushafLine()
}

data class MushafPage(
    val pageNumber: Int,
    val juzNumber: Int,
    val surahName: String,
    val lines: List<MushafLine>
)

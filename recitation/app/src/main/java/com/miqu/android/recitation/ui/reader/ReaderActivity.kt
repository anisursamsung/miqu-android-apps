package com.miqu.android.recitation.ui.reader

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.miqu.android.recitation.data.QuranDatabaseHelper
import com.miqu.android.recitation.data.UserSettings
import com.miqu.android.recitation.databinding.ActivityReaderBinding
import com.miqu.android.recitation.model.Verse
import kotlin.concurrent.thread

class ReaderActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SURAH_ID = "extra_surah_id"
        const val EXTRA_SURAH_NAME = "extra_surah_name"
        const val EXTRA_SURAH_TRANSLITERATION = "extra_surah_transliteration"
        const val EXTRA_TOTAL_VERSES = "extra_total_verses"
        const val EXTRA_TARGET_VERSE = "extra_target_verse"
    }

    private lateinit var binding: ActivityReaderBinding
    private lateinit var quranDbHelper: QuranDatabaseHelper
    private lateinit var userSettings: UserSettings
    private lateinit var adapter: VerseAdapter
    private var verses: List<Verse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityReaderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { v, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = statusBars.top)
            insets
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.toolbar.inflateMenu(com.miqu.android.recitation.R.menu.menu_reader)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                com.miqu.android.recitation.R.id.action_appearance -> {
                    val sheet = ReaderAppearanceBottomSheetFragment {
                        adapter.notifyDataSetChanged()
                    }
                    sheet.show(supportFragmentManager, "AppearanceSheet")
                    true
                }
                com.miqu.android.recitation.R.id.action_settings -> {
                    val intent = android.content.Intent(this, com.miqu.android.recitation.ui.settings.SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        val surahId = intent.getIntExtra(EXTRA_SURAH_ID, 1)
        val surahName = intent.getStringExtra(EXTRA_SURAH_NAME) ?: ""
        val transliteration = intent.getStringExtra(EXTRA_SURAH_TRANSLITERATION) ?: "Surah $surahId"
        val totalVerses = intent.getIntExtra(EXTRA_TOTAL_VERSES, 0)
        val targetVerse = intent.getIntExtra(EXTRA_TARGET_VERSE, -1)

        binding.toolbar.title = "$transliteration ($surahName)"
        binding.toolbar.subtitle = if (totalVerses > 0) "$totalVerses Verses • Surah #$surahId" else "Surah #$surahId"

        quranDbHelper = QuranDatabaseHelper.getInstance(this)
        userSettings = UserSettings(this)

        adapter = VerseAdapter(
            context = this,
            userSettings = userSettings,
            onMorphologyClick = { verse ->
                val sheet = MorphologyBottomSheetFragment.newInstance(verse.surahNumber, verse.verseNumber)
                sheet.show(supportFragmentManager, "MorphologySheet")
            },
            onTafsirClick = { verse ->
                val sheet = TafsirBottomSheetFragment.newInstance(verse.surahNumber, verse.verseNumber, verse.id)
                sheet.show(supportFragmentManager, "TafsirSheet")
            }
        )

        binding.recyclerViewVerses.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewVerses.adapter = adapter

        thread(start = true, name = "verses-loader") {
            verses = quranDbHelper.getVersesForSurah(surahId)
            runOnUiThread {
                adapter.submitList(verses)
                if (targetVerse > 0) {
                    val targetPos = verses.indexOfFirst { it.verseNumber == targetVerse }
                    if (targetPos != -1) {
                        binding.recyclerViewVerses.scrollToPosition(targetPos)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) {
            adapter.notifyDataSetChanged()
        }
    }
}

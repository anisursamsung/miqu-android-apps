package com.miqu.android.recitation.ui.reader

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.miqu.android.recitation.data.RootsDatabaseHelper
import com.miqu.android.recitation.data.SurahRepository
import com.miqu.android.recitation.databinding.ActivityWordDetailBinding
import kotlin.concurrent.thread

class WordDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ARABIC_WORD = "extra_arabic_word"
        const val EXTRA_CURRENT_MEANING = "extra_current_meaning"
    }

    private lateinit var binding: ActivityWordDetailBinding
    private lateinit var adapter: ExactWordOccurrencesAdapter
    private lateinit var rootsDbHelper: RootsDatabaseHelper
    private lateinit var surahRepo: SurahRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityWordDetailBinding.inflate(layoutInflater)
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

        val arabicWord = intent.getStringExtra(EXTRA_ARABIC_WORD) ?: ""
        val currentMeaning = intent.getStringExtra(EXTRA_CURRENT_MEANING) ?: ""

        binding.textExactWordLarge.text = arabicWord
        binding.textExactWordLarge.typeface = com.miqu.android.recitation.util.FontHelper.getArabicTypeface(this)
        binding.toolbar.title = "Word: $arabicWord"

        rootsDbHelper = RootsDatabaseHelper.getInstance(this)
        surahRepo = SurahRepository(this)

        adapter = ExactWordOccurrencesAdapter(surahRepo) { occurrence ->
            val surah = surahRepo.getSurahById(occurrence.surah)
            val intent = Intent(this, ReaderActivity::class.java).apply {
                putExtra(ReaderActivity.EXTRA_SURAH_ID, occurrence.surah)
                putExtra(ReaderActivity.EXTRA_SURAH_NAME, surah?.name ?: "")
                putExtra(
                    ReaderActivity.EXTRA_SURAH_TRANSLITERATION,
                    surah?.transliteration ?: "Surah ${occurrence.surah}"
                )
                putExtra(ReaderActivity.EXTRA_TOTAL_VERSES, surah?.totalVerses ?: 0)
                putExtra(ReaderActivity.EXTRA_TARGET_VERSE, occurrence.verse)
            }
            startActivity(intent)
        }

        binding.recyclerViewWordOccurrences.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewWordOccurrences.adapter = adapter

        thread(start = true, name = "exact-word-occurrences-loader") {
            val occurrences = rootsDbHelper.getExactWordOccurrences(arabicWord)
            runOnUiThread {
                binding.textExactWordCount.text = "${occurrences.size} Exact Matches"
                adapter.submitList(occurrences)
            }
        }
    }
}

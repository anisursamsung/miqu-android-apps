package com.miqu.android.recitation.ui.lexicon

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
import com.miqu.android.recitation.databinding.ActivityRootDetailBinding
import com.miqu.android.recitation.ui.reader.ReaderActivity
import kotlin.concurrent.thread

class RootDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ROOT = "extra_root"
        const val EXTRA_DEFINITION = "extra_definition"
    }

    private lateinit var binding: ActivityRootDetailBinding
    private lateinit var adapter: RootOccurrencesAdapter
    private lateinit var rootsDbHelper: RootsDatabaseHelper
    private lateinit var surahRepo: SurahRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityRootDetailBinding.inflate(layoutInflater)
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

        val rootText = intent.getStringExtra(EXTRA_ROOT) ?: ""
        val definition = intent.getStringExtra(EXTRA_DEFINITION) ?: ""

        binding.textRootArabicLarge.text = rootText.toCharArray().joinToString(" ")
        binding.textRootArabicLarge.typeface = com.miqu.android.recitation.util.FontHelper.getArabicTypeface(this)
        binding.textRootFullDefinition.text = definition.ifEmpty { "Classical root form: $rootText" }
        binding.toolbar.title = "Root: \u200E$rootText"

        rootsDbHelper = RootsDatabaseHelper.getInstance(this)
        surahRepo = SurahRepository(this)

        adapter = RootOccurrencesAdapter { wordRoot ->
            val surah = surahRepo.getSurahById(wordRoot.surah)
            val intent = Intent(this, ReaderActivity::class.java).apply {
                putExtra(ReaderActivity.EXTRA_SURAH_ID, wordRoot.surah)
                putExtra(ReaderActivity.EXTRA_SURAH_NAME, surah?.name ?: "")
                putExtra(ReaderActivity.EXTRA_SURAH_TRANSLITERATION, surah?.transliteration ?: "Surah ${wordRoot.surah}")
                putExtra(ReaderActivity.EXTRA_TOTAL_VERSES, surah?.totalVerses ?: 0)
                putExtra(ReaderActivity.EXTRA_TARGET_VERSE, wordRoot.verse)
            }
            startActivity(intent)
        }

        binding.recyclerViewOccurrences.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewOccurrences.adapter = adapter

        thread(start = true, name = "root-occurrences-loader") {
            val occurrences = rootsDbHelper.getOccurrencesForRoot(rootText)
            runOnUiThread {
                binding.textRootTotalOccurrences.text = "${occurrences.size} Occurrences"
                adapter.submitList(occurrences)
            }
        }
    }
}

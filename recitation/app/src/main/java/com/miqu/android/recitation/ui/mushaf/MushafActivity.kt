package com.miqu.android.recitation.ui.mushaf

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.miqu.android.recitation.R
import com.miqu.android.recitation.data.MushafRepository
import com.miqu.android.recitation.databinding.ActivityMushafBinding
import com.miqu.android.recitation.model.MushafPage
import com.miqu.android.recitation.model.MushafType
import kotlin.concurrent.thread

class MushafActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MUSHAF_TYPE = "extra_mushaf_type"
        const val EXTRA_START_PAGE = "extra_start_page"
    }

    private lateinit var binding: ActivityMushafBinding
    private lateinit var adapter: MushafPageAdapter
    private lateinit var repository: MushafRepository
    private var mushafType = MushafType.KING_FAHAD
    private var pages: List<MushafPage> = emptyList()
    private var areControlsVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMushafBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.btnFloatingBack) { v, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updateLayoutParams<androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams> {
                topMargin = statusBars.top + 16
            }
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.cardBottomControls) { v, insets ->
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updateLayoutParams<androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams> {
                bottomMargin = navBars.bottom + 24
            }
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.viewPagerMushaf) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top + 4, bottom = bars.bottom + 4)
            insets
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        binding.btnFloatingBack.setOnClickListener {
            finish()
        }

        val typeStr = intent.getStringExtra(EXTRA_MUSHAF_TYPE) ?: MushafType.KING_FAHAD.id
        mushafType = if (typeStr == MushafType.INDO_PAK.id) MushafType.INDO_PAK else MushafType.KING_FAHAD
        val startPage = intent.getIntExtra(EXTRA_START_PAGE, 1)

        repository = MushafRepository.getInstance(this)

        adapter = MushafPageAdapter(this, mushafType) {
            toggleControls()
        }

        // Physical Mushaf is read Right-to-Left
        binding.viewPagerMushaf.layoutDirection = View.LAYOUT_DIRECTION_RTL
        binding.viewPagerMushaf.adapter = adapter
        binding.viewPagerMushaf.offscreenPageLimit = 1

        binding.viewPagerMushaf.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position in pages.indices) {
                    val page = pages[position]
                    binding.textBottomPageIndicator.text = "Page ${page.pageNumber} / ${pages.size}"
                }
            }
        })

        binding.btnPrevPage.setOnClickListener {
            val curr = binding.viewPagerMushaf.currentItem
            if (curr > 0) {
                binding.viewPagerMushaf.currentItem = curr - 1
            }
        }

        binding.btnNextPage.setOnClickListener {
            val curr = binding.viewPagerMushaf.currentItem
            if (curr < pages.size - 1) {
                binding.viewPagerMushaf.currentItem = curr + 1
            }
        }

        binding.btnJumpToPage.setOnClickListener {
            showJumpToPageDialog()
        }

        thread(start = true, name = "mushaf-pages-loader") {
            pages = repository.getPages(mushafType)
            runOnUiThread {
                adapter.submitList(pages)
                val targetIndex = (startPage - 1).coerceIn(0, (pages.size - 1).coerceAtLeast(0))
                binding.viewPagerMushaf.setCurrentItem(targetIndex, false)
                if (pages.isNotEmpty()) {
                    val p = pages[targetIndex]
                    binding.textBottomPageIndicator.text = "Page ${p.pageNumber} / ${pages.size}"
                }
            }
        }
    }

    private fun toggleControls() {
        areControlsVisible = !areControlsVisible
        val targetAlpha = if (areControlsVisible) 1f else 0f
        val visibility = if (areControlsVisible) View.VISIBLE else View.GONE

        binding.btnFloatingBack.animate().alpha(targetAlpha).setDuration(200).withEndAction {
            binding.btnFloatingBack.visibility = visibility
        }.start()

        binding.cardBottomControls.animate().alpha(targetAlpha).setDuration(200).withEndAction {
            binding.cardBottomControls.visibility = visibility
        }.start()
    }

    private fun showJumpToPageDialog() {
        if (pages.isEmpty()) return
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = "Enter page (1 - ${pages.size})"
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.jump_to_page)
            .setView(input)
            .setPositiveButton("Go") { _, _ ->
                val entered = input.text.toString().toIntOrNull()
                if (entered != null && entered in 1..pages.size) {
                    binding.viewPagerMushaf.currentItem = entered - 1
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

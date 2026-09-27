package com.miqu.android.recitation.ui.mushaf

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.miqu.android.recitation.R
import com.miqu.android.recitation.databinding.ItemMushafPageBinding
import com.miqu.android.recitation.model.MushafLine
import com.miqu.android.recitation.model.MushafPage
import com.miqu.android.recitation.model.MushafType

class MushafPageAdapter(
    private val context: Context,
    private val mushafType: MushafType,
    private val onPageClick: () -> Unit
) : RecyclerView.Adapter<MushafPageAdapter.PageViewHolder>() {

    private var pages: List<MushafPage> = emptyList()

    private val mushafTypeface: Typeface = when (mushafType) {
        MushafType.KING_FAHAD -> try {
            ResourcesCompat.getFont(context, R.font.uthman) ?: Typeface.DEFAULT
        } catch (_: Exception) {
            Typeface.DEFAULT
        }
        MushafType.INDO_PAK -> try {
            ResourcesCompat.getFont(context, R.font.aqqm) ?: Typeface.DEFAULT
        } catch (_: Exception) {
            Typeface.DEFAULT
        }
    }

    private val bismillahTypeface: Typeface = try {
        ResourcesCompat.getFont(context, R.font.bismillah) ?: mushafTypeface
    } catch (_: Exception) {
        mushafTypeface
    }

    private val surahNameMap: Map<Int, String> by lazy {
        try {
            com.miqu.android.recitation.data.SurahRepository(context).getSurahs().associate { it.id to it.name }
        } catch (_: Exception) {
            emptyMap()
        }
    }

    fun submitList(newPages: List<MushafPage>) {
        pages = newPages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val binding = ItemMushafPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int = pages.size

    inner class PageViewHolder(private val binding: ItemMushafPageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val lineSlots = ArrayList<FrameLayout>(15)
        private val lineDividers = ArrayList<View>(14)

        init {
            binding.root.setOnClickListener {
                onPageClick()
            }
            binding.containerLines.setOnClickListener {
                onPageClick()
            }

            // Pre-create exactly 15 line slot containers with dividers between them
            binding.containerLines.removeAllViews()
            val dividerHeight = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                1f,
                context.resources.displayMetrics
            ).toInt().coerceAtLeast(1)

            for (i in 0 until 15) {
                val slot = FrameLayout(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                    )
                }
                lineSlots.add(slot)
                binding.containerLines.addView(slot)

                if (i < 14) {
                    val divider = View(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            dividerHeight
                        )
                        isClickable = false
                        isFocusable = false
                    }
                    lineDividers.add(divider)
                    binding.containerLines.addView(divider)
                }
            }
        }

        fun bind(page: MushafPage) {
            binding.textPageSurah.text = page.surahName
            binding.textPageJuz.text = "Juz ${page.juzNumber}"
            binding.textPageNumber.text = "Page ${page.pageNumber}"

            val textColor = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnSurface)
            val primaryColor = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorPrimary)
            val containerHigh = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorSurfaceContainerHigh)
            val outlineVariant = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOutlineVariant)

            val lines = page.lines
            for (i in 0 until 14) {
                val divider = lineDividers[i]
                val currentLine = lines.getOrNull(i)
                val nextLine = lines.getOrNull(i + 1)
                val shouldShow = currentLine != null && nextLine != null &&
                        currentLine !is MushafLine.EmptySpacer &&
                        nextLine !is MushafLine.EmptySpacer &&
                        currentLine !is MushafLine.SurahHeader &&
                        nextLine !is MushafLine.SurahHeader

                divider.visibility = if (shouldShow) View.VISIBLE else View.INVISIBLE
                divider.setBackgroundColor(outlineVariant)
            }
            for (i in 0 until 15) {
                val slot = lineSlots[i]
                slot.removeAllViews()

                if (i >= lines.size) continue
                val line = lines[i]

                when (line) {
                    is MushafLine.EmptySpacer -> {
                        // Slot remains empty spacer to preserve 15-line vertical grid
                    }
                    is MushafLine.Bismillah -> {
                        val bismillahView = TextView(context).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            )
                            gravity = Gravity.CENTER
                            maxLines = 1
                            isSingleLine = true
                            includeFontPadding = false
                            setPadding(0, 0, 0, 0)
                            text = if (mushafType == MushafType.INDO_PAK) {
                                "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِیْمِ"
                            } else {
                                "بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ"
                            }
                            typeface = bismillahTypeface
                            setTextColor(primaryColor)
                            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                                this, 12, 24, 1, TypedValue.COMPLEX_UNIT_SP
                            )
                        }
                        slot.addView(bismillahView)
                    }
                    is MushafLine.SurahHeader -> {
                        val bannerCard = MaterialCardView(context).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            ).apply {
                                setMargins(4, 2, 4, 2)
                            }
                            radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, context.resources.displayMetrics)
                            cardElevation = 0f
                            setCardBackgroundColor(containerHigh)
                            strokeColor = outlineVariant
                            strokeWidth = 1
                        }

                        val surahTitle = TextView(context).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            )
                            gravity = Gravity.CENTER
                            maxLines = 1
                            isSingleLine = true
                            includeFontPadding = false
                            setPadding(0, 0, 0, 0)
                            val arabicName = surahNameMap[line.surahNumber] ?: line.surahName
                            text = "۞  سُورَةُ $arabicName  ۞"
                            typeface = mushafTypeface
                            setTextColor(primaryColor)
                            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                                this, 10, 19, 1, TypedValue.COMPLEX_UNIT_SP
                            )
                        }
                        bannerCard.addView(surahTitle)
                        slot.addView(bannerCard)
                    }
                    is MushafLine.TextLine -> {
                        val textView = MushafLineTextView(context).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT
                            )
                            gravity = Gravity.CENTER
                            textDirection = View.TEXT_DIRECTION_RTL
                            text = line.text
                            typeface = mushafTypeface
                            setTextColor(textColor)
                            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                                this, 8, 22, 1, TypedValue.COMPLEX_UNIT_SP
                            )
                        }
                        slot.addView(textView)
                    }
                }
            }
        }
    }

    private class MushafLineTextView(context: Context) : androidx.appcompat.widget.AppCompatTextView(context) {
        init {
            maxLines = 1
            setHorizontallyScrolling(false)
            includeFontPadding = false
            setPadding(0, 0, 0, 0)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            textScaleX = 1.0f
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            val availableWidth = measuredWidth - paddingLeft - paddingRight
            if (availableWidth > 0 && !text.isNullOrEmpty()) {
                val textWidth = paint.measureText(text.toString())
                if (textWidth > availableWidth) {
                    textScaleX = (availableWidth / textWidth).coerceIn(0.6f, 1.0f)
                }
            }
        }
    }
}

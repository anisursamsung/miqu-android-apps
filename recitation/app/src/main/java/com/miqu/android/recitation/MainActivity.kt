package com.miqu.android.recitation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.miqu.android.recitation.databinding.ActivityMainBinding
import com.miqu.android.recitation.ui.lexicon.LexiconFragment
import com.miqu.android.recitation.ui.settings.SettingsFragment
import com.miqu.android.recitation.ui.surah.SurahListFragment
import com.miqu.android.recitation.ui.videos.VideosFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val surahListFragment = SurahListFragment()
    private val mushafsFragment = com.miqu.android.recitation.ui.mushaf.MushafsFragment()
    private val lexiconFragment = LexiconFragment()
    private val videosFragment = VideosFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.appBarLayout) { v, insets ->
            val statusBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.updatePadding(top = statusBars.top)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { v, insets ->
            val navBars = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.updatePadding(bottom = navBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            switchFragment(surahListFragment, getString(R.string.app_name))
        }

        binding.toolbar.inflateMenu(R.menu.menu_main)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    val intent = android.content.Intent(this, com.miqu.android.recitation.ui.settings.SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_surahs -> {
                    switchFragment(surahListFragment, getString(R.string.app_name))
                    true
                }
                R.id.nav_mushaf -> {
                    switchFragment(mushafsFragment, getString(R.string.nav_mushaf))
                    true
                }
                R.id.nav_lexicon -> {
                    switchFragment(lexiconFragment, getString(R.string.nav_lexicon))
                    true
                }
                R.id.nav_media -> {
                    switchFragment(videosFragment, getString(R.string.nav_media))
                    true
                }
                else -> false
            }
        }
    }

    private fun switchFragment(fragment: Fragment, title: String) {
        binding.toolbar.title = title
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, fragment)
            .commit()
    }
}

package com.miqu.android.recitation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.miqu.android.recitation.databinding.ActivityMainBinding
import com.miqu.android.recitation.ui.learn.LearnFragment
import com.miqu.android.recitation.ui.more.MoreFragment
import com.miqu.android.recitation.ui.mushaf.MushafsFragment
import com.miqu.android.recitation.ui.surah.SurahListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val surahListFragment = SurahListFragment()
    private val mushafsFragment = MushafsFragment()
    private val learnFragment = LearnFragment()
    private val moreFragment = MoreFragment()

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
                R.id.nav_learn -> {
                    switchFragment(learnFragment, getString(R.string.nav_learn))
                    true
                }
                R.id.nav_more -> {
                    switchFragment(moreFragment, getString(R.string.nav_more))
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

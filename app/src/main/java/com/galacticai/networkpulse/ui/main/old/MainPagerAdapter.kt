package com.galacticai.networkpulse.ui.main.old

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.galacticai.networkpulse.MainActivityOld


class MainPagerAdapter(mainActivity: MainActivityOld) : FragmentStateAdapter(mainActivity) {
    override fun getItemCount(): Int {
        return MainFragmentName.entries.size
    }

    override fun createFragment(position: Int): Fragment {
        return MainFragmentName.fragmentFromName(
            MainFragmentName.fromInt(position)
        )
    }

}

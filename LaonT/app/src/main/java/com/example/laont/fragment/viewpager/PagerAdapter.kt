package com.example.laont.fragment.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.laont.fragment.board.BoardFragment
import com.example.laont.fragment.map.MapFragment

class PagerAdapter(fm: FragmentManager, lc: Lifecycle) : FragmentStateAdapter(fm, lc) {
    var fragmentList = arrayOfNulls<Fragment>(2)

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            fragmentList[0] = BoardFragment()
            return fragmentList[0]!!
        } else if (position == 1) {
            fragmentList[1] = MapFragment()
            return fragmentList[1]!!
        } else {
            error("No such position: $position")
        }
    }
}
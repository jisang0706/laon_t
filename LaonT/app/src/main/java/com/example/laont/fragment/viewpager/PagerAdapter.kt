package com.example.laont.fragment.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.laont.fragment.board.BoardFragment
import com.example.laont.fragment.map.MapFragment

class PagerAdapter(fm: FragmentManager, lc: Lifecycle) : FragmentStateAdapter(fm, lc) {
    var boardFragment: BoardFragment = BoardFragment()
    var mapFragment: MapFragment = MapFragment()

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            boardFragment = BoardFragment()
            return boardFragment
        } else if (position == 1) {
            mapFragment = MapFragment()
            return mapFragment
        } else {
            error("No such position: $position")
        }
    }
}
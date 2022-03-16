package com.example.laont.fragment.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.laont.fragment.board.BoardFragment
import com.example.laont.fragment.map.MapFragment
import com.example.laont.fragment.userpage.UserFragment

class PagerAdapter(fm: FragmentManager, lc: Lifecycle) : FragmentStateAdapter(fm, lc) {
    var boardFragment: BoardFragment = BoardFragment()
    var mapFragment: MapFragment = MapFragment()
    var userFragment: UserFragment = UserFragment()

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        when(position) {
            0 -> return boardFragment
            1 -> return mapFragment
            2 -> return userFragment
            else -> error("No such position: $position")
        }
    }
}
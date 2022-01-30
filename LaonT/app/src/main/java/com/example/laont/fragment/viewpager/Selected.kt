package com.example.laont.fragment.viewpager

import androidx.viewpager2.widget.ViewPager2
import com.example.laont.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class Selected (view_pager: ViewPager2, bottom_navigation: BottomNavigationView) {
    private var view_pager: ViewPager2
    private var bottom_navigation: BottomNavigationView

    init {
        this.view_pager = view_pager
        this.bottom_navigation = bottom_navigation
    }

    inner class PageChangeCallback(function: () -> Unit) : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            bottom_navigation.selectedItemId = when (position) {
                0 -> R.id.item_board
                1 -> R.id.item_map
                else -> error("No such position $position")
            }
        }
    }
}
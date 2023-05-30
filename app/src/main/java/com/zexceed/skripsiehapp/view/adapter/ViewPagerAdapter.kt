package com.zexceed.skripsiehapp.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.zexceed.skripsiehapp.view.fragment.InventoryFragment
import com.zexceed.skripsiehapp.view.fragment.PeminjamanFragment


class ViewPagerAdapter(
    fragmentActivity: FragmentActivity,private var totalCount: Int
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PeminjamanFragment()
            1 -> InventoryFragment()
            else -> PeminjamanFragment()
        }
    }

    override fun getItemCount(): Int {
        return totalCount
    }

}

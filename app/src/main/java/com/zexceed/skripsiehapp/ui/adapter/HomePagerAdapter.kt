package com.zexceed.skripsiehapp.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zexceed.skripsiehapp.ui.view.fragment.inventory.InventoryFragment
import com.zexceed.skripsiehapp.ui.view.fragment.peminjaman.PeminjamanFragment

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PeminjamanFragment()
            1 -> InventoryFragment()
//            HomeTabs.PEMINJAMAN.index -> PeminjamanFragment.newInstance(HomeTabs.PEMINJAMAN.name)
//            HomeTabs.INVENTORY.index -> InventoryFragment.newInstance(HomeTabs.INVENTORY.name)
            else -> PeminjamanFragment()
//                throw IllegalStateException("Fragment not found")
        }
    }
}
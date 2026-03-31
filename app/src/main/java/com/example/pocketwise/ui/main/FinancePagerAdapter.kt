package com.example.pocketwise.ui.main

import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pocketwise.ui.dashboard.DashboardFragment
import com.example.pocketwise.ui.transactions.TransactionsFragment

class FinancePagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): androidx.fragment.app.Fragment {
        return when(position){
            0 -> DashboardFragment()
            1 -> TransactionsFragment()
//            2 -> CategoriesFragment()
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }

}
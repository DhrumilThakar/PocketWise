package com.example.pocketwise.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pocketwise.R
import com.example.pocketwise.databinding.ActivityMainBinding
import com.example.pocketwise.ui.dialogs.AddTransactionDialog
import com.example.pocketwise.ui.viewModel.FinanceViewModel
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FinanceViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(FinanceViewModel::class.java)

        setupViewPager()
        setupFab()

    }

    private fun setupFab() {
        binding.fabaddTransactions.setOnClickListener {
            showAddTransactionDialog()
        }
    }

    fun showAddTransactionDialog() {
        AddTransactionDialog().show(supportFragmentManager, "AddTransaction")
    }

    private fun setupViewPager() {
        val adapter = FinancePagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager){
            tab, position ->
            tab.text = when(position){
                0 -> getString(R.string.tab_dashboard)
                1 -> getString(R.string.tab_transactions)
                2 -> "Categories" // Using hardcoded string if R.string.tab_categories is missing
                else -> throw IllegalArgumentException("Invalid position $position")
            }
        }.attach()

    }
}
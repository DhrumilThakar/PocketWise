package com.example.pocketmaster.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.pocketmaster.R
import com.example.pocketmaster.databinding.ActivityMainBinding
import com.example.pocketmaster.ui.dialogs.AddTransactionDialog
import com.example.pocketmaster.ui.viewmodel.FinanceViewModel
import com.google.android.material.tabs.TabLayoutMediator
import android.widget.Toast
import com.example.pocketmaster.ui.dialogs.AddPersonDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: FinanceViewModel

    private val SMS_PERMISSION_CODE = 101

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            binding.fabAddTransaction.apply {
                visibility = android.view.View.VISIBLE
                when (position) {
                    0 -> {
                        text = getString(R.string.add_transaction)
                        setIconResource(R.drawable.ic_add)
                    }
                    1 -> {
                        visibility = android.view.View.GONE
                    }
                    2 -> {
                        text = getString(R.string.add_person)
                        setIconResource(R.drawable.ic_person)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[FinanceViewModel::class.java]

        setupViewPager()
        setupFab()
        checkSmsPermission()
    }

    private fun setupViewPager() {
        val adapter = FinancePagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(pageChangeCallback)
        binding.tabLayout.apply {
            setSelectedTabIndicatorColor(getColor(R.color.white))
            setTabTextColors(
                getColor(R.color.divider1),
                getColor(R.color.white)
            )
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_dashboard)
                1 -> getString(R.string.tab_transactions)
                2 -> getString(R.string.tab_debts)
                else -> getString(R.string.tab_categories)
            }
        }.attach()
    }

    private fun setupFab() {
        binding.fabAddTransaction.setOnClickListener {
            when (binding.viewPager.currentItem) {
                0 -> showAddTransactionDialog()
                2 -> showAddPersonDialog()
            }
        }
    }

    private fun showAddTransactionDialog() {
        AddTransactionDialog().show(supportFragmentManager, "AddTransaction")
    }

    private fun showAddPersonDialog() {
        AddPersonDialog().show(supportFragmentManager, "AddPerson")
    }

    private fun checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS), SMS_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS Detection Enabled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
    }
}
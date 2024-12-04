package com.example.qlctcanhan

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.qlctcanhan.databinding.ActivityMainBinding
import com.example.qlctcanhan.ui.fragments.BudgetFragment
import com.example.qlctcanhan.ui.fragments.CategoryManagementFragment
import com.example.qlctcanhan.ui.fragments.SettingsFragment
import com.example.qlctcanhan.ui.fragments.StatisticsFragment
import com.example.qlctcanhan.ui.fragments.TransactionsFragment
import com.example.qlctcanhan.viewmodel.TransactionViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val transactionViewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_transactions -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            TransactionsFragment()
                        )
                        .commit()
                    true
                }

                R.id.nav_statistics -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.fragment_container,
                            StatisticsFragment()
                        )
                        .commit()
                    true
                }

                R.id.nav_budget -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CategoryManagementFragment())
                        .commit()
                    true
                }

                R.id.settingsFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SettingsFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }
    }
}

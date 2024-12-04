package com.example.qlctcanhan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.qlctcanhan.model.Budget
import com.example.qlctcanhan.repository.BudgetRepository
import com.example.qlctcanhan.repository.TransactionRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class BudgetViewModel(
    private val repository: BudgetRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    fun setBudgetForCategory(categoryId: Long, amount: Double) {
        val currentMonth = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            repository.insertOrUpdateBudget(
                Budget(
                    categoryId = categoryId,
                    monthYear = currentMonth,
                    budgetAmount = amount
                )
            )
        }
    }

    fun checkBudgetStatus(categoryId: Long): LiveData<BudgetStatus> {
        val currentMonth = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date())
        return liveData {
            val budget = repository.getBudgetForCategory(categoryId, currentMonth)
            val totalSpent = repository.getTotalSpentForCategory(categoryId, currentMonth)

            val status = when {
                budget == null -> BudgetStatus.NO_BUDGET
                totalSpent == null -> BudgetStatus.SAFE // Nếu totalSpent là null, coi như là an toàn
                totalSpent > budget.budgetAmount * 1.2 -> BudgetStatus.EXCEEDED
                totalSpent > budget.budgetAmount * 0.8 -> BudgetStatus.WARNING
                else -> BudgetStatus.SAFE
            }

            emit(status)
        }
    }
}

enum class BudgetStatus {
    NO_BUDGET, SAFE, WARNING, EXCEEDED
}
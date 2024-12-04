package com.example.qlctcanhan.repository

import androidx.lifecycle.LiveData
import com.example.qlctcanhan.database.BudgetDao
import com.example.qlctcanhan.model.Budget
import com.example.qlctcanhan.viewmodel.BudgetStatus
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {

    // Lấy danh sách ngân sách cho một tháng cụ thể
    fun getBudgetsForMonth(monthYear: String): Flow<List<Budget>> {
        return budgetDao.getBudgetsForMonth(monthYear)
    }

    // Thêm mới hoặc cập nhật ngân sách
    suspend fun insertOrUpdateBudget(budget: Budget) {
        budgetDao.insertOrUpdateBudget(budget)
    }

    // Cập nhật số tiền đã chi tiêu trong ngân sách
    suspend fun updateSpentAmount(budgetId: Long, amount: Double) {
        budgetDao.updateSpentAmount(budgetId, amount)
    }

    // Lấy ngân sách cho một danh mục và tháng cụ thể
    suspend fun getBudgetForCategory(categoryId: Long, monthYear: String): Budget? {
        return budgetDao.getBudgetForCategory(categoryId, monthYear)
    }

    // Lấy tổng số tiền đã chi tiêu cho một danh mục và tháng cụ thể
    suspend fun getTotalSpentForCategory(categoryId: Long, monthYear: String): Double? {
        return budgetDao.getTotalSpentForCategory(categoryId, monthYear)
    }

    // Lấy trạng thái ngân sách cho một danh mục và tháng cụ thể
    fun getBudgetStatus(categoryId: Long, monthYear: String): LiveData<BudgetStatus> {
        return budgetDao.getBudgetStatus(categoryId, monthYear)
    }
}

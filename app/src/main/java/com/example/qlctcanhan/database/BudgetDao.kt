package com.example.qlctcanhan.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.qlctcanhan.model.Budget
import com.example.qlctcanhan.viewmodel.BudgetStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE monthYear = :monthYear")
    fun getBudgetsForMonth(monthYear: String): Flow<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: Budget)

    @Query("UPDATE budgets SET spentAmount = spentAmount + :amount WHERE id = :budgetId")
    suspend fun updateSpentAmount(budgetId: Long, amount: Double)

    @Query("""
        SELECT budgetAmount - spentAmount AS remainingBudget,
               spentAmount AS spent,
               budgetAmount AS total
        FROM budgets 
        WHERE categoryId = :categoryId AND monthYear = :monthYear
    """)
    fun getBudgetStatus(categoryId: Long, monthYear: String): LiveData<BudgetStatus>

    @Query("""
        SELECT * 
        FROM budgets 
        WHERE categoryId = :categoryId AND monthYear = :monthYear
        LIMIT 1
    """)
    suspend fun getBudgetForCategory(categoryId: Long, monthYear: String): Budget?

    @Query("""
        SELECT SUM(amount) 
        FROM transaction_table 
        WHERE categoryId = :categoryId AND strftime('%m/%Y', date / 1000, 'unixepoch') = :monthYear
    """)
    suspend fun getTotalSpentForCategory(categoryId: Long, monthYear: String): Double?
    
}

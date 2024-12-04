package com.example.qlctcanhan.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Long,
    val monthYear: String, // Format "MM/yyyy"
    val budgetAmount: Double,
    val spentAmount: Double = 0.0
)
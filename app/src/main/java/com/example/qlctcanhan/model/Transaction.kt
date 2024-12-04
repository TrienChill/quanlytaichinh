package com.example.qlctcanhan.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_table") // Tên bảng là "transaction_table"
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val categoryId: Long,
    val date: Long,
    val description: String,
    val type: TransactionType
)

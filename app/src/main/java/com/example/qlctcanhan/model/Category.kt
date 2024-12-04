package com.example.qlctcanhan.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: Int,
    val type: TransactionType,
    val isDefault: Boolean = false,
    val color: Int? = null // Màu sắc tùy chỉnh cho danh mục
)
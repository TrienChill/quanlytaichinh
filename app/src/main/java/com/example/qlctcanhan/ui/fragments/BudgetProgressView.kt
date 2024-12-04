package com.example.qlctcanhan.ui.fragments

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.example.qlctcanhan.R

class BudgetProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val progressBar: ProgressBar
    private val tvBudgetInfo: TextView

    init {
        val view = inflate(context, R.layout.view_budget_progress, this)
        progressBar = view.findViewById(R.id.progressBar)
        tvBudgetInfo = view.findViewById(R.id.tvBudgetInfo)
    }

    fun setBudgetProgress(spent: Double, total: Double) {
        val progress = ((spent / total) * 100).toInt()
        progressBar.progress = progress
        tvBudgetInfo.text = "Đã chi: ${formatCurrency(spent)}/${formatCurrency(total)}"
    }

    private fun formatCurrency(total: Double): String {
        return String.format("%.2f", total)
    }
}
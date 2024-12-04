package com.example.qlctcanhan.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.qlctcanhan.database.AppDatabase
import com.example.qlctcanhan.databinding.FragmentStatisticsBinding
import com.example.qlctcanhan.model.Transaction
import com.example.qlctcanhan.model.TransactionType
import com.example.qlctcanhan.repository.TransactionRepository
import com.example.qlctcanhan.viewmodel.TransactionViewModel
import com.example.qlctcanhan.viewmodel.TransactionViewModelFactory
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Locale

class StatisticsFragment : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        // Khởi tạo ViewModel với ViewModelFactory
        val transactionDao = AppDatabase.getDatabase(requireContext()).transactionDao()
        val repository = TransactionRepository(transactionDao)
        val factory = TransactionViewModelFactory(repository)
        transactionViewModel = ViewModelProvider(this, factory).get(TransactionViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionViewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            setupPieChart(transactions)
            setupBarChart(transactions)
        }
    }

    private fun setupPieChart(transactions: List<Transaction>) {
        val pieChart = binding.pieChartExpenses
        val entries = mutableListOf<PieEntry>()

        // Tổng thu nhập và chi tiêu
        val totalIncome = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
        val totalExpense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        // Thêm dữ liệu thu nhập và chi tiêu vào biểu đồ
        entries.add(PieEntry(totalIncome.toFloat(), "Thu Nhập"))
        entries.add(PieEntry(totalExpense.toFloat(), "Chi Tiêu"))

        val dataSet = PieDataSet(entries, "Tổng Thu Nhập & Chi Tiêu")
        dataSet.colors = listOf(Color.GREEN, Color.RED)
        val pieData = PieData(dataSet)

        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "Thu Nhập vs Chi Tiêu"
        pieChart.animateY(1000)
        pieChart.invalidate()
    }

    private fun setupBarChart(transactions: List<Transaction>) {
        val barChart = binding.barChartMonthlyTrend
        val entriesIncome = mutableListOf<BarEntry>()
        val entriesExpense = mutableListOf<BarEntry>()

        // Nhóm giao dịch theo tháng
        val monthlyTransactions = transactions.groupBy {
            SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(java.util.Date(it.date))
        }

        // Tạo danh sách các tháng đã sắp xếp
        val sortedMonths = monthlyTransactions.keys.sorted()

        // Xử lý dữ liệu cho từng tháng
        sortedMonths.forEachIndexed { index, month ->
            val transactionsInMonth = monthlyTransactions[month] ?: emptyList()

            // Tổng thu nhập và chi tiêu trong tháng
            val totalIncome = transactionsInMonth
                .filter { it.type == TransactionType.INCOME }
                .sumOf { it.amount }
            val totalExpense = transactionsInMonth
                .filter { it.type == TransactionType.EXPENSE }
                .sumOf { it.amount }

            // Thêm dữ liệu vào danh sách
            entriesIncome.add(BarEntry(index.toFloat(), totalIncome.toFloat()))
            entriesExpense.add(BarEntry(index.toFloat(), totalExpense.toFloat()))
        }

        // Tạo DataSet cho Thu Nhập và Chi Tiêu
        val incomeDataSet = BarDataSet(entriesIncome, "Thu Nhập").apply { color = Color.GREEN }
        val expenseDataSet = BarDataSet(entriesExpense, "Chi Tiêu").apply { color = Color.RED }

        // Cấu hình dữ liệu và nhóm các thanh
        val barData = BarData(incomeDataSet, expenseDataSet).apply {
            barWidth = 0.3f // Độ rộng của mỗi cột
        }
        barChart.data = barData

        // Cấu hình trục X và nhóm các cột
        barChart.xAxis.apply {
            granularity = 1f
            isGranularityEnabled = true
            axisMinimum = 0f
            axisMaximum = sortedMonths.size.toFloat()
        }
        barChart.groupBars(0f, 0.4f, 0.1f) // Nhóm các cột (khoảng cách giữa các nhóm)

        // Cấu hình biểu đồ và hiển thị
        barChart.description.isEnabled = false
        barChart.animateY(1000)
        barChart.invalidate()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    // Trong Fragment
    private fun setupAnimations() {
        val fadeIn = Fade(Fade.IN)
        fadeIn.duration = 300

        val slideIn = Slide(Gravity.BOTTOM)
        slideIn.duration = 300

        enterTransition = TransitionSet().apply {
            val addTransition = addTransition(fadeIn)
            addTransition(slideIn)
        }
    }
}

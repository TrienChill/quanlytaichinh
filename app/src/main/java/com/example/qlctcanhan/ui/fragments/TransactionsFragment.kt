package com.example.qlctcanhan.ui.fragments

import android.app.AlertDialog
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qlctcanhan.R
import com.example.qlctcanhan.database.AppDatabase
import com.example.qlctcanhan.databinding.DialogAddTransactionBinding
import com.example.qlctcanhan.databinding.FragmentTransactionsBinding
import com.example.qlctcanhan.model.Transaction
import com.example.qlctcanhan.model.TransactionType
import com.example.qlctcanhan.ui.adapters.TransactionAdapter
import com.example.qlctcanhan.viewmodel.TransactionViewModel
import com.example.qlctcanhan.viewmodel.TransactionViewModelFactory
import com.example.qlctcanhan.repository.TransactionRepository

class TransactionsFragment : Fragment(R.layout.fragment_transactions) {
    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the database and repository
        val transactionDao = AppDatabase.getDatabase(requireContext()).transactionDao()
        val repository = TransactionRepository(transactionDao)

        // Initialize ViewModel with factory
        val factory = TransactionViewModelFactory(repository)
        transactionViewModel = ViewModelProvider(this, factory).get(TransactionViewModel::class.java)

        // Setup RecyclerView
        setupRecyclerView()

        // Button to add transaction
        binding.fabAddTransaction.setOnClickListener {
            showAddTransactionDialog()
        }

        // Observe transactions
        transactionViewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
            updateSummary(transactions)
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        binding.recyclerViewTransactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionAdapter

            // Swipe to delete functionality
            ItemTouchHelper(object : SwipeToDeleteCallback(requireContext()) {
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val transaction = transactionAdapter.currentList[position]
                    transactionViewModel.delete(transaction)
                }
            }).attachToRecyclerView(this)
        }
    }

    private fun showAddTransactionDialog() {
        val dialogBinding = DialogAddTransactionBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Thêm Giao Dịch Mới")
            .setView(dialogBinding.root)
            .setPositiveButton("Thêm") { _, _ ->
                val amount = dialogBinding.etAmount.text.toString().toDoubleOrNull()
                val description = dialogBinding.etDescription.text.toString()
                val type = if (dialogBinding.rbExpense.isChecked) TransactionType.EXPENSE else TransactionType.INCOME

                if (amount != null) {
                    val transaction = Transaction(
                        amount = amount,
                        description = description,
                        type = type,
                        date = System.currentTimeMillis(),
                        categoryId = 1 // Temporarily hardcoded
                    )
                    transactionViewModel.insert(transaction)
                }
            }
            .setNegativeButton("Hủy", null)
            .create()

        dialog.show()
    }

    private fun updateSummary(transactions: List<Transaction>) {
        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        binding.tvTotalIncome.text = formatCurrency(totalIncome)
        binding.tvTotalExpense.text = formatCurrency(totalExpense)
        binding.tvBalance.text = formatCurrency(totalIncome - totalExpense)
    }

    private fun formatCurrency(amount: Double): String {
        return "$%.2f".format(amount) // Adjust currency symbol if needed
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
            addTransition(fadeIn)
            addTransition(slideIn)
        }
    }
}

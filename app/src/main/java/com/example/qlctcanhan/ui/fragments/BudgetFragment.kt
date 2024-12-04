package com.example.qlctcanhan.ui.fragments

import android.os.Bundle
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qlctcanhan.R
import com.example.qlctcanhan.databinding.FragmentBudgetBinding
import com.example.qlctcanhan.databinding.DialogAddCategoryBinding
import com.example.qlctcanhan.model.Category
import com.example.qlctcanhan.model.TransactionType
import com.example.qlctcanhan.ui.adapters.CategoryAdapter
import com.example.qlctcanhan.database.AppDatabase
import com.example.qlctcanhan.repository.CategoryRepository
import com.example.qlctcanhan.viewmodel.CategoryViewModel
import com.example.qlctcanhan.viewmodel.CategoryViewModelFactory
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.launch
import android.app.AlertDialog
import com.example.qlctcanhan.databinding.DialogEditCategoryBinding

class BudgetFragment : Fragment() {
    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoryAdapter: CategoryAdapter

    // Khởi tạo CategoryViewModel với CategoryViewModelFactory
    private val categoryViewModel: CategoryViewModel by viewModels {
        val categoryDao = AppDatabase.getDatabase(requireContext()).categoryDao()
        val categoryRepository = CategoryRepository(categoryDao)
        CategoryViewModelFactory(categoryRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)

        setupRecyclerView()
        binding.fabAddCategory.setOnClickListener { showAddCategoryDialog() }

        return binding.root
    }

    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onEditClick = { category -> showEditCategoryDialog(category) },
            onDeleteClick = { category -> deleteCategory(category) }
        )

        binding.recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categoryAdapter
        }

        lifecycleScope.launch {
            categoryViewModel.getCategoriesByType(TransactionType.EXPENSE).collect { categories ->
                categoryAdapter.submitList(categories)
            }
        }
    }

    private fun showAddCategoryDialog() {
        val dialogBinding = DialogAddCategoryBinding.inflate(layoutInflater)

        val colorPickerDialog = ColorPickerDialog.Builder(requireContext())
            .setTitle("Chọn Màu Danh Mục")
            .setPreferenceName("MyColorPickerDialog")
            .setPositiveButton("Chọn", ColorEnvelopeListener { envelope, _ ->
                val selectedColor = envelope.color

                val categoryName = dialogBinding.etCategoryName.text.toString()
                val type = if (dialogBinding.rbExpense.isChecked)
                    TransactionType.EXPENSE
                else
                    TransactionType.INCOME

                val category = Category(
                    name = categoryName,
                    icon = R.drawable.ic_default_category,
                    type = type,
                    isDefault = false,
                    color = selectedColor
                )
                categoryViewModel.addCategory(category)
            })
            .setNegativeButton("Hủy") { dialogInterface, _ -> dialogInterface.dismiss() }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)

        AlertDialog.Builder(requireContext())
            .setTitle("Thêm Danh Mục Mới")
            .setView(dialogBinding.root)
            .setPositiveButton("Tiếp") { _, _ -> colorPickerDialog.show() }
            .setNegativeButton("Hủy", null)
            .create()
            .show()
    }

    private fun showEditCategoryDialog(category: Category) {
        val dialogBinding = DialogEditCategoryBinding.inflate(layoutInflater)

        // Điền thông tin hiện tại của danh mục vào dialog
        dialogBinding.etCategoryName.setText(category.name)
        when (category.type) {
            TransactionType.EXPENSE -> dialogBinding.rbExpense.isChecked = true
            TransactionType.INCOME -> dialogBinding.rbIncome.isChecked = true
        }

        // Tạo dialog
        AlertDialog.Builder(requireContext())
            .setTitle("Chỉnh sửa danh mục")
            .setView(dialogBinding.root)
            .setPositiveButton("Lưu") { _, _ ->
                val categoryName = dialogBinding.etCategoryName.text.toString()
                val type = if (dialogBinding.rbExpense.isChecked)
                    TransactionType.EXPENSE
                else
                    TransactionType.INCOME

                val updatedCategory = category.copy(
                    name = categoryName,
                    type = type
                )
                categoryViewModel.updateCategory(updatedCategory)
            }
            .setNegativeButton("Hủy", null)
            .create()
            .show()
    }

    private fun deleteCategory(category: Category) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa Danh Mục")
            .setMessage("Bạn có chắc muốn xóa danh mục này?")
            .setPositiveButton("Xóa") { _, _ -> categoryViewModel.deleteCategory(category) }
            .setNegativeButton("Hủy", null)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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

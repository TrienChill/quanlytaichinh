package com.example.qlctcanhan.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qlctcanhan.R
import com.example.qlctcanhan.databinding.DialogAddCategoryBinding
import com.example.qlctcanhan.databinding.FragmentCategoryManagementBinding
import com.example.qlctcanhan.model.Category
import com.example.qlctcanhan.model.TransactionType
import com.example.qlctcanhan.ui.adapters.CategoryAdapter
import com.example.qlctcanhan.viewmodel.CategoryViewModel
import androidx.lifecycle.lifecycleScope
import com.example.qlctcanhan.database.AppDatabase
import com.example.qlctcanhan.database.CategoryDao
import com.example.qlctcanhan.databinding.DialogEditCategoryBinding
import com.example.qlctcanhan.repository.CategoryRepository
import com.example.qlctcanhan.viewmodel.CategoryViewModelFactory
import kotlinx.coroutines.launch
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


class CategoryManagementFragment : Fragment() {
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentCategoryManagementBinding.inflate(inflater, container, false)

        // Lấy instance của CategoryDao từ AppDatabase
        val categoryDao = AppDatabase.getDatabase(requireContext()).categoryDao()

        // Khởi tạo CategoryRepository và ViewModelFactory
        val categoryRepository = CategoryRepository(categoryDao)
        val factory = CategoryViewModelFactory(categoryRepository)

        // Khởi tạo CategoryViewModel
        categoryViewModel = ViewModelProvider(this, factory).get(CategoryViewModel::class.java)

        // Thiết lập RecyclerView
        setupRecyclerView(binding)

        // Nút thêm danh mục
        binding.fabAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }

        return binding.root
    }
    
    private fun setupRecyclerView(binding: FragmentCategoryManagementBinding) {
        categoryAdapter = CategoryAdapter(
            onEditClick = { category -> showEditCategoryDialog(category) },
            onDeleteClick = { category -> deleteCategory(category) }
        )

        binding.recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categoryAdapter
        }

        // Observe danh sách danh mục
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
            .setNegativeButton("Hủy") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .attachAlphaSlideBar(true)
            .attachBrightnessSlideBar(true)

        AlertDialog.Builder(requireContext())
            .setTitle("Thêm Danh Mục Mới")
            .setView(dialogBinding.root)
            .setPositiveButton("Tiếp") { _, _ ->
                colorPickerDialog.show()
            }
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
            .setPositiveButton("Xóa") { _, _ ->
                categoryViewModel.deleteCategory(category)
            }
            .setNegativeButton("Hủy", null)
            .create()
            .show()
    }
}
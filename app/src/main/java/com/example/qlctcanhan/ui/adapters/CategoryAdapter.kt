package com.example.qlctcanhan.ui.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qlctcanhan.R
import com.example.qlctcanhan.databinding.ItemCategoryBinding
import com.example.qlctcanhan.model.Category
import com.example.qlctcanhan.model.TransactionType

class CategoryAdapter(
    private val onEditClick: (Category) -> Unit,
    private val onDeleteClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryViewHolder(
        private val binding: ItemCategoryBinding,
        private val onEditClick: (Category) -> Unit,
        private val onDeleteClick: (Category) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.apply {
                // Set category name
                tvCategoryName.text = category.name

                // Set category icon
                ivCategoryIcon.setImageResource(category.icon)

                // Set category color
                ivCategoryIcon.setColorFilter(category.color ?: root.context.getColor(R.color.md_theme_light_primary))

                // Set category type text
                tvCategoryType.text = when(category.type) {
                    TransactionType.EXPENSE -> "Chi phí"
                    TransactionType.INCOME -> "Thu nhập"
                }

                // Edit button click listener
                btnEdit.setOnClickListener {
                    onEditClick(category)
                }

                // Delete button click listener
                btnDelete.setOnClickListener {
                    onDeleteClick(category)
                }
            }
        }
    }

    // DiffUtil callback for efficient list updates
    class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }
}
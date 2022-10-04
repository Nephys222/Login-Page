package com.nilearning.palace.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nilearning.palace.databinding.BottomSheetItemBinding

class BottomSheetAdapter(
    private val items: List<BottomSheetItem>,
    private val listener: (index: Int) -> Unit
) : RecyclerView.Adapter<BottomSheetViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        val binding = BottomSheetItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BottomSheetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            title.text =
                if (item.currentValue != null) "${item.title} (${item.currentValue})" else item.title

            root.setOnClickListener {
                listener.invoke(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class BottomSheetViewHolder(
    val binding: BottomSheetItemBinding
) : RecyclerView.ViewHolder(binding.root)

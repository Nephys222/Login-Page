package com.nilearning.palace.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.nilearning.palace.databinding.BottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BottomSheet : BottomSheetDialogFragment() {
    private lateinit var items: List<BottomSheetItem>
    private lateinit var listener: (index: Int) -> Unit
    private lateinit var binding: BottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.optionsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.optionsRecycler.adapter = BottomSheetAdapter(items, listener)
    }

    fun setSimpleItems(titles: List<String>, listener: (index: Int) -> Unit) {
        this.items = titles.map { BottomSheetItem(it) }
        this.listener = { index ->
            listener.invoke(index)
            dialog?.dismiss()
        }
    }
}
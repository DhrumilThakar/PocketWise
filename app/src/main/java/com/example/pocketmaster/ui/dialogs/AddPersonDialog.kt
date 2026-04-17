package com.example.pocketmaster.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.pocketmaster.databinding.DialogAddPersonBinding
import com.example.pocketmaster.ui.viewmodel.FinanceViewModel

class AddPersonDialog : DialogFragment() {
    private var _binding: DialogAddPersonBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinanceViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddPersonBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnCancel.setOnClickListener { dismiss() }
        
        binding.btnSave.setOnClickListener {
            val name = binding.etPersonName.text.toString()
            if (name.isNotBlank()) {
                viewModel.addPerson(name)
                dismiss()
            } else {
                binding.tilPersonName.error = "Name is required"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
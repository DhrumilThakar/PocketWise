package com.example.pocketmaster.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.pocketmaster.databinding.DialogFriendInteractionBinding
import com.example.pocketmaster.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FriendInteractionDialog : DialogFragment() {
    private var _binding: DialogFriendInteractionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinanceViewModel by activityViewModels()

    private var personId: Int = -1
    private var personName: String = ""

    companion object {
        private const val ARG_PERSON_ID = "person_id"
        private const val ARG_PERSON_NAME = "person_name"

        fun newInstance(id: Int, name: String): FriendInteractionDialog {
            val fragment = FriendInteractionDialog()
            val args = Bundle().apply {
                putInt(ARG_PERSON_ID, id)
                putString(ARG_PERSON_NAME, name)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        personId = arguments?.getInt(ARG_PERSON_ID) ?: -1
        personName = arguments?.getString(ARG_PERSON_NAME) ?: ""
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogFriendInteractionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeBalance()
    }

    private fun setupUI() {
        binding.tvFriendName.text = personName
        
        binding.btnLend.setOnClickListener {
            handleTransaction(true)
        }

        binding.btnBorrow.setOnClickListener {
            handleTransaction(false)
        }
    }

    private fun observeBalance() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getBalanceForPerson(personId).collectLatest { balance ->
                val total = balance ?: 0.0
                binding.tvCurrentBalance.text = "Current Balance: ₹$total"
                
                when {
                    total > 0 -> binding.tvCurrentBalance.setTextColor(Color.parseColor("#4CAF50"))
                    total < 0 -> binding.tvCurrentBalance.setTextColor(Color.parseColor("#F44336"))
                    else -> binding.tvCurrentBalance.setTextColor(Color.GRAY)
                }
            }
        }
    }

    private fun handleTransaction(isLent: Boolean) {
        val amountStr = binding.etAmount.text.toString()
        val note = binding.etNote.text.toString()

        if (amountStr.isBlank()) {
            binding.etAmount.error = "Enter amount"
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.etAmount.error = "Invalid amount"
            return
        }

        viewModel.addDebt(personId, amount, isLent, note.ifBlank { if (isLent) "Lent money" else "Borrowed money" })
        
        val message = if (isLent) "Lent ₹$amount to $personName" else "Borrowed ₹$amount from $personName"
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
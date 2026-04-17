package com.example.pocketmaster.ui.debts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmaster.databinding.FragmentDebtsBinding
import com.example.pocketmaster.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DebtsFragment : Fragment() {
    private var _binding: FragmentDebtsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FinanceViewModel by activityViewModels()
    private lateinit var adapter: PersonAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDebtsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = PersonAdapter(viewModel)
        binding.rvPersons.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPersons.adapter = adapter
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allPersons.collectLatest { persons ->
                adapter.submitList(persons)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.debtSummary.collectLatest { summary ->
                binding.tvTotalLent.text = "₹${summary.first}"
                binding.tvTotalBorrowed.text = "₹${summary.second}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
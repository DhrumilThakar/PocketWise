package com.example.pocketmaster.ui.debts

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketmaster.data.model.Person
import com.example.pocketmaster.databinding.ItemPersonBinding
import com.example.pocketmaster.ui.dialogs.FriendInteractionDialog
import com.example.pocketmaster.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PersonAdapter(private val viewModel: FinanceViewModel) :
    ListAdapter<Person, PersonAdapter.PersonViewHolder>(PersonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val binding = ItemPersonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PersonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PersonViewHolder(private val binding: ItemPersonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(person: Person) {
            binding.tvPersonName.text = person.name
            
            // Observe balance for this person
            CoroutineScope(Dispatchers.Main).launch {
                viewModel.getBalanceForPerson(person.id).collectLatest { balance ->
                    val total = balance ?: 0.0
                    binding.tvBalance.text = "₹$total"
                    
                    when {
                        total > 0 -> {
                            binding.tvBalance.setTextColor(Color.parseColor("#4CAF50"))
                            binding.tvStatus.text = "You will get"
                        }
                        total < 0 -> {
                            binding.tvBalance.setTextColor(Color.parseColor("#F44336"))
                            binding.tvStatus.text = "You owe"
                        }
                        else -> {
                            binding.tvBalance.setTextColor(Color.GRAY)
                            binding.tvStatus.text = "Settled"
                        }
                    }
                }
            }
            
            binding.root.setOnClickListener {
                val context = it.context
                if (context is AppCompatActivity) {
                    FriendInteractionDialog.newInstance(person.id, person.name)
                        .show(context.supportFragmentManager, "FriendInteraction")
                }
            }
        }
    }

    class PersonDiffCallback : DiffUtil.ItemCallback<Person>() {
        override fun areItemsTheSame(oldItem: Person, newItem: Person): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Person, newItem: Person): Boolean = oldItem == newItem
    }
}
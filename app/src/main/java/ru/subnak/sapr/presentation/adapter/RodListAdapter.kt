package ru.subnak.sapr.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.subnak.sapr.R
import ru.subnak.sapr.databinding.RodCardviewBinding
import ru.subnak.sapr.domain.model.Rod
import ru.subnak.sapr.presentation.callback.RodCallback
import ru.subnak.sapr.presentation.viewholder.RodViewHolder

class RodListAdapter : ListAdapter<Rod, RodViewHolder>(RodCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RodViewHolder {
        val binding = RodCardviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RodViewHolder, position: Int) {
        val rod = getItem(position)
        holder.binding.tvRodTitle.text = posToString(position+1, holder.itemView.context)
        holder.binding.tvRodSquare.text = rod.square.toString()
        holder.binding.tvRodVoltage.text = rod.voltage.toString()
        holder.binding.tvLoadRunning.text = rod.loadRunning.toString()
        holder.binding.tvRodElasticModule.text = rod.elasticModule.toString()
    }

    private fun posToString(position: Int, context: Context): String {
        return String.format(
            context.getString(R.string.rods_number),
            position
        )
    }
}